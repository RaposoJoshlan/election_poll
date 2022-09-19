package com.blockchain.voting.services;

import com.amazon.ion.IonStruct;
import com.amazon.ion.IonValue;
import com.blockchain.voting.config.QLDBConfig;
import com.blockchain.voting.customExceptions.CustomNotFoundException;
import com.blockchain.voting.model.Candidate;
import com.blockchain.voting.model.Vote;
import com.blockchain.voting.model.Voter;
import com.blockchain.voting.model.consts.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.qldb.TransactionExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.blockchain.voting.QLDBOperation.insertDocument.InsertDocument.insertDocument;
import static com.blockchain.voting.model.consts.AWSQldb.getDocumentIdsFromDmlResult;
import static com.blockchain.voting.model.consts.AWSQldb.intoJson;

/**
 * Created on 11 Aug 2022
 */

@Service
@Profile("qldb")
public class CandidateServiceImpl implements CandidateService {
    public final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<IonStruct> findAll(final String tableName) {

        return (QLDBConfig.getDriver().execute(txn -> {
            log.info("Scanning '{}'...", tableName);
            final String scanTable = String.format("SELECT * FROM %s", tableName);
            List<IonStruct> documents = ScanTable.toIonStructs(txn.execute(scanTable));
            log.info("Scan successful!");
            return documents;
        }));
    }

    @Override
    public List<Candidate> findById(String id, String tableName) {
        return
                (
                        QLDBConfig.getDriver().execute(txn -> {
                            log.info("Scanning '{}'...", tableName);
//                            final String ids = String.format("SELECT GovId FROM %s", tableName);
//                            List<IonStruct> documentIds = ScanTable.toIonStructs(txn.execute(ids));
//
                            final ObjectMapper objectMapper = new ObjectMapper();
                            try {
//                                List<GovID> docIds = objectMapper.readValue(intoJson(documentIds), new TypeReference<>() {
//                                });
                                try {
                                    final String queryToGetCandidatesById
                                            = String.format("SELECT * FROM %s c WHERE c.GovId = '%s'", tableName, id);
                                    List<IonStruct> documents2 = ScanTable.toIonStructs(txn.execute(queryToGetCandidatesById));

                                    return objectMapper.readValue(intoJson(documents2), new TypeReference<>() {
                                    });
                                } catch (IndexOutOfBoundsException fe) {
                                    String message = String.format("Candidate with id '%s' not found. Please enter valid id", id);
                                    throw new CustomNotFoundException(message);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            log.info("Unable to complete request");
                            return null;
                        })
                );
    }

    public Vote voteByCandidateId(String id, String tableName, Vote voteDetails) {

        List<Candidate> candidates = Collections.unmodifiableList(findById(id, tableName));
        final List<Vote> newVotes = new ArrayList<>();
        Vote newVote = new Vote();

        QLDBConfig.getDriver().execute(txn -> {
            log.info("Scanning '{}'...", tableName);
            final ObjectMapper objectMapper = new ObjectMapper();

            try {

                final String queryToGetAllVotes
                        = String.format("SELECT * FROM %s v WHERE v.VoterId = '%s'", Constants.VOTE_TABLE_NAME, voteDetails.getVoterId());
                List<IonStruct> voteDocs = ScanTable.toIonStructs(txn.execute(queryToGetAllVotes));
                List<Vote> allVotes = objectMapper.readValue(intoJson(voteDocs), new TypeReference<>() {
                });

                if (allVotes.size() != 0) {
                    for (Vote vote : allVotes) {
                        if (vote.getVoterId().equals(voteDetails.getVoterId())) {
                            throw new CustomNotFoundException("Already Voted. Cannot Vote Again. Please logout");
                        } else {
                            voting(candidates, newVote, voteDetails, newVotes);
                        }
                    }
                } else {
                    voting(candidates, newVote, voteDetails, newVotes);
                }

                if (newVotes.size() != 0) {
                    insertDocument(txn, Constants.VOTE_TABLE_NAME, Collections.unmodifiableList(newVotes));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("Scan successful!");
        });

        return newVote;
    }

    public void voting(List<Candidate> candidates, Vote newVote, Vote voteDetails, List<Vote> newVotes) {
        for (Candidate candidate : candidates) {
            if (voteDetails.getCandidate().getGovId().equals(candidate.getGovId())) {
                newVote.setCandidate(candidate);
                newVote.setVoterId(voteDetails.getVoterId());
                newVote.setVoterFullName(voteDetails.getVoterFullName());
                newVote.setVoterGender(voteDetails.getVoterGender());
                newVote.setVoterLocation(voteDetails.getVoterLocation());

                newVotes.add(newVote);
            }
        }
    }
}
