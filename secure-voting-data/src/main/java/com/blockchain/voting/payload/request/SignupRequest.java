package com.blockchain.voting.payload.request;

import com.blockchain.voting.model.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String username;

    private String password;

    @JsonProperty("VoterFirstName")
    private String VoterFirstName;

    @JsonProperty("VoterLastName")
    private String VoterLastName;

    @JsonProperty("VoterGender")
    private Gender VoterGender;

    @JsonProperty("VoterLocation")
    private String VoterLocation;

    private Set<String> role;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getVoterFirstName() {
        return VoterFirstName;
    }

    public String getVoterLastName() {
        return VoterLastName;
    }

    public Gender getVoterGender() {
        return VoterGender;
    }

    public String getVoterLocation() {
        return VoterLocation;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVoterFirstName(String voterFirstName) {
        this.VoterFirstName = voterFirstName;
    }

    public void setVoterLastName(String voterLastName) {
        this.VoterLastName = voterLastName;
    }

    public void setVoterGender(Gender voterGender) {
        this.VoterGender = voterGender;
    }

    public void setVoterLocation(String voterLocation) {
        this.VoterLocation = voterLocation;
    }

    public Set<String> getRole() {
        return role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "SignupRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", VoterFirstName='" + VoterFirstName + '\'' +
                ", VoterLastName='" + VoterLastName + '\'' +
                ", VoterGender=" + VoterGender +
                ", VoterLocation='" + VoterLocation + '\'' +
                '}';
    }
}
