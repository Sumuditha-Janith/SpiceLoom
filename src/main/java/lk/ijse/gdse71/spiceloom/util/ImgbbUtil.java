package lk.ijse.gdse71.spiceloom.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Map;

@Component
public class ImgbbUtil {

    @Value("${imgbb.api.key}")
    private String imgbbApiKey;

    private static final String IMGBB_API_URL = "https://api.imgbb.com/1/upload";

    public String uploadImage(MultipartFile file) {
        try {
            byte[] fileContent = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(fileContent);

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("key", imgbbApiKey);        // now injected from property
            body.add("image", base64);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(IMGBB_API_URL, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                if (data != null) {
                    return (String) data.get("url");
                }
            }
            throw new RuntimeException("Image upload failed");
        } catch (Exception e) {
            throw new RuntimeException("Image upload error: " + e.getMessage());
        }
    }
}