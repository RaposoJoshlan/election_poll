package com.blockchain.voting.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created on 06 Sep 2022
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GovID {
    @JsonProperty("GovId")
    private String govId;

    @Override
    public String toString() {
        return govId;
    }
}
