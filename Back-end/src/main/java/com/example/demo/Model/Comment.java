package com.example.demo.Model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Comment {
    @Id
    private String id;
    private String text;
    private int likes;
    private User user;
    private String replies;
    private LocalDateTime createdAt; // Timestamp
    private LocalDateTime updatedAt; // Last updated timestamp

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setReplies(String replies) {
        this.replies = replies;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getLikes() {
        return likes;
    }

    public User getUser() {
        return user;
    }

    public String getReplies() {
        return replies;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
