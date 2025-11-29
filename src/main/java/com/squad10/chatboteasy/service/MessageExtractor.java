package com.squad10.chatboteasy.service;

import com.squad10.chatboteasy.dto.IncomingMessage;
import org.springframework.stereotype.Service;


@Service
public class MessageExtractor {
    
    private final ChatLogic chatLogic;

    public MessageExtractor(ChatLogic chatLogic) {
        this.chatLogic = chatLogic;
    }

    public void processIncomingMessage(IncomingMessage payload) throws InterruptedException {

            if (payload == null || payload.getEntry() == null) return;

            for (var entry : payload.getEntry()) {
                if (entry.getChanges() == null) continue;

                for (var change : entry.getChanges()) {
                    var value = change.getValue();
                    if (value == null || value.getMessages() == null) continue;

                    for (var msg : value.getMessages()) {
                        var from = msg.getFrom();
                        var tipo = msg.getType();
                        var conteudo = tipo;

                        System.out.printf("\nMensagem de %s \nTipo: %s \n", from, tipo);

                        if (tipo.equals("text")) {
                            conteudo = msg.getText().getBody().trim();
                        }

                        if (tipo.equals("interactive")){
                            conteudo = msg.getInteractive().getList_reply().getId();
                        }

                        System.out.printf("Conteudo: %s \n", conteudo);
                        chatLogic.chatFlux(from, conteudo, tipo);

                    }
                }
            }
        }
}
