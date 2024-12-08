package com.example.demo.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.demo.Model.Reply;
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
            post.getComments().add(comment);
            return postRepository.save(post);
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    public Post addReply(String postId, Reply reply, int commentId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.getComments().get(commentId).getReplies().add(reply);
            return postRepository.save(post);
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    public void increaseCommentLike(String postId, int commentId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.getComments().get(commentId).setLikes(post.getComments().get(commentId).getLikes() + 1);
            postRepository.save(post);
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    public void increaseReplyLike(String postId, int commentId, int replyId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.getComments().get(commentId).getReplies().get(replyId).setLikes(post.getComments().get(commentId).getReplies().get(replyId).getLikes() + 1);
            postRepository.save(post);
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

    public void increaseDownloads(String postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setDownloads(post.getDownloads() + 1);
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