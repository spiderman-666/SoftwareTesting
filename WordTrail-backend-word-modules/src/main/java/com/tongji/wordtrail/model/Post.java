package com.tongji.wordtrail.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Document(collection = "posts") // MongoDB 的集合
public class Post {

    @Id
    private String id; // MongoDB 使用 String 作为主键
    private String title;
    private String content;
    private List<String> filePaths; // 存储文件路径列表
    private LocalDateTime createdAt;
    private LocalDateTime updatedTime;
    private String userId;
    private int commentCount;
    private int voteCount;
    private int page;
    private String state;
    private String userAvatar;
    private String message;
    private int like;
    private int dislike;
    public Post() {
        this.commentCount = 0;
        this.voteCount = 0;
    }

    public Post(String title, String content, LocalDateTime createdAt, LocalDateTime updatedTime, String userId, List<String> filePaths, int commentCount, int voteCount, int page, String userAvatar) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedTime = updatedTime;
        this.userId = userId;
        this.filePaths = filePaths;
        this.commentCount = commentCount;
        this.voteCount = voteCount;
        this.page = page;
        this.state = "normal";
        this.userAvatar = userAvatar;
        this.like = 0;
        this.dislike = 0;
    }

    // Getter 和 Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public void setFilePaths(List<String> filePaths) {
        this.filePaths = filePaths;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public int getCommentCount() {
        return commentCount;
    }
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    public int getVoteCount() {
        return voteCount;
    }
    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getUserAvatar() {
        return userAvatar;
    }
    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public int getLike() {
        return like;
    }
    public void setLike(int like) {
        this.like = like;
    }
    public int getDislike() {
        return dislike;
    }
    public void setDislike(int dislike) {
        this.dislike = dislike;
    }
}

