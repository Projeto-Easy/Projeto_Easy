package com.squad10.chatboteasy.dev;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@ConditionalOnProperty(name = "terminal.chat.enabled", havingValue = "true", matchIfMissing = false)
public class ConsoleMessageSender implements MessageSender {
    @Override
    public void sendPdf(String to, String filename, byte[] pdfBytes) {
    try {
        var dir = java.nio.file.Path.of("tmp");
        java.nio.file.Files.createDirectories(dir);

        var path = dir.resolve(filename);
        java.nio.file.Files.write(path, pdfBytes);

        System.out.println("\n[PDF -> " + to + "] salvo em: " + path.toAbsolutePath() + "\n");
        } catch (Exception e) {
        throw new RuntimeException("Falha ao salvar PDF no terminal", e);
        }
}

    @Override
    public void sendMessage(String to, String text) {
        System.out.println("\n[BOT -> " + to + "]\n" + text + "\n");
    }

    @Override
    public void sendInteractiveMenuPrincipal(String to) {
        // No terminal, só “simula” o menu interativo como texto
        sendMessage(to, """
        MENU PRINCIPAL
          1. Resumo financeiro
          2. Contas a receber
          3. Contas a pagar
          4. Fluxo de caixa
          5. Sair
        """);
    }
    @Override
    public void sendInteractivePdfPeriodo(String to) {
    System.out.println("\n[BOT -> " + to + "]\n1) Últimos 7 dias\n2) Período personalizado\n");
    }

    @Override
    public void sendInteractiveResumoFinanceiro(String to) {
        sendMessage(to, """
        RESUMO FINANCEIRO - ESCOLHA O PERÍODO
          1. Últimos 7 dias
          2. Últimos 15 dias
          3. Últimos 30 dias
          4. Período personalizado
        """);
    }

    @Override
    public void sendRepetirQuestion(String to) {
        sendMessage(to, "Deseja fazer outra consulta? (sim/não)");
    }

    @Override
    public void sendAgradecerContato(String to) {
        sendMessage(to, "Obrigado pelo contato! Se precisar, é só chamar 🙂");
    }
}
