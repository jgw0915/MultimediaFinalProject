package com.example.demo.Model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Comment {
    @Id
    private String id;           // Unique ID for the comment
    private String text;         // Comment text
    private int likes;           // Like count
    private User user;           // User who made the comment
    private boolean isFlagged;   // Whether the comment is flagged as inappropriate
    private String replyTo;      // ID of the comment this is replying to (null if not a reply)
    private LocalDateTime createdAt; // Timestamp
    private LocalDateTime updatedAt; // Last updated timestamp

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public boolean isFlagged() { return isFlagged; }
    public void setFlagged(boolean flagged) { isFlagged = flagged; }

    public String getReplyTo() { return replyTo; }
    public void setReplyTo(String replyTo) { this.replyTo = replyTo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", likes=" + likes +
                ", user=" + user +
                ", isFlagged=" + isFlagged +
                ", replyTo='" + replyTo + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
