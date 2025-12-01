package com.squad10.chatboteasy.dev;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.squad10.chatboteasy.service.ChatLogic;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@ConditionalOnProperty(name = "terminal.chat.enabled", havingValue = "true", matchIfMissing = false)
@Component

public class DevChatTxExecutor {

    private final ChatLogic chatLogic;

    public DevChatTxExecutor(ChatLogic chatLogic) {
        this.chatLogic = chatLogic;
    }

    @Transactional
    public void processarMensagem(String from, String mensagem, String tipo) {
        chatLogic.chatFlux(from, mensagem, tipo);
    }
}
