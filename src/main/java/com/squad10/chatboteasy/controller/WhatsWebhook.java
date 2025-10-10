package com.squad10.chatboteasy.controller;

import com.squad10.chatboteasy.service.WhatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WhatsWebhook {

    @Autowired
    private WhatsService whatsService;

    @GetMapping
    public ResponseEntity<String> verify(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String token
    ) {
        if ("vibecoding".equals(token)) {
            System.out.println("Webhook Verificado");
            return ResponseEntity.ok(challenge);
        }

        System.out.println("Webhook Nao Verificado");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    public void receiveMessage(@RequestBody Map<String, Object> payload) {
        System.out.println("Recebido: " + payload);

        try{
            List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.get("entry");
            if (entries == null || entries.isEmpty()) return;

            Map<String, Object> firstEntry = entries.get(0);
            List<Map<String, Object>> changes = (List<Map<String, Object>>) firstEntry.get("changes");
            if (changes == null || changes.isEmpty()) return;

            Map<String, Object> firstChange = changes.get(0);
            Map<String, Object> value = (Map<String, Object>) firstChange.get("value");
            if (value == null) return;

            List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");
            if (messages == null || messages.isEmpty()) return;

            Map<String, Object> firstMessage = messages.get(0);
            String from = (String) firstMessage.get("from");
            Map<String, Object> text = (Map<String, Object>) firstMessage.get("text");
            String body = (String) text.get("body");

            List<Map<String, Object>> contacts = (List<Map<String, Object>>) value.get("contacts");
            Map<String, Object> firstContact = contacts.get(0);
            Map<String, Object> profile = (Map<String, Object>) firstContact.get("profile");
            String profileName = (String) profile.get("name");

            if ("oi".equalsIgnoreCase(body.trim())) {
                String userIdentifier = (profileName != null && !profileName.isBlank()) ? profileName : from;
                String responseMessage = "Olá, " + userIdentifier + "! 👋";

                whatsService.sendMessage(from, responseMessage);
            }
        } catch (Exception e){
            System.err.println("Erro ao processar payload: " + e.getMessage());
        }
    }
}