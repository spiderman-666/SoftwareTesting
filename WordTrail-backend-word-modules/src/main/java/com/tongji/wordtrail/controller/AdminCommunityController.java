package com.tongji.wordtrail.controller;

import com.tongji.wordtrail.dto.PostResponse;
import com.tongji.wordtrail.dto.UserResponse;
import com.tongji.wordtrail.model.Comment;
import com.tongji.wordtrail.model.Post;
import com.tongji.wordtrail.repository.CommentRepository;
import com.tongji.wordtrail.service.CommunityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminCommunityController {
    private final CommunityService communityService;
    private final CommentRepository commentRepository;

    // ✅ 需要这个构造方法注入
    @Autowired
    public AdminCommunityController(CommunityService communityService, CommentRepository commentRepository) {
        this.communityService = communityService;
        this.commentRepository = commentRepository;
    }
    @GetMapping("/getpostcount")
    public ResponseEntity<?> getPostCount() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("msg", null);
        List<Map<String, Long>> data = new ArrayList<>();
        Map<String, Long> count = new HashMap<>();
        count.put("count", communityService.getPostCount());
        data.add(count);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/getpost/{id}")
    public ResponseEntity<?> getPost(@PathVariable int id) {
        List<PostResponse> postList = communityService.getPostsByPage(id);
        Collections.reverse(postList);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", postList);
        return ResponseEntity.ok().body(responseData);
    }
    @PostMapping("/deletepost")
    public ResponseEntity<?> deletePost(@RequestBody Map<String, Object> post) {
        String id = (String) post.get("id");
        String message = (String) post.get("message");
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("msg", null);
        if (communityService.existPost(id)) {
            communityService.deletePostById(id);
            communityService.addMessage(id, message);
            responseData.put("code", 200);
        }
        else {
            responseData.put("code", 404);
            responseData.put("msg", "帖子不存在");
        }
        return ResponseEntity.ok().body(responseData);
    }
    @GetMapping("/searchposttitle")
    public ResponseEntity<?> searchPostTitle(@RequestParam String keyword) {
        List<PostResponse> postList = communityService.getPostSearch(keyword);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", postList);
        return ResponseEntity.ok().body(responseData);
    }
    @GetMapping("/searchpostuserId")
    public ResponseEntity<?> searchPostByUserId(@RequestParam String userId) {
        List<PostResponse> postList = communityService.getPostSearchUser(userId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", postList);
        return ResponseEntity.ok().body(responseData);
    }
    @GetMapping("/searchpoststate")
    public ResponseEntity<?> searchPostState(@RequestParam String state) {
        List<PostResponse> postList = communityService.getPostState(state);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", postList);
        return ResponseEntity.ok().body(responseData);
    }
    @PostMapping("/setstate/{id}")
    public ResponseEntity<?> setState(@PathVariable String id, @RequestBody Map<String, Object> post) {
        String state = (String) post.get("state");
        communityService.setState(id, state);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        return ResponseEntity.ok().body(responseData);
    }
    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPostsById(@PathVariable String id) {
        Map<String, Object> responseData = new HashMap<>();
        PostResponse post = communityService.getPostById(id);
        List<Comment> comments = commentRepository.findByPostId(id);
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("post", post);
        responseData.put("comment", comments);
        return ResponseEntity.ok().body(responseData);
    }
    @DeleteMapping("/posts/comsearchpoststatement")
    public ResponseEntity<?> deletePostComment(@RequestBody Map<String, Object> data) {
        String message = (String) data.get("message");
        String commentId = (String) data.get("commentId");
        communityService.comsearchpoststatement(commentId, message);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        return ResponseEntity.ok().body(responseData);
    }
    @GetMapping("/getuserscount")
    public ResponseEntity<?> getUsersCount() {
        int count = communityService.getUsersCount();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("count", count);
        return ResponseEntity.ok().body(responseData);
    }
    @GetMapping("/getuser/{id}")
    public ResponseEntity<?> getUser(@PathVariable int id) {
        List<UserResponse> data = communityService.getUsers(id);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        responseData.put("data", data);
        return ResponseEntity.ok().body(responseData);
    }
    @PostMapping("/users/{id}/setstate")
    public ResponseEntity<?> setUserState(@PathVariable String id, @RequestBody Map<String, Object> post) {
        String state = (String) post.get("state");
        String message = (String) post.get("message");
        communityService.setUserState(id, state, message);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("msg", null);
        return ResponseEntity.ok().body(responseData);
    }
}
