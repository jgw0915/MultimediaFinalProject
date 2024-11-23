package com.example.demo.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // Add a new post
    @PostMapping("/add")
    public ResponseEntity<String> createPost(@RequestBody Post post) {
        try {
            post.setCreatedAt(LocalDateTime.now());
            post.setUpdatedAt(LocalDateTime.now());
            post.setViews(0);
            Post savedPost = postRepository.save(post);
            return ResponseEntity.status(HttpStatus.CREATED).body("Saved Post Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
            return ResponseEntity.ok(updatedPost.toString());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
                post.setTitle(updatedPost.getTitle());
                post.setContent(updatedPost.getContent());
                post.setUpdatedAt(LocalDateTime.now());

                Post savedPost = postRepository.save(post);
                return ResponseEntity.ok("Update Post Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
