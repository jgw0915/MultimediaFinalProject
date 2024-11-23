package com.example.demo.Model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Post")
public class Post {
    @Id
    private String id;                // Unique ID for the post
    private String contentText;
    private String contentImage;          // Content of the post
    private User author;            // Author of the post
//    private List<Comment> comments;   // List of comments
    private int likes;
    private int downloads;
    private LocalDateTime createdAt;  // Post creation timestamp
    private LocalDateTime updatedAt;  // Post update timestamp

    public void setId(String id) {
        this.id = id;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public void setContentImage(String contentImage) {
        this.contentImage = contentImage;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

//    public void setComments(List<Comment> comments) {
//        this.comments = comments;
//    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
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

    public String getContentText() {
        return contentText;
    }

    public String getContentImage() {
        return contentImage;
    }

    public User getAuthor() {
        return author;
    }

//    public List<Comment> getComments() {
//        return comments;
//    }

    public int getLikes() {
        return likes;
    }

    public int getDownloads() {
        return downloads;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", contentText='" + contentText + '\'' +
                ", contentImage='" + contentImage + '\'' +
                ", author=" + author +
//                ", comments=" + comments +
                ", likes=" + likes +
                ", downloads=" + downloads +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}