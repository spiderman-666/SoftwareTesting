package com.tongji.wordtrail.dto;
import lombok.Data;

@Data
public class AdminResetPasswordRequest {
    private String account;
    private String key;
    private String newpassword;

    public AdminResetPasswordRequest(String key123, String newpass) {
    }
}
