package com.tongji.wordtrail.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String admin, String number) {
    }
}