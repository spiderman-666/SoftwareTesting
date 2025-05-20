package com.tongji.wordtrail.dto;

import lombok.Data;

@Data
public class UserResponse {
    int id;
    String userId;
    String username;
    int postCount;
    int commentCount;
    int likeCount;
    int dislikeCount;
    int favoriteCount;
    String state;
    public UserResponse() {

    }

}
