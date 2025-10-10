package com.squad10.chatboteasy.service;

import com.squad10.chatboteasy.dto.IncomingMessage;
import com.squad10.chatboteasy.dto.IncomingMessage.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatLogic {

    private final SendMessage sendMessage;

    public void processIncomingMessage(IncomingMessage payload) {

        for(var entry : payload.getEntry()) {
            for(var change : entry.getChanges()) {
                if (change.getValue().getMessages() == null) continue;
                
                for(Message message : change.getValue().getMessages()){
                    if (change.getValue().getMessages() == null) continue;

                    String from = message.getFrom();

                    if ("text".equals(message.getType())) {
                        String conteudo = message.getText().getBody();

                        handleTextMessage(from, conteudo);
                    } else {
                        handleNonTextMessage(from);
                    }

                }

            }
        }
    }

    public void handleTextMessage(String from, String conteudo) {
        String resposta = "Mensagem recebida: " + conteudo;
        sendMessage.sendMessage(from, resposta);
    }

    public void handleNonTextMessage(String from) {
        String resposta = "Desculpe, só consigo receber mensagens de texto por enquanto.";
        sendMessage.sendMessage(from, resposta);
    }

}

