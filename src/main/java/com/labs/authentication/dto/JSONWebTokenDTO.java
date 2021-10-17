package com.labs.authentication.dto;

public class JSONWebTokenDTO {

    private String token;

    public JSONWebTokenDTO(String token) {
        this.token = token;
    }

    public JSONWebTokenDTO() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
