package com.squad10.chatboteasy.controller;

import com.squad10.chatboteasy.dto.IncomingMessage;
import com.squad10.chatboteasy.service.ChatLogic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/webhook")
public class WhatsWebhook {

    private final ChatLogic chatLogic;

    public WhatsWebhook(ChatLogic chatLogic) {
        this.chatLogic = chatLogic;
    }

    @Value("${meta.verify.token}")
    private String verifyToken;

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
        @RequestParam("hub.mode") String mode,
        @RequestParam("hub.verify_token") String tokenRecebido,
        @RequestParam("hub.challenge") String challenge
     ) {
        if(tokenRecebido.equals(verifyToken))
        {
            System.out.println("Webhook Verificado");
            return ResponseEntity.ok(challenge);
        }

        System.out.println("Webhook não verificado");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    public void receiveMessage(@RequestBody IncomingMessage payload) {
        chatLogic.processIncomingMessage(payload);
    }

}