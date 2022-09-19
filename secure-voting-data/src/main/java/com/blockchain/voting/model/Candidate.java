package com.blockchain.voting.model;

import com.amazon.ion.IonStruct;
import com.blockchain.voting.model.streams.RevisionData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.io.IOException;
import java.util.List;

import static com.blockchain.voting.model.consts.AWSQldb.intoJson;


/**
 * Created on 10/08/2022
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityScan
public class Candidate extends Person implements RevisionData {

    @JsonProperty("GovId")
    private String govId;

    @JsonProperty("Election")
    private Election election;

    @JsonProperty("Gender")
    private Gender gender;

    @JsonProperty("Constituency")
    private String constituency;

    @Builder
    public Candidate(String id, String firstName, String lastName, String govId, Election election, Gender gender, String constituency) {
        super(id, firstName, lastName);
        this.govId = govId;
        this.election = election;
        this.gender = gender;
        this.constituency = constituency;
    }

    @Builder
    public Candidate(String firstName, String lastName, String govId, Election election, Gender gender, String constituency) {
        super(firstName, lastName);
        this.election = election;
        this.gender = gender;
        this.constituency = constituency;
        this.govId = govId;
    }

    @JsonIgnore
    public boolean isNew() {
        return this.getId() == null;
    }

    public Candidate checkIfCandidateAlreadyExists(List<IonStruct> documents, String newCandidateFirstName, String newCandidateLastName, String newCandidateConstituency,
                                                   String newCandidateElectionParty, boolean ignoreNew) {

        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<Candidate> candidates = objectMapper.readValue(intoJson(documents), new TypeReference<>() {
            });

            for (Candidate candidate : candidates) {
                if (!ignoreNew || !candidate.isNew()) {
                    String existingCandidateFirstName = candidate.getFirstName().toLowerCase();
                    String existingCandidateLastName = candidate.getLastName().toLowerCase();
                    String existingCandidateConstituency = candidate.getConstituency().toLowerCase();
                    String existingCandidateElectionParty = candidate.getElection().getElectionParty().toLowerCase();

                    if (newCandidateFirstName.equals(existingCandidateFirstName) && newCandidateLastName.equals(existingCandidateLastName) && newCandidateElectionParty.equals(existingCandidateElectionParty)) {
                        return candidate;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString() {
        return "Candidate {" +
                "FirstName: " + getFirstName() +
                ", LastName: " + getLastName() +
                ", GovId: " + getGovId() +
                ", ElectionParty: " + getElection().getElectionParty() +
                ", Gender: " + gender +
                ", Constituency: '" + constituency + '\'' +
                '}';
    }


}

// Candidates can have same name and can be from same constituency but cannot be from same election party
// If new candidate name is similar to exiting candidate, it is Ok and
