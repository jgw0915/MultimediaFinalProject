package com.example.demo.Service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Config.ImgurConfig;

@Service
public class ImgurService {

    private final ImgurConfig imgurConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public ImgurService(ImgurConfig imgurConfig) {
        this.imgurConfig = imgurConfig;
    }

    public String uploadImage(MultipartFile image) throws IOException {
        String url = "https://api.imgur.com/3/image";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Client-ID " + imgurConfig.getId());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> responseBody = response.getBody();
            Map<String, String> data = (Map<String, String>) responseBody.get("data");
            return data.get("link");
        } else {
            throw new RuntimeException("Failed to upload image to Imgur");
        }
    }
}
