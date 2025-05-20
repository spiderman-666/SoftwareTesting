package com.tongji.wordtrail.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostResponse {
    String id;
    String createdTime;
    String updatedTime;
    String title;
    String content;
    String userId;
    String username;
    List<String> filePaths;
    int commentCount;
    int voteCount;
    String state;
    String userAvatar;

    public PostResponse() {}

    public PostResponse(String id, String createdTime, String updatedTime, String title, String content, String userId, String username, List<String> filePaths, int commentCount, int voteCount, String state, String userAvatar) {
        this.id = id;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.username = username;
        this.filePaths = filePaths;
        this.commentCount = commentCount;
        this.voteCount = voteCount;
        this.state = state;
        this.userAvatar = userAvatar;
    }
}
