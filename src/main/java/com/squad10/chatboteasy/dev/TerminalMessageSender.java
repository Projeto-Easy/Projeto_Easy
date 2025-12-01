package com.squad10.chatboteasy.dev;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "terminal.chat.enabled", havingValue = "true", matchIfMissing = false)

public class TerminalMessageSender implements MessageSender {
    @Override
    public void sendInteractivePdfPeriodo(String to) {
        sendMessage(to, """
            RELATÓRIO EM PDF 📄
            1. Últimos 7 dias
            2. Período personalizado
            """);
    }
    @Override
    public void sendMessage(String to, String text) {
        System.out.println("\n[BOT -> " + to + "]\n" + text);
    }
    @Override
    public void sendPdf(String to, String filename, byte[] pdfBytes) {
    try {
        var dir = java.nio.file.Path.of("tmp");
        java.nio.file.Files.createDirectories(dir);

        var path = dir.resolve(filename);
        java.nio.file.Files.write(path, pdfBytes);

        sendMessage(to, "PDF gerado e salvo em: " + path.toAbsolutePath());
    } catch (Exception e) {
        throw new RuntimeException("Falha ao salvar PDF", e);
    }
}

    @Override
    public void sendInteractiveMenuPrincipal(String to) {
        sendMessage(to, """
            Olá! Sou seu assistente financeiro da Easy.
            Estou aqui para te ajudar no que precisar.

            O que você quer ver agora?

               1. Resumo do financeiro
               2. Contas a receber
               3. Contas a pagar
               4. Fluxo de caixa
               5. Sair
            """);
    }

    @Override
    public void sendInteractiveResumoFinanceiro(String to) {
        sendMessage(to, """
            RESUMO FINANCEIRO

            Qual período você quer consultar?

                1. Últimos 7 dias
                2. Últimos 15 dias
                3. Últimos 30 dias
                4. Período personalizado

            Digite o número da opção.
            """);
    }

    @Override
    public void sendRepetirQuestion(String to) {
        sendMessage(to, "Deseja realizar outra consulta ? sim ou não");
    }

    @Override
    public void sendAgradecerContato(String to) {
        sendMessage(to, "Até logo!");
    }
}
