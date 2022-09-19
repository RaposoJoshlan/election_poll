package com.blockchain.voting.payload.response;

import com.blockchain.voting.model.Gender;

/**
 * Created on 14 Sept 2022
 */
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String id;
    private String username;

    private String userFirstName;

    private String userLastName;

    private Gender userGender;

    private String userLocation;


    public JwtResponse(String token, String id, String username, String userFirstName, String userLastName, Gender userGender, String userLocation) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userGender = userGender;
        this.userLocation = userLocation;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public Gender getUserGender() {
        return userGender;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public void setUserGender(Gender userGender) {
        this.userGender = userGender;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }
}