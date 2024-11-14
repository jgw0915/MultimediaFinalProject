
package com.example.demo.Model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "User") // Specify the collection name in MongoDB
public class User {
    
    @Id
    private String id; // Maps to the MongoDB field "_id"
    
    @Field("_email") // Explicitly map to the MongoDB "_email" field
    private String email;
    
    @Field("_nickname") // Explicitly map to the MongoDB "_nickname" field
    private String nickname;
    
    @Field("_password") // Explicitly map to the MongoDB "_password" field
    private String password;

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
