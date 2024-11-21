package com.example.demo.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "imgur.client")
public class ImgurConfig {
    private String id;
    private String secret;

    // Getters and setters
    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void setSecret(String secret){
        this.secret = secret;
    }

    public String getSecret(){
        return secret;
    }
}
