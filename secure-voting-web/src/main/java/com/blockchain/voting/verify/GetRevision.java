package com.blockchain.voting.verify;

import com.amazon.ion.*;
import com.amazon.ion.system.IonReaderBuilder;
import com.amazon.ion.system.IonSystemBuilder;
import com.amazonaws.services.qldb.AmazonQLDB;
import com.amazonaws.services.qldb.model.GetDigestResult;
import com.amazonaws.services.qldb.model.GetRevisionRequest;
import com.amazonaws.services.qldb.model.GetRevisionResult;
import com.amazonaws.services.qldb.model.ValueHolder;
import com.blockchain.voting.QLDBOperation.createLedger.CreateSecureVotingSysLedger;
import com.blockchain.voting.config.QLDBConfig;
import com.blockchain.voting.model.Candidate;
import com.blockchain.voting.model.consts.Constants;
import com.blockchain.voting.model.verifyDocuments.BlockAddress;
import com.blockchain.voting.model.verifyDocuments.QldbRevision;
import com.blockchain.voting.model.verifyDocuments.QldbStringUtils;
import com.blockchain.voting.model.verifyDocuments.Verifier;
import com.blockchain.voting.services.ScanTable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.qldb.QldbDriver;
import software.amazon.qldb.Result;
import software.amazon.qldb.TransactionExecutor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.blockchain.voting.model.consts.AWSQldb.intoJson;

