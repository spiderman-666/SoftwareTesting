package com.tongji.wordtrail.service;

import com.tongji.wordtrail.dto.CommentRequest;
import com.tongji.wordtrail.dto.PostResponse;
import com.tongji.wordtrail.dto.UserResponse;
import com.tongji.wordtrail.model.*;
import com.tongji.wordtrail.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CommunityService {
    @Autowired
    private final PostRepository postRepository;
    @Autowired
    private final VoteRepository voteRepository;
    @Autowired
    private final FavouriteRepository favouriteRepository;
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final DeletePostRepository deletePostRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMessageRepository userMessageRepository;

    @Autowired
    public CommunityService(PostRepository postRepository, VoteRepository voteRepository, FavouriteRepository favouriteRepository, CommentRepository commentRepository , DeletePostRepository deletePostRepository) {
        this.postRepository = postRepository;
        this.voteRepository = voteRepository;
        this.favouriteRepository = favouriteRepository;
        this.commentRepository = commentRepository;
        this.deletePostRepository = deletePostRepository;
    }

    public Post createPost(String userId, String title, String content, List<String> filePaths) {
        // 直接存储字符串路径
        long postCountLong = postRepository.count(); // MongoDB 里统计文档总数
        int postCount = (int) postCountLong;
        // 示例：用帖子总数计算页码（比如每页10个）
        int page = postCount / 10 + 1;
        User user = userRepository.findById(userId).orElse(null);
        String userAvatar = null;
        if (user != null) {
            userAvatar = user.getAvatarUrl();
        }
        Post post = new Post(title, content, LocalDateTime.now(), LocalDateTime.now(), userId, filePaths, 0, 0, page, userAvatar);
        return postRepository.save(post);
    }
    public PostResponse getPostById(String id) {
        Post post = postRepository.findById(id).get();
        User user = userRepository.findById(post.getUserId()).orElse(null);
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setFilePaths(post.getFilePaths());
        response.setCreatedTime(post.getCreatedAt().toString());
        response.setUpdatedTime(post.getUpdatedTime().toString());
        response.setUserId(post.getUserId());
        response.setUsername(user.getUsername());
        response.setCommentCount(post.getCommentCount());
        response.setVoteCount(post.getVoteCount());
        response.setState(post.getState());
        response.setUserAvatar(post.getUserAvatar());
        return response;
    }
    public boolean deletePostById(String id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<PostResponse> getPostsByPage(int page) {
        List<Post> posts = postRepository.findByPage(page);
        return posts.stream().map(post -> {
            User user = userRepository.findById(post.getUserId()).orElse(null);
            PostResponse response = new PostResponse();
            response.setId(post.getId());
            response.setTitle(post.getTitle());
            response.setContent(post.getContent());
            response.setFilePaths(post.getFilePaths());
            response.setCreatedTime(post.getCreatedAt().toString());
            response.setUpdatedTime(post.getUpdatedTime().toString());
            response.setUserId(post.getUserId());
            response.setUsername(user.getUsername());
            response.setCommentCount(post.getCommentCount());
            response.setVoteCount(post.getVoteCount());
            response.setState(post.getState());
            response.setUserAvatar(post.getUserAvatar());
            return response;
        }).collect(Collectors.toList());
    }
    // 随机获取10条帖子
    public List<PostResponse> getRandomPostResponses() {
        List<Post> posts = postRepository.findRandomPosts(10);  // 随机10条
        return posts.stream().map(post -> {
            User user = userRepository.findById(post.getUserId()).orElse(null);
            PostResponse response = new PostResponse();
            response.setId(post.getId());
            response.setTitle(post.getTitle());
            response.setContent(post.getContent());
            response.setFilePaths(post.getFilePaths());
            response.setCreatedTime(post.getCreatedAt().toString());
            response.setUpdatedTime(post.getUpdatedTime().toString());
            response.setUserId(post.getUserId());
            response.setUsername(user.getUsername());
            response.setCommentCount(post.getCommentCount());
            response.setVoteCount(post.getVoteCount());
            response.setState(post.getState());
            response.setUserAvatar(post.getUserAvatar());
            return response;
        }).collect(Collectors.toList());
    }
    public long getPostCount() {
        return postRepository.count();
    }
    public List<PostResponse> getPostsByUserPage(String userId, int page) {
        List<Post> posts = postRepository.findByUserIdAndPage(userId, page);
        return posts.stream().map(post -> {
            User user = userRepository.findById(post.getUserId()).orElse(null);
            PostResponse response = new PostResponse();
            response.setId(post.getId());
            response.setTitle(post.getTitle());
            response.setContent(post.getContent());
            response.setFilePaths(post.getFilePaths());
            response.setCreatedTime(post.getCreatedAt().toString());
            response.setUpdatedTime(post.getUpdatedTime().toString());
            response.setUserId(post.getUserId());
            response.setUsername(user.getUsername());
            response.setCommentCount(post.getCommentCount());
            response.setVoteCount(post.getVoteCount());
            response.setState(post.getState());
            response.setUserAvatar(post.getUserAvatar());
            return response;
        }).collect(Collectors.toList());
    }
    public List<PostResponse> getPostSearch(String keyword, int page) {
        List<Post> posts = postRepository.findByKeywordAndPage(keyword, page);
        return posts.stream().map(post -> {
            User user = userRepository.findById(post.getUserId()).orElse(null);
            PostResponse response = new PostResponse();
            response.setId(post.getId());
            response.setTitle(post.getTitle());
            response.setContent(post.getContent());
            response.setFilePaths(post.getFilePaths());
            response.setCreatedTime(post.getCreatedAt().toString());
            response.setUpdatedTime(post.getUpdatedTime().toString());
            response.setUserId(post.getUserId());
            response.setUsername(user.getUsername());
            response.setCommentCount(post.getCommentCount());
            response.setVoteCount(post.getVoteCount());
            response.setState(post.getState());
            response.setUserAvatar(post.getUserAvatar());
            return response;
        }).collect(Collectors.toList());
    }
    public List<PostResponse> getPostSearch(String keyword) {
        List<Post> posts = postRepository.findByKeyword(keyword);
        return posts.stream().map(post -> {
            User user = userRepository.findById(post.getUserId()).orElse(null);
            PostResponse response = new PostResponse();
            response.setId(post.getId());
            response.setTitle(post.getTitle());
            response.setContent(post.getContent());
            response.setFilePaths(post.getFilePaths());
            response.setCreatedTime(post.getCreatedAt().toString());
            response.setUpdatedTime(post.getUpdatedTime().toString());
            response.setUserId(post.getUserId());
            response.setUsername(user.getUsername());
            response.setCommentCount(post.getCommentCount());
            response.setVoteCount(post.getVoteCount());
            response.setState(post.getState());
            response.setUserAvatar(post.getUserAvatar());
            return response;
        }).collect(Collectors.toList());
    }
    public int getPostSearchCount(String keyword) {
        List<Post> posts = postRepository.findByKeyword(keyword);
        return posts.size();
    }
    public int getPostVoteCount(String postId, String userId, String upvote) {
        Post post = postRepository.findById(postId).get();
        Vote vote = new Vote(LocalDateTime.now(), userId, upvote, postId);
        voteRepository.save(vote);
        if (Objects.equals(upvote, "true")) {
            post.setLike(post.getLike() + 1);
            post.setVoteCount(post.getVoteCount() + 1);
            postRepository.save(post);
        }
        else if(Objects.equals(upvote, "false")) {
            post.setDislike(post.getDislike() + 1);
            post.setVoteCount(post.getVoteCount() - 1);
            postRepository.save(post);
        }
        else if(upvote == null) {
            if(voteRepository.existsByUserId(userId)) {
                if(vote.getUpvote().equals("true")) {
                    post.setLike(post.getLike() - 1);
                    post.setVoteCount(post.getVoteCount() - 1);
                    postRepository.save(post);
                }
                else if(vote.getUpvote().equals("false")) {
                    post.setDislike(post.getDislike() - 1);
                    post.setVoteCount(post.getVoteCount() + 1);
                    postRepository.save(post);
                }
            }
        }
        return post.getVoteCount();
    }
    public int getPostVote(String postId, String userId) {
        if (voteRepository.existsByUserIdAndPostId(userId, postId)) {
            Vote vote = voteRepository.findByPostIdAndUserId(postId, userId).get(0);
            if (vote.getUpvote().equals("true")) {
                return 1;
            }
            else if(vote.getUpvote().equals("false")) {
                return -1;
            }
            else{
                return 0;
            }
        }
        else {
            return 0;
        }

    }
    public int getPostVoteCount(String postId) {
        return voteRepository.findByPostId(postId).size();
    }
    public void addFavourite(String postId, String userId) {
        Favourite favourite = new Favourite(userId, postId, LocalDateTime.now(), LocalDateTime.now());
        favouriteRepository.save(favourite);
    }
    public boolean deleteFavourite(String postId, String userId) {
        if (favouriteRepository.existsByPostIdAndUserId(postId, userId)) {
            favouriteRepository.deleteByPostIdAndUserId(postId, userId);
            return true;
        }
        else {
            return false;
        }
    }
    public List<Favourite> getFavoriteList(String userId) {
        List<Favourite> favourites = favouriteRepository.findByUserId(userId);
        return favourites;
    }
    public List<Favourite> getFavoriteListByPostId(String postId) {
        return favouriteRepository.findByPostId(postId);
    }
    public boolean isFavorite(String postId, String userId) {
        return favouriteRepository.existsByPostIdAndUserId(postId, userId);
    }
    public void addComment(CommentRequest request) {
        Comment comment = new Comment(request.getPostId(), request.getContent(), request.getUserId(), LocalDateTime.now(), LocalDateTime.now(), request.getParentComment());
        Post post = postRepository.findById(request.getPostId()).orElse(null);
        if (post != null) {
            post.setCommentCount(post.getCommentCount() + 1);
            postRepository.save(post);

        }
        commentRepository.save(comment);
    }
    public void deleteComment(String CommentId) {
        Comment comment = commentRepository.findById(CommentId).get();
        commentRepository.delete(comment);
    }
    public List<Comment> getCommentList(String postId) {
        return commentRepository.findByPostId(postId);
    }

    public boolean existPost(String postId) {
        return postRepository.existsById(postId);
    }
    public void addMessage(String postId, String message) {
        DeletePost deletePost = new DeletePost(postId, message);
        deletePostRepository.save(deletePost);
    }
    public List<PostResponse> getPostSearchUser(String userId) {
        List<Post> posts = postRepository.findByUserId(userId); // 假设有对应的查询方法
        User user = userRepository.findById(userId).get();
        return posts.stream().map(post -> {
            PostResponse response = new PostResponse();
            response.setId(post.getId());
            response.setTitle(post.getTitle());
            response.setContent(post.getContent());
            response.setFilePaths(post.getFilePaths());
            response.setCreatedTime(post.getCreatedAt().toString());
            response.setUpdatedTime(post.getUpdatedTime().toString());
            response.setUserId(post.getUserId());
            response.setUsername(user.getUsername());
            response.setCommentCount(post.getCommentCount());
            response.setVoteCount(post.getVoteCount());
            response.setState(post.getState());
            response.setUserAvatar(post.getUserAvatar());
            return response;
        }).collect(Collectors.toList());
    }
    // 举报帖子
    public void reportPost(String postId, String message) {
        Post post = postRepository.findById(postId).get();
        post.setState("reported");
        post.setMessage(message);
        postRepository.save(post);
    }
    // 查询某状态的所有帖子
    public List<PostResponse> getPostState(String state) {
        List<Post> posts = postRepository.findByState(state); // 假设有对应的查询方法
        return posts.stream().map(post -> {
            User user = userRepository.findById(post.getUserId()).orElse(null);
            PostResponse response = new PostResponse();
            response.setId(post.getId());
            response.setTitle(post.getTitle());
            response.setContent(post.getContent());
            response.setFilePaths(post.getFilePaths());
            response.setCreatedTime(post.getCreatedAt().toString());
            response.setUpdatedTime(post.getUpdatedTime().toString());
            response.setUserId(post.getUserId());
            response.setUsername(user.getUsername());
            response.setCommentCount(post.getCommentCount());
            response.setVoteCount(post.getVoteCount());
            response.setState(post.getState());
            response.setUserAvatar(post.getUserAvatar());
            return response;
        }).collect(Collectors.toList());
    }
    public void setState(String postId, String state) {
        Post post = postRepository.findById(postId).get();
        post.setState(state);
        postRepository.save(post);
    }
    public void comsearchpoststatement(String commentId, String message) {
        Comment comment = commentRepository.findById(commentId).get();
        comment.setState("reported");
        comment.setMessage(message);
        commentRepository.save(comment);
    }
    public int getUsersCount() {
        return (int) userRepository.count();
    }

    public List<UserResponse> getUsers(int id) {
        Pageable pageable = PageRequest.of(id - 1, 10);

        Page<User> page = userRepository.findAll(pageable);
        List<User> users = page.getContent();
        List <UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            UserResponse response = new UserResponse();
            response.setId(id);
            response.setUserId(user.getUserId());
            response.setUsername(user.getUsername());
            List <Post> posts = postRepository.findByUserId(user.getUserId());
            int like = 0, dislike = 0;
            for (Post post : posts) {
                like += post.getLike();
                dislike += post.getDislike();
            }
            response.setPostCount(posts.size());
            response.setCommentCount(commentRepository.findByUserId(user.getUserId()).size());
            response.setLikeCount(like);
            response.setDislikeCount(dislike);
            List<UserState> messages = userMessageRepository.findByUserId(user.getUserId());
            if (!messages.isEmpty()) {
                response.setState(messages.get(0).getState()); // 或者其他合适策略
            } else {
                response.setState("normal");
            }

            userResponses.add(response);
        }
        return userResponses;
    }
    public void setUserState(String userId, String state, String message) {
        UserState userState = new UserState(userId, state, message);
        userMessageRepository.save(userState);
    }
    public Comment getComment(String commentId) {
        return commentRepository.findById(commentId).get();
    }
}
