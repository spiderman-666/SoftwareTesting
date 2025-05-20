package com.tongji.wordtrail.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "comment") // MongoDB 的集合
public class Comment {
    @Id
    private String id;
    private String postId;
    private String content;
    private String userId;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String parentComment;
    private String state;
    private String message;
    public Comment(String postId, String content, String userId, LocalDateTime createdTime, LocalDateTime updatedTime, String parentComment) {
        this.postId = postId;
        this.content = content;
        this.userId = userId;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.parentComment = parentComment;
        this.state = "normal";
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPostId() {
        return postId;
    }
    public void setPostId(String postId) {
        this.postId = postId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
    public String getParentComment() {
        return parentComment;
    }
    public void setParentComment(String parentComment) {
        this.parentComment = parentComment;
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
