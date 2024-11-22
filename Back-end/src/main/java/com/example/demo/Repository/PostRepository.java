package com.example.demo.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
}
