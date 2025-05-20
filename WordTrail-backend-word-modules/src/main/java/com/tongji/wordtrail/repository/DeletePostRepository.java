package com.tongji.wordtrail.repository;

import com.tongji.wordtrail.model.DeletePost;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeletePostRepository extends MongoRepository<DeletePost, String> {

}
