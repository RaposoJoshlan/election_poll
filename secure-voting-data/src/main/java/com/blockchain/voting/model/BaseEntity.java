package com.blockchain.voting.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created on 12 Aug 2022
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {
    @JsonProperty("id")
    private String id;

    @Override
    public String toString() {
        return id;
    }
}

