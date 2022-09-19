package com.blockchain.voting.services;

import com.amazon.ion.IonStruct;
import com.blockchain.voting.config.QLDBConfig;
import com.blockchain.voting.model.ERole;
import com.blockchain.voting.model.Role;
import com.blockchain.voting.model.Voter;
import com.blockchain.voting.model.consts.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.blockchain.voting.QLDBOperation.insertDocument.InsertDocument.insertDocument;
import static com.blockchain.voting.model.consts.AWSQldb.intoJson;

/**
 * Created on 12 Sep 2022
 */

@Service
public class VoterServiceImpl implements VoterService {
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
    public List<Voter> findByUsername(String username, String tableName) {
        return
                (
                        QLDBConfig.getDriver().execute(txn -> {
                            log.info("Scanning '{}'...", tableName);

                            final ObjectMapper objectMapper = new ObjectMapper();
                            try {
                                try {
                                    final String queryToGetVoterByUsername
                                            = String.format("SELECT * FROM %s v WHERE v.username = '%s'", tableName, username);
                                    List<IonStruct> documents2 = ScanTable.toIonStructs(txn.execute(queryToGetVoterByUsername));

                                    return objectMapper.readValue(intoJson(documents2), new TypeReference<>() {
                                    });
                                } catch (UsernameNotFoundException nFE) {
                                    String message = String.format("User Not Found with username: %s" + username);
                                    throw new UsernameNotFoundException(message);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            log.info("Scan successful!");
                            return null;
                        })
                );
    }

/*    @Override
    public List<Voter> findById(String id, String tableName) {
        return
                (
                        QLDBConfig.getDriver().execute(txn -> {
                            log.info("Scanning '{}'...", tableName);

                            final ObjectMapper objectMapper = new ObjectMapper();
                            try {
                                try {
                                    final String queryToGetVoterByUsername
                                            = String.format("SELECT * FROM %s v WHERE v.id = '%s'", tableName, id);
                                    List<IonStruct> documents2 = ScanTable.toIonStructs(txn.execute(queryToGetVoterByUsername));

                                    return objectMapper.readValue(intoJson(documents2), new TypeReference<>() {
                                    });
                                } catch (IndexOutOfBoundsException fe) {
                                    String message = String.format("Voter with login '%s' not found. Please enter valid id to login", id);
                                    throw new CustomNotFoundException(message);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            log.info("Scan successful!");
                            return null;
                        })
                );
    }*/

    @Override
    public Voter addVoter(Voter voterDetails) {
        Voter voter = new Voter();
        final List<Voter> newVoter = new ArrayList<>();

        //voter.setUsername(generateUniqueStringForGivenCandidateAsID(voterDetails.getFirstName(), voterDetails.getLastName(), voterDetails.getGender().toString(), voterDetails.getLocation()));
        voter.setFirstName(voterDetails.getFirstName());
        voter.setLastName(voterDetails.getLastName());
        voter.setGender(voterDetails.getGender());
        voter.setLocation(voterDetails.getLocation());
        voter.setId(voterDetails.getId());
        voter.setUsername(voterDetails.getUsername());
        voter.setPassword(voterDetails.getPassword());
        newVoter.add(voter);

        QLDBConfig.getDriver().execute(txn -> {
            insertDocument(txn, Constants.VOTER_TABLE_NAME, Collections.unmodifiableList(newVoter));
            log.info("Inserting some documents in the {} table...", Constants.VOTER_TABLE_NAME);
        });
        return voter;
    }

    @Override
    public Boolean existsByUsername(String username) throws IOException {
        List<IonStruct> usernames = findAll(Constants.VOTER_TABLE_NAME);

        final ObjectMapper objectMapper = new ObjectMapper();
        List<Voter> docIds = objectMapper.readValue(intoJson(usernames), new TypeReference<>() {});

        for (Voter voter: docIds) {
            if (voter.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Optional<Role> findByName(ERole name) {
        Role role = new Role();
        return Optional.ofNullable(role);
    }
}
