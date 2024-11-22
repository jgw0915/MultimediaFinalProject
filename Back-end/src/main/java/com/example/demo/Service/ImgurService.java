package com.example.demo.Service;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Config.ImgurConfig;

@Service
public class ImgurService {

    private static final Logger logger = LoggerFactory.getLogger(ImgurService.class);

    private final ImgurConfig imgurConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public ImgurService(ImgurConfig imgurConfig) {
        this.imgurConfig = imgurConfig;
    }

    public String uploadImage(MultipartFile image) throws IOException {
        String url = "https://api.imgur.com/3/image";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Client-ID " + imgurConfig.getId());

        ByteArrayResource fileAsResource = new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", fileAsResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        int maxAttempts = 5;
        int attempt = 0;
        long waitTime = 1000; // Initial wait time in milliseconds

        while (attempt < maxAttempts) {
            logger.info("Call {} times to imgur API",attempt);
            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> responseBody = response.getBody();
                    Map<String, String> data = (Map<String, String>) responseBody.get("data");
                    return data.get("link");
                } else {
                    throw new RuntimeException("Failed to upload image to Imgur: " + response.getStatusCode());
                }
            } catch (HttpClientErrorException.TooManyRequests e) {
                attempt++;
                if (attempt >= maxAttempts) {
                    throw new RuntimeException("Exceeded maximum retry attempts due to rate limiting.");
                }
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted during backoff.", ie);
                }
                waitTime *= 2; // Exponential backoff
            } catch (ResourceAccessException e) {
                throw new RuntimeException("I/O error during image upload.", e);
            }
        }
        throw new RuntimeException("Failed to upload image after multiple attempts.");
    }
}
