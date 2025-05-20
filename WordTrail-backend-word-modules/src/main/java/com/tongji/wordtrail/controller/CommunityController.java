package com.tongji.wordtrail.controller;

import cn.hutool.core.io.FileUtil;
import com.tongji.wordtrail.dto.*;
import com.tongji.wordtrail.model.Comment;
import com.tongji.wordtrail.model.Favourite;
import com.tongji.wordtrail.model.Post;
import com.tongji.wordtrail.service.CommunityService;
import com.tongji.wordtrail.service.OSSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/forum")
public class CommunityController {
    private static Logger logger = LoggerFactory.getLogger(CommunityController.class);
    private final CommunityService communityService;
    private final OSSService ossService;
    public CommunityController(CommunityService communityService, OSSService ossService) {
        this.communityService = communityService;
        this.ossService = ossService;
    }
    // 发布新帖子
    @PostMapping("/post/new")
    public ResponseEntity<?> createNewPost(
            @RequestParam("userId") String userId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) throws IOException {
        System.out.println("===> 接收到请求");

        // System.out.println(username);
        List<String> imagePaths = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                String url=ossService.uploadFile(file);
                imagePaths.add(url);
            }
        }

        // 处理标题和内容
        System.out.println("Title: " + title);
        System.out.println("Content: " + content);

        Post post = communityService.createPost(userId, title, content, imagePaths);
        return ResponseEntity.ok().body("{}");
    }


    // 获取帖子详情
    @GetMapping("/post/get")
    public ResponseEntity<?> getPost(@RequestParam String id) {
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("Post ID is required");
        }
        PostResponse post = communityService.getPostById(id);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", post);
        return ResponseEntity.ok().body(responseData);
    }
    // 删除帖子
    @DeleteMapping("/post/delete")
    public ResponseEntity<?> deletePost(@RequestBody(required = true) DeletePostRequest deletePostRequest) {
        boolean deleted = communityService.deletePostById(deletePostRequest.getId());

        if (deleted) {
            return ResponseEntity.ok().body("{}"); // 返回空 JSON
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // 获取帖子信息
    @GetMapping("/post/list")
    public ResponseEntity<?> getPostListByPage(@RequestParam(value = "page", defaultValue = "1") int page) {
        List<PostResponse> postList = communityService.getPostsByPage(page);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", postList);
        return ResponseEntity.ok().body(responseData);
    }
    // 随机获取10条帖子
    @GetMapping("/post/random")
    public ResponseEntity<?> getRandomPost() {
        List<PostResponse> postResponses = communityService.getRandomPostResponses();
        return ResponseEntity.ok().body(postResponses);
    }
    // 获取帖子总数
    @GetMapping("/post/count")
    public ResponseEntity<?> getPostCount() {
        long data = communityService.getPostCount();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", data);
        return ResponseEntity.ok().body(responseData);
    }
    // 返回某用户所有帖子中的第n页帖子
    @GetMapping("/post/user")
    public ResponseEntity<?> getPostUser(@RequestParam("uid") String uid, @RequestParam("page") int page) {
        List<PostResponse> postList = communityService.getPostsByUserPage(uid, page);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", postList);
        return ResponseEntity.ok().body(responseData);

    }
    // 返回关键字搜索后的帖子
    @GetMapping("/post/search")
    public ResponseEntity<?> getPostSearch(@RequestParam("keyword") String keyword, @RequestParam("page") int page) {
        List<PostResponse> postList = communityService.getPostSearch(keyword, page);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", postList);
        return ResponseEntity.ok().body(responseData);
    }
    // 返回关键字搜索后的帖子总数
    @GetMapping("/post/search-count")
    public ResponseEntity<?> getPostSearchCount(@RequestParam("keyword") String keyword) {
        int dataCount = communityService.getPostSearchCount(keyword);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", dataCount);
        return ResponseEntity.ok().body(responseData);
    }
    // 用户进行点赞，并返回帖子点赞总数
    @PostMapping("/post/vote")
    public ResponseEntity<?> getPostVote(@RequestParam("postId") String postId, @RequestParam("userId") String userId, @RequestParam("upvote") String upvote) {
        int count = communityService.getPostVoteCount(postId, userId, upvote);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", count);
        return ResponseEntity.ok().body(responseData);
    }
    // 返回用户是否点赞
    @GetMapping("/post/isVoted")
    public ResponseEntity<?> getPostIsVoted(@RequestParam("postId") String postId, @RequestParam("userId") String userId) {
        int dataCount = communityService.getPostVote(postId, userId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", dataCount);
        return ResponseEntity.ok().body(responseData);
    }
    // 返回总点赞数量
    @GetMapping("/post/voteCount")
    public ResponseEntity<?> getPostVoteCount(@RequestParam("postId") String postId) {
        int dataCount = communityService.getPostVoteCount(postId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", dataCount);
        return ResponseEntity.ok().body(responseData);
    }
    // 收藏帖子
    @PostMapping("/post/favorite")
    public ResponseEntity<?> addFavourite(@RequestParam("postId") String postId, @RequestParam("userId") String userId) {
        communityService.addFavourite(postId, userId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", 200);
        return ResponseEntity.ok().body(responseData);
    }
    // 取消收藏
    @DeleteMapping("/post/deleteFavorite")
    public ResponseEntity<?> deleteFavorite(@RequestParam("postId") String postId, @RequestParam("userId") String userId) {
        Map<String, Object> responseData = new HashMap<>();
        if (communityService.deleteFavourite(postId, userId)) {
            responseData.put("code", 200);
            responseData.put("msg", null);
            responseData.put("data", 200);
            return ResponseEntity.ok().body(responseData);
        }
        else {
            responseData.put("code", 404);
            responseData.put("msg", 404);
            return ResponseEntity.ok().body(responseData);
        }

    }
    // 罗列所有的收藏
    @PostMapping("/post/listFavorite")
    public ResponseEntity<?> getFavoriteList(@RequestParam("userId") String userId) {
        List<Favourite> favoriteList = communityService.getFavoriteList(userId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", favoriteList);
        return ResponseEntity.ok().body(responseData);
    }
    // 返回是否收藏帖子
    @PostMapping("/post/isFavorite")
    public ResponseEntity<?> isFavorite(@RequestParam("postId") String postId, @RequestParam("userId") String userId) {
        boolean result = communityService.isFavorite(postId, userId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", result);
        return ResponseEntity.ok().body(responseData);
    }
    // 返回某帖子收藏数量
    @GetMapping("/post/countFavorite")
    public ResponseEntity<?> countFavorite(@RequestParam("postId") String postId) {
        List<Favourite> favoriteList = communityService.getFavoriteListByPostId(postId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data",favoriteList.size());
        return ResponseEntity.ok().body(responseData);
    }
    // 添加评论
    @PostMapping("/comment/post")
    public ResponseEntity<?> addComment(@RequestBody CommentRequest request) {
        communityService.addComment(request);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", "评论成功");
        return ResponseEntity.ok().body(responseData);
    }
    // 删除评论
    @DeleteMapping("/comment/delete")
    public ResponseEntity<?> deleteComment(@RequestParam("commentId") String commentId) {
        communityService.deleteComment(commentId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", "删除成功");
        return ResponseEntity.ok().body(responseData);
    }
    // 查找帖子中的所有评论
    @GetMapping("/comment/list")
    public ResponseEntity<?> getCommentList(@RequestParam("postId") String postId) {
        List<Comment> comments = communityService.getCommentList(postId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", comments);
        return ResponseEntity.ok().body(responseData);
    }
    @GetMapping("/comment/{id}")
    public ResponseEntity<?> getComment(@PathVariable("id") String commentId) {
        Comment comment = communityService.getComment(commentId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", comment);
        return ResponseEntity.ok().body(responseData);
    }
    // 举报帖子
    @PostMapping("/post/report")
    public ResponseEntity<?> reportPost(@RequestBody Map<String, Object> requestData) {
        String postId = (String) requestData.get("id");
        String message = (String) requestData.get("message");
        communityService.reportPost(postId, message);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", "举报成功");
        return ResponseEntity.ok().body(responseData);
    }
    // 查找某用户发布的全部帖子
    @GetMapping("/post/searchByUser")
    public ResponseEntity<?> searchByUser(@RequestParam("userId") String userId) {
        List<PostResponse> postList = communityService.getPostSearchUser(userId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", postList);
        return ResponseEntity.ok().body(responseData);
    }

}
