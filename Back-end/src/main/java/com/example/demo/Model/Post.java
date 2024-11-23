package com.example.demo.Model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Post")
public class Post {
    @Id
    private String id;                // Unique ID for the post
    private String title;             // Title of the post
    private String contentText;
    private String contentImage;          // Content of the post
    private User author;            // Author of the post
    private List<Comment> comments;   // List of comments
    private int views;                // Number of views
    private List<User> tags;        // Tags associated with the post
    private boolean isPublished;      // Whether the post is published
    private LocalDateTime createdAt;  // Post creation timestamp
    private LocalDateTime updatedAt;  // Post update timestamp

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContentText() { return contentText; }
    public void setContentText(String contentText) { this.contentText = contentText; }

    public String getContentImage() { return contentImage; }
    public void setContentImage(String contentImage) { this.contentImage = contentImage; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public List<User> getTags() { return tags; }
    public void setTags(List<User> tags) { this.tags = tags; }

    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", contentText='" + contentText + '\'' +
                ", contentImage='" + contentImage + '\'' +
                ", author='" + author + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", comments=" + comments +
                '}';
    }
}
