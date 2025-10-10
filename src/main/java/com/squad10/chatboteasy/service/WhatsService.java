package com.squad10.chatboteasy.service;

import com.squad10.chatboteasy.dto.SendMessageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

@Service
public class WhatsService {

    @Value("${whats.api.token}")
    private String apiToken;

    @Value("${whats.phone.number.id}")
    private String phoneNumberId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMessage(String to, String text){
        String url = "https://graph.facebook.com/v23.0/" + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);

        SendMessageRequest requestBody = new SendMessageRequest(to, text);

        HttpEntity<SendMessageRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            System.out.println("Enviando resposta para: " + to);
            restTemplate.postForEntity(url, requestEntity, String.class);
            System.out.println("Resposta enviada");
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
        }

    }
}
