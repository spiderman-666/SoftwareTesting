package com.tongji.wordtrail.model;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import javax.persistence.Id;

@Document(collection = "deletePost") // MongoDB 的集合
public class DeletePost {
    @Id
    private String id;
    private String message;

    public DeletePost(String id, String message) {
        this.id = id;
        this.message = message;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
