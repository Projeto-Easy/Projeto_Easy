package com.squad10.chatboteasy.service;

import com.squad10.chatboteasy.dto.report.WeeklyReportDTO;
import com.squad10.chatboteasy.enums.EtapaFluxo;
import com.squad10.chatboteasy.repository.NumeroCadastradoRepository;
import com.squad10.chatboteasy.tables.NumeroCadastrado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
public class ChatLogic {

    private final SendMessage sendMessage;
    private final NumeroCadastradoRepository numRepo;
    private final OmieDataService omieDataService;

    public void chatFlux(String from, String mensagem, String tipo) {


        // 1. Verifica se o número está cadastrado
        if (!numRepo.existsByNumero(from)) {
            sendMessage.sendMessage(from, "Número não cadastrado no sistema.");
            return;
        }

        NumeroCadastrado contato = numRepo.findByNumero(from).get();

        // 2. Se não for mensagem de texto responde e NÃO muda a etapa
        if (!"text".equalsIgnoreCase(tipo)) {
            sendMessage.sendMessage(from, "Desculpe, só aceito mensagens de texto por enquanto.");
            return;
        }

        // 3. Primeiro contato: define etapa inicial
        if (contato.getEtapaFluxo() == null
                || contato.getEtapaFluxo().isBlank()
                || (contato.getUltimoContato() != null
                && contato.getUltimoContato().isBefore(LocalDateTime.now().minusMinutes(5)))) {

            contato.setEtapaFluxo(EtapaFluxo.INICIO.name());
        }

        contato.setUltimoContato(LocalDateTime.now());

        // 4. Fluxo principal (só entra aqui se for texto!)
        switch (EtapaFluxo.valueOf(contato.getEtapaFluxo())) {

            case INICIO -> {
                sendMessage.sendMessage(from, """
                    Olá! Sou seu assistente financeiro da Easy.
                    
                    O que você gostaria de consultar hoje?
                    
                    1. Resumo financeiro do mês
                    2. Contas a receber
                    3. Contas a pagar
                    4. Fluxo de caixa
                    5. Sair
                    """);
                contato.setEtapaFluxo(EtapaFluxo.MENU_PRINCIPAL.name());
            }

            case MENU_PRINCIPAL -> {
                String msg = mensagem.trim().toLowerCase();

                switch (msg) {
                    case "1" -> {
                        sendMessage.sendMessage(from, """
        RESUMO FINANCEIRO
        
        Escolha o período:
        1. Últimos 7 dias
        2. Últimos 15 dias
        3. Últimos 30 dias
        4. Período personalizado
        
        Digite o número:
        """);
                        contato.setEtapaFluxo(EtapaFluxo.RELATORIO_ESCOLHER_PERIODO.name());
                    }

                    case "2" -> {
                        sendMessage.sendMessage(from, """
                CONTAS A RECEBER
                
                1. Recebidas
                2. Pendentes
                """);
                        contato.setEtapaFluxo(EtapaFluxo.CONTAS_RECEBER_TIPO.name());
                    }

                    case "3" -> {
                        sendMessage.sendMessage(from, """
                CONTAS A PAGAR
                
                1. Pagas
                2. Pendentes
                """);
                        contato.setEtapaFluxo(EtapaFluxo.CONTAS_PAGAR_TIPO.name());
                    }

                    case "4" -> {
                        sendMessage.sendMessage(from, """
                FLUXO DE CAIXA
                
                1. Última semana
                2. Último mês
                """);
                        contato.setEtapaFluxo(EtapaFluxo.FLUXO_CAIXA_PERIODO.name());
                    }

                    case "5", "sair", "tchau" -> {
                        sendMessage.sendMessage(from, "Até logo!");
                        contato.setEtapaFluxo(EtapaFluxo.INICIO.name());
                    }

                    // QUALQUER OUTRA MENSAGEM = VOLTA AO MENU
                    default -> sendMessage.sendMessage(from, """
            O que você gostaria de consultar hoje?
            
            1. Resumo financeiro do mês
            2. Contas a receber
            3. Contas a pagar
            4. Fluxo de caixa
            5. Sair
            """);
                }
            }

            case RELATORIO_ESCOLHER_PERIODO -> {
                switch (mensagem.trim()) {
                    case "1" -> {
                        LocalDate fim = LocalDate.now();
                        LocalDate inicio = fim.minusDays(7);
                        enviarRelatorio(contato, from, inicio, fim, "ÚLTIMOS 7 DIAS");
                    }
                    case "2" -> {
                        LocalDate fim = LocalDate.now();
                        LocalDate inicio = fim.minusDays(15);
                        enviarRelatorio(contato, from, inicio, fim, "ÚLTIMOS 15 DIAS");
                    }
                    case "3" -> {
                        LocalDate fim = LocalDate.now();
                        LocalDate inicio = fim.minusDays(30);
                        enviarRelatorio(contato, from, inicio, fim, "ÚLTIMOS 30 DIAS");
                    }
                    case "4" -> {
                        sendMessage.sendMessage(from, """
                PERÍODO PERSONALIZADO
                
                Digite as datas no formato:
                dd/mm/aaaa até dd/mm/aaaa
                
                Exemplo: 01/10/2025 até 31/10/2025
                """);
                        contato.setEtapaFluxo(EtapaFluxo.RELATORIO_AGUARDANDO_DATAS.name());
                    }
                    default -> {
                        sendMessage.sendMessage(from, "Opção inválida.");
                    }
                }
            }

            case RELATORIO_AGUARDANDO_DATAS -> {
                try {
                    // Aceita vários formatos: "01/10/2025 até 31/10/2025" ou "01/10/2025 31/10/2025"
                    String texto = mensagem.trim().toLowerCase().replace("até", " ").replace("ate", " ");

                    String[] partes = texto.split("\\s+");
                    if (partes.length < 2) throw new Exception();

                    String data1 = partes[0];
                    String data2 = partes[partes.length - 1]; // pega a última

                    LocalDate inicio = LocalDate.parse(data1, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    LocalDate fim = LocalDate.parse(data2, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    if (inicio.isAfter(fim)) {
                        sendMessage.sendMessage(from, "Data inicial maior que a final. Tente novamente.");
                        return;
                    }

                    enviarRelatorio(contato, from, inicio, fim, "PERÍODO PERSONALIZADO");

                } catch (Exception e) {
                    sendMessage.sendMessage(from, """
            Formato inválido!
            
            Use: dd/mm/aaaa até dd/mm/aaaa
            Exemplo: 01/10/2025 até 15/10/2025
            
            Tente novamente:
            """);
                }
            }

            case CONTAS_RECEBER_TIPO -> {
                if ("1".equals(mensagem.trim())) {
                    sendMessage.sendMessage(from, "Aqui estão as contas a receber já pagas...\n(ainda não implementado)");
                    sendMessage.sendMessage(from, "\nDigite qualquer coisa para voltar.");
                    contato.setEtapaFluxo(EtapaFluxo.MENU_PRINCIPAL.name());
                } else if ("2".equals(mensagem.trim())) {
                    sendMessage.sendMessage(from, "Aqui estão as contas a receber pendentes...\n(ainda não implementado)");
                    sendMessage.sendMessage(from, "\nDigite qualquer coisa para voltar.");
                    contato.setEtapaFluxo(EtapaFluxo.MENU_PRINCIPAL.name());
                } else {
                    sendMessage.sendMessage(from, "Por favor, digite 1 ou 2.");
                }
            }


            default -> {
                sendMessage.sendMessage(from, "Erro no fluxo. Reiniciando...");
                contato.setEtapaFluxo(EtapaFluxo.INICIO.name());
            }
        }

        numRepo.save(contato);
    }

    private void enviarRelatorio(NumeroCadastrado contato, String from, LocalDate inicio, LocalDate fim, String titulo) {
        try {
            WeeklyReportDTO rel = omieDataService.gerarRelatorioPorContato(
                    contato, inicio, fim, true
            );

            String texto = """
            %s
            Período: %s a %s
            
            Entradas: %s
            Saidas: %s
            
            Receita operacional..: R$ %s
            Custos variáveis.....: R$ %s
            Despesas fixas.......: R$ %s
            Resultado operacional: R$ %s
            """.formatted(
                    titulo,
                    inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    rel.getReceitaOperacional_1_0(),
                    rel.getTotalRecebido(),
                    rel.getTotalPago(),
                    rel.getCustosVariaveis_2_1(),
                    rel.getDespesasFixas_3x(),
                    rel.getResultadoOperacional()
            );

            sendMessage.sendMessage(from, texto);
            sendMessage.sendMessage(from, "\nDigite  coisa para voltar ao menu.");

        } catch (Exception e) {
            sendMessage.sendMessage(from, "Erro ao gerar relatório. Tente novamente mais tarde.");
        }
    }
}