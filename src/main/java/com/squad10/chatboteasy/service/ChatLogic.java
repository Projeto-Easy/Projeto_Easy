package com.squad10.chatboteasy.service;

import com.squad10.chatboteasy.repository.NumeroCadastradoRepository;
import com.squad10.chatboteasy.tables.NumeroCadastrado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatLogic {

    private final SendMessage sendMessage;
    private final NumeroCadastradoRepository numRepo;

    public void chatFlux (String from, String mensagem, String tipo){

        if(numRepo.existsByNumero(from)){

            if (tipo.equals("text")){
                handleTextMessage(from, mensagem);
            } else handleNonTextMessage(from, mensagem);

        }
    }

    public void handleTextMessage(String from, String mensagem) {
        String resposta = "Mensagem recebida: " + mensagem;
        sendMessage.sendMessage(from, resposta);
    }

    public void handleNonTextMessage(String from, String mensagem) {
        String resposta = String.format("Mensagems do tipo %s não são suportadas.", mensagem);
        sendMessage.sendMessage(from, resposta);
    }

}