/**
 * Verify the integrity of a document revision in a QLDB ledger.
 *
 * This code expects that you have AWS credentials setup per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public final class GetRevision {
    public static final Logger log = LoggerFactory.getLogger(GetRevision.class);
    public static AmazonQLDB client = CreateSecureVotingSysLedger.getClient();
    private static final IonSystem SYSTEM = IonSystemBuilder.standard().build();

    private GetRevision() { }

    public static void main(String... args) throws Exception {

        final ObjectMapper objectMapper = new ObjectMapper();

        QLDBConfig.getDriver().execute(txn -> {
            final String queryToGetCandidatesById
                    = String.format("SELECT * FROM %s c", Constants.CANDIDATE_TABLE_NAME);
            List<IonStruct> documents2 = ScanTable.toIonStructs(txn.execute(queryToGetCandidatesById));

            try {
                List<Candidate> candidates = objectMapper.readValue(intoJson(documents2), new TypeReference<>() {});

                for (Candidate candidate: candidates) {
                    final String id = candidate.getGovId();
                    verifyCandidateRegistration(QLDBConfig.getDriver(), Constants.LEDGER_NAME, id);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    /**
     * Verify each version of the registration for the given VIN.
     *
     * @param driver
     *              A QLDB driver.
     * @param ledgerName
     *              The ledger to get digest from.
     * @param candidateGovId
     *              VIN to query the revision history of a specific registration with.
     * @throws Exception if failed to verify digests.
     * @throws AssertionError if document revision verification failed.
     */
    public static void verifyCandidateRegistration(final QldbDriver driver, final String ledgerName, final String candidateGovId)
            throws Exception {
        log.info(String.format("Let's verify the candidate with id=%s, in ledger=%s.", candidateGovId, ledgerName));

        try {
            log.info("First, let's get a digest.");
            GetDigestResult digestResult = GetDigest.getDigest(ledgerName);

            ValueHolder digestTipAddress = digestResult.getDigestTipAddress();
            byte[] digestBytes = Verifier.convertByteBufferToByteArray(digestResult.getDigest());

            log.info("Got a ledger digest. Digest end address={}, digest={}.",
                QldbStringUtils.toUnredactedString(digestTipAddress),
                Verifier.toBase64(digestBytes));

            log.info(String.format("Next, let's query the candidate with GovId=%s. "
                    + "Then we can verify each version of the candidate.", candidateGovId));
            List<IonStruct> documentsWithMetadataList = new ArrayList<>();
            driver.execute(txn -> {
                documentsWithMetadataList.addAll(queryCandidatesId(txn, candidateGovId));
            });
            log.info("Candidates queried successfully!");

            log.info(String.format("Found %s revisions of the candidate with GovId=%s.",
                    documentsWithMetadataList.size(), candidateGovId));

            for (IonStruct ionStruct : documentsWithMetadataList) {

                QldbRevision document = QldbRevision.fromIon(ionStruct);
                log.info(String.format("Let's verify the document: %s", document));

                log.info("Let's get a proof for the document.");
                GetRevisionResult proofResult = getRevision(
                        ledgerName,
                        document.getMetadata().getId(),
                        digestTipAddress,
                        document.getBlockAddress()
                );

                final IonValue proof = Constants.MAPPER.writeValueAsIonValue(proofResult.getProof());
                final IonReader reader = IonReaderBuilder.standard().build(proof);
                reader.next();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IonWriter writer = SYSTEM.newBinaryWriter(baos);
                writer.writeValue(reader);
                writer.close();
                baos.flush();
                baos.close();
                byte[] byteProof = baos.toByteArray();

                log.info(String.format("Got back a proof: %s", Verifier.toBase64(byteProof)));

                boolean verified = Verifier.verify(
                        document.getHash(),
                        digestBytes,
                        proofResult.getProof().getIonText()
                );

                if (!verified) {
                    throw new AssertionError("Document revision is not verified!");
                } else {
                    log.info("Success! The document is verified");
                }

                byte[] alteredDigest = Verifier.flipRandomBit(digestBytes);
                log.info(String.format("Flipping one bit in the digest and assert that the document is NOT verified. "
                        + "The altered digest is: %s", Verifier.toBase64(alteredDigest)));
                verified = Verifier.verify(
                        document.getHash(),
                        alteredDigest,
                        proofResult.getProof().getIonText()
                );

                if (verified) {
                    throw new AssertionError("Expected document to not be verified against altered digest.");
                } else {
                    log.info("Success! As expected flipping a bit in the digest causes verification to fail.");
                }

                byte[] alteredDocumentHash = Verifier.flipRandomBit(document.getHash());
                log.info(String.format("Flipping one bit in the document's hash and assert that it is NOT verified. "
                        + "The altered document hash is: %s.", Verifier.toBase64(alteredDocumentHash)));
                verified = Verifier.verify(
                        alteredDocumentHash,
                        digestBytes,
                        proofResult.getProof().getIonText()
                );

                if (verified) {
                    throw new AssertionError("Expected altered document hash to not be verified against digest.");
                } else {
                    log.info("Success! As expected flipping a bit in the document hash causes verification to fail.");
                }
            }

        } catch (Exception e) {
            log.error("Failed to verify digests.", e);
            throw e;
        }

        log.info(String.format("Finished verifying the candidate with GovId=%s in ledger=%s.", candidateGovId, ledgerName));
    }

    /**
     * Get the revision of a particular document specified by the given document ID and block address.
     *
     * @param ledgerName
     *              Name of the ledger containing the document.
     * @param documentId
     *              Unique ID for the document to be verified, contained in the committed view of the document.
     * @param digestTipAddress
     *              The latest block location covered by the digest.
     * @param blockAddress
     *              The location of the block to request.
     * @return the requested revision.
     */
    public static GetRevisionResult getRevision(final String ledgerName, final String documentId,
                                                final ValueHolder digestTipAddress, final BlockAddress blockAddress) {
        try {
            GetRevisionRequest request = new GetRevisionRequest()
                    .withName(ledgerName)
                    .withDigestTipAddress(digestTipAddress)
                    .withBlockAddress(new ValueHolder().withIonText(Constants.MAPPER.writeValueAsIonValue(blockAddress)
                            .toString()))
                    .withDocumentId(documentId);
            return client.getRevision(request);
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    /**
     * Query the registration history for the given VIN.
     *
     * @param txn
     *              The {@link TransactionExecutor} for lambda execute.
     * @param id
     *              The unique VIN to query.
     * @return a list of {@link IonStruct} representing the registration history.
     * @throws IllegalStateException if failed to convert parameters into {@link IonValue}
     */
    public static List<IonStruct> queryCandidatesId(final TransactionExecutor txn, final String id) {
        log.info(String.format("Let's query the 'Candidates' table for GovId: %s...", id));
        log.info("Let's query the 'Candidates' table for id: {}...", id);
        final String query = String.format("SELECT * FROM _ql_committed_%s WHERE data.GovId = ?", Constants.CANDIDATE_TABLE_NAME);
        try {
            final List<IonValue> parameters = Collections.singletonList(Constants.MAPPER.writeValueAsIonValue(id));
            final Result result = txn.execute(query, parameters);
            List<IonStruct> list = ScanTable.toIonStructs(result);
            log.info(String.format("Found %d document(s)!", list.size()));
            return list;
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }
}
