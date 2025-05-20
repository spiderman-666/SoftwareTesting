package com.tongji.wordtrail.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.Id;


@Document(collection = "userState")
public class UserState {
    @Id
    private String userId;
    private String state;
    private String message;
    public UserState(String userId, String state, String message) {
        this.userId = userId;
        this.state = state;
        this.message = message;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
