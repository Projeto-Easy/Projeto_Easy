package com.squad10.chatboteasy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SendMessage {

    @Value("${whats.api.token}")
    String apiToken;

    @Value("${whats.phone.number.id}")
    String phoneId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMessage(String to, String text) {
        String url = "https://graph.facebook.com/v22.0/" + phoneId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("to", to);
        payload.put("type", "text");

        Map<String, String> textObj = new HashMap<>();
        textObj.put("body", text);
        payload.put("text", textObj);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.printf("Resposta enviada para : %s\n", to);
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem: " + e);
        }

    }
}
