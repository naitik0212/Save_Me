package com.example.group17.medaas.API.model;

/**
 * Created by Samarth on 4/30/2018.
 */

public class Token {
    private String deviceToken;

    public Token(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public Token() {
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
