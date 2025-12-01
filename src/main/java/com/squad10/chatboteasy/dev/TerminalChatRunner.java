package com.squad10.chatboteasy.dev;

import com.squad10.chatboteasy.enums.EtapaFluxo;
import com.squad10.chatboteasy.repository.NumeroCadastradoRepository;
import com.squad10.chatboteasy.tables.NumeroCadastrado;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Scanner;

@ConditionalOnProperty(name = "terminal.chat.enabled", havingValue = "true", matchIfMissing = false)
@Component
@RequiredArgsConstructor
public class TerminalChatRunner implements CommandLineRunner {

    private final DevChatTxExecutor tx; // <-- NOVO (envolve ChatLogic em @Transactional)
    private final NumeroCadastradoRepository numRepo;

    // número fixo do “usuário” de teste no terminal
    private static final String FROM = "5579999999999";

    @Override
    public void run(String... args) {
        Locale.setDefault(Locale.forLanguageTag("pt-BR"));

        System.out.println("""
        🧪 TESTE CHAT PELO TERMINAL
        Digite mensagens.
        Comandos:
          /reset  -> volta pro INICIO
          /etapa  -> mostra etapa atual
          /sair   -> encerra
        """);

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("\n[VOCÊ] > ");
                String line = sc.nextLine();
                if (line == null) return;

                String input = line.trim();
                if (input.isEmpty()) continue;

                // comandos do terminal (não passam pelo ChatLogic)
                if (input.equalsIgnoreCase("/sair")) return;

                if (input.equalsIgnoreCase("/reset") || input.equalsIgnoreCase("/menu")) {
                    resetarFluxo(FROM);
                    System.out.println("[DEV] Fluxo resetado para INICIO ✅");
                    continue;
                }

                if (input.equalsIgnoreCase("/etapa")) {
                    var etapa = numRepo.findByNumero(FROM).map(NumeroCadastrado::getEtapaFluxo).orElse(null);
                    System.out.println("[DEV] etapaFluxo atual = " + etapa);
                    continue;
                }

                // aqui sim: manda pro fluxo real (dentro de transação)
                tx.processarMensagem(FROM, input, "text");
            }
        }
    }

    private void resetarFluxo(String numero) {
        numRepo.findByNumero(numero).ifPresent(contato -> {
            contato.setEtapaFluxo(EtapaFluxo.INICIO.name());
            contato.setUltimoContato(LocalDateTime.now());
            numRepo.save(contato);
        });
    }
}
