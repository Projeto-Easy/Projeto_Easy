package com.squad10.chatboteasy.dev;

import com.squad10.chatboteasy.service.SendMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "terminal.chat.enabled", havingValue = "false", matchIfMissing = true)
public class MetaMessageSender implements MessageSender {
    @Override
    public void sendInteractivePdfPeriodo(String to) {
        send.sendInteractivePdfPeriodo(to);
    }

    private final SendMessage send;

    public MetaMessageSender(SendMessage send) {
        this.send = send;
    }

    @Override
    public void sendMessage(String to, String text) {
        send.sendMessage(to, text);
    }

    @Override
    public void sendInteractiveMenuPrincipal(String to) {
        send.sendInteractiveMenuPrincipal(to);
    }

    @Override
    public void sendInteractiveResumoFinanceiro(String to) {
        send.sendInteractiveResumoFinanceiro(to);
    }

    @Override
    public void sendRepetirQuestion(String to) {
        send.sendRepetirQuestion(to);
    }

    @Override
    public void sendAgradecerContato(String to) {
        send.sendAgradecerContato(to);
    }
    @Override
    public void sendPdf(String to, String filename, byte[] pdfBytes) {
    try {
        var dir = java.nio.file.Path.of("tmp");
        java.nio.file.Files.createDirectories(dir);
        var path = dir.resolve(filename);
        java.nio.file.Files.write(path, pdfBytes);

        send.sendMessage(to, "PDF gerado e salvo em: " + path.toAbsolutePath());
    } catch (Exception e) {
        throw new RuntimeException("Falha ao salvar PDF", e);
    }
}

}
