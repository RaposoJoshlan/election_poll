package com.blockchain.voting.model;

import com.blockchain.voting.model.streams.RevisionData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Set;

/**
 * Created on 01 Sep 2022
 */

@Getter
@Setter
@NoArgsConstructor
public class Voter extends User implements RevisionData {

    private String id;
    @JsonProperty("VoterFirstName")
    private String firstName;

    @JsonProperty("VoterLastName")
    private String lastName;

    @JsonProperty("VoterGender")
    private Gender gender;

    @JsonProperty("VoterLocation")
    private String location;

    public Voter(String username, String password, String firstName, String lastName, Gender gender, String location) {
        super(username, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.location = location;
    }

    public Voter(String id, String firstName, String lastName, Gender gender, String location) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.location = location;
    }

    @Override
    public String toString() {
        return "Voter{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", location='" + location + '\'' +
                '}';
    }
}
