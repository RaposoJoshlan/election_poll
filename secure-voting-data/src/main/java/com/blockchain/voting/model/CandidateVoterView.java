package com.blockchain.voting.model;

import com.blockchain.voting.model.streams.RevisionData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created on 01 Sep 2022
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateVoterView implements RevisionData {

    @JsonProperty("FullName")
    private String fullName;

    @JsonProperty("Election")
    private String election;

}
