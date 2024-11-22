package com.example.demo.Model;

import org.springframework.web.multipart.MultipartFile;

public class UserImageSelectParam {
    private String email;
    private MultipartFile image;

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
