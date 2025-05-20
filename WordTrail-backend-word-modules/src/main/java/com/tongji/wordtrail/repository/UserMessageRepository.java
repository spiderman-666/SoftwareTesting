package com.tongji.wordtrail.repository;

import com.tongji.wordtrail.model.UserState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMessageRepository extends MongoRepository<UserState,String> {
    List<UserState> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
