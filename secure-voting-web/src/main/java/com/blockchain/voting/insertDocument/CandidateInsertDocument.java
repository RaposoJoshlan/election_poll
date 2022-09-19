package com.blockchain.voting.insertDocument;

import com.amazon.ion.IonStruct;
import com.blockchain.voting.bootstrap.SampleData;
import com.blockchain.voting.config.QLDBConfig;
import com.blockchain.voting.model.Candidate;
import com.blockchain.voting.model.consts.Constants;
import com.blockchain.voting.services.ScanTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.blockchain.voting.QLDBOperation.insertDocument.InsertDocument.insertDocument;

/**
 * Created on 10/08/2022
 */

public class CandidateInsertDocument {

    public final Logger log = LoggerFactory.getLogger(this.getClass());

    public void updateCandidateID(List<String> documentIDs, final List<Candidate> candidates) {
        for (int i = 0; i < documentIDs.size(); ) {
            Candidate candidate = SampleData.CANDIDATES.get(i);
            candidates.add(SampleData.updateCandidateIDVotes(candidate, documentIDs.get(i)));
            i++;
            if (i == SampleData.CANDIDATES.size()) {
                break;
            }
        }
    }

    public void insert() {
        final List<Candidate> newCandidates = new ArrayList<>();
        List<Candidate> candidates = new ArrayList<>();

        QLDBConfig.getDriver().execute(txn -> {
            List<String> documentIds = insertDocument(txn, Constants.ELECTION_TABLE_NAME, SampleData.ELECTIONS);

            final String candidateQuery = String.format("SELECT * FROM %s", Constants.CANDIDATE_TABLE_NAME);
            List<IonStruct> existingCandidateDocuments = ScanTable.toIonStructs(txn.execute(candidateQuery));

            if (existingCandidateDocuments.size() != 0) {
                updateCandidateID(documentIds, newCandidates);
                for (Candidate candidate : newCandidates) {
                    if (StringUtils.hasLength(candidate.getFirstName())
                            && candidate.checkIfCandidateAlreadyExists(existingCandidateDocuments, candidate.getFirstName().toLowerCase(),
                            candidate.getLastName().toLowerCase(), candidate.getConstituency().toLowerCase(),
                            candidate.getElection().getElectionParty().toLowerCase(), true) != null) {
                        log.info("Record already exist");
                    } else {
                        candidates.add(candidate);
                    }
                }
                if (candidates.size() != 0) {
                    insertDocument(txn, Constants.CANDIDATE_TABLE_NAME, Collections.unmodifiableList(candidates));
                }
            } else {
                updateCandidateID(documentIds, newCandidates);
                insertDocument(txn, Constants.CANDIDATE_TABLE_NAME, Collections.unmodifiableList(newCandidates));
            }
        });
        log.info("Documents inserted successfully!");
    }
}