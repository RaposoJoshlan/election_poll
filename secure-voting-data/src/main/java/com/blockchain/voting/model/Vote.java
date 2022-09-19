package com.blockchain.voting.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Created on 01 Sep 2022
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vote /*extends BaseEntity */{

    @JsonProperty("VotedCandidate")
    private Candidate candidate;

    @JsonProperty("VoterId")
    private String voterId;

    @JsonProperty("VoterFullName")
    private String voterFullName;

    @JsonProperty("VoterGender")
    private Gender voterGender;

    @JsonProperty("VoterLocation")
    private String voterLocation;
}
