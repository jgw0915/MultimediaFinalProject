package com.example.demo.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Model.Comment;
import com.example.demo.Model.Post;
import com.example.demo.Repository.PostRepository;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    // Add a comment to a post
    public Post addComment(String postId, Comment comment) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
//            comment.setId(null); // Let MongoDB generate the ID
//            comment.setCreatedAt(LocalDateTime.now());
            post.getComments().add(comment);
//            post.setUpdatedAt(LocalDateTime.now());
            return postRepository.save(post);
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    public void increaseLike(String postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setLikes(post.getLikes() + 1);
            post.setUpdatedAt(LocalDateTime.now());
            postRepository.save(post);
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    public void decreaseLike(String postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setLikes(post.getLikes() - 1);
            post.setUpdatedAt(LocalDateTime.now());
            postRepository.save(post);
        } else {
            throw new RuntimeException("Post not found");
        }
    }
}
