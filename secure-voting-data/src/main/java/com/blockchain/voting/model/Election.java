package com.blockchain.voting.model;

import com.blockchain.voting.model.streams.RevisionData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created on 10/08/2022
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Election implements RevisionData {

    @JsonProperty("ElectionParty")
    private String electionParty;

    @Override
    public String toString() {
        return electionParty;
    }
}

