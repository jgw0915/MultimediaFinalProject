package com.example.demo.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Model.Comment;
import com.example.demo.Model.Post;
import com.example.demo.Repository.PostRepository;
import com.example.demo.Service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    // Add a new post
    @PostMapping("/add")
    public ResponseEntity<String> createPost(@RequestBody Post post) {
        try {
            logger.info("Post content text: {}", post.getContentText());
            logger.info("Post content image: {}", post.getContentImage());
            post.setCreatedAt(LocalDateTime.now());
            post.setUpdatedAt(LocalDateTime.now());
            Post savedPost = postRepository.save(post);
            return ResponseEntity.status(HttpStatus.CREATED).body("Saved Post Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving post");
        }
    }

    // Add a comment to a post
    @PostMapping("/{postId}/comments")
    public ResponseEntity<String> addComment(@PathVariable String postId, @RequestBody Comment comment) {
        try {
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdatedAt(LocalDateTime.now());
            comment.setLikes(0);
            Post updatedPost = postService.addComment(postId, comment);
            return ResponseEntity.ok("Comment added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid post ID");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding comment");
        }
    }

    // Get all posts
    @GetMapping("/getAll")
    public ResponseEntity<String> getAllPosts() {
        try {
            List<Post> posts = postRepository.findAll();
            String jsonResponse = objectMapper.writeValueAsString(posts);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving posts.");
        }
    }

    // Update an existing post
    @PutMapping("/update/{postId}")
    public ResponseEntity<String> updatePost(@PathVariable String postId, @RequestBody Post updatedPost) {
        try {
            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isPresent()) {
                Post post = postOptional.get();

                // Update fields
                post.setContentText(updatedPost.getContentText());
                post.setContentImage(updatedPost.getContentImage());
                post.setUpdatedAt(LocalDateTime.now());

                Post savedPost = postRepository.save(post);
                return ResponseEntity.ok("Update Post Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not Found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating post");
        }
    }

    @PutMapping("/like/{postId}")
    public ResponseEntity<String> updateLike(@PathVariable String postId, @RequestParam boolean increase) {
        try {
            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isPresent()) {
                if (increase) {
                    postService.increaseLike(postId);
                } else {
                    postService.decreaseLike(postId);
                }
                return ResponseEntity.ok("Updated Post Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not Found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating post");
        }
    }

    @PutMapping("/download/{postId}")
    public ResponseEntity<String> updateDownloads(@PathVariable String postId) {
        try {
            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isPresent()) {
                postService.increaseDownloads(postId);
                return ResponseEntity.ok("Updated Post Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not Found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating post");
        }
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable String postId) {
        try {
            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isPresent()) {
                postRepository.deleteById(postId);
                return ResponseEntity.ok("Post deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting post");
        }
    }

}
