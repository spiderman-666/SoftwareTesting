package com.tongji.wordtrail.repository;

import com.tongji.wordtrail.model.Post;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String>, CustomPostRepository {
    List<Post> findByPage(int Page);
    List<Post> findByUserIdAndPage(String userId, int Page);
    @Query("{'$and': [ { 'page': ?1 }, { '$or': [ {'title': { $regex: ?0, $options: 'i' }}, {'content': { $regex: ?0, $options: 'i' }} ] } ] }")
    List<Post> findByKeywordAndPage(String keyword, int page);
    @Query("{'$and': [ { '$or': [ {'title': { $regex: ?0, $options: 'i' }}, {'content': { $regex: ?0, $options: 'i' }} ] } ] }")
    List<Post> findByKeyword(String keyword);

    List<Post> findByUserId(String userId);
    List<Post> findByState(String state);

}

