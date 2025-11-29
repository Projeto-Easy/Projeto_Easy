package com.squad10.chatboteasy.service;

import com.squad10.chatboteasy.dto.report.WeeklyReportDTO;
import com.squad10.chatboteasy.enums.EtapaFluxo;
import com.squad10.chatboteasy.model.MovimentoEnriquecido;
import com.squad10.chatboteasy.repository.NumeroCadastradoRepository;
import com.squad10.chatboteasy.tables.Empresa;
import com.squad10.chatboteasy.tables.NumeroCadastrado;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatLogic {

    private final SendMessage sendMessage;
    private final NumeroCadastradoRepository numRepo;
    private final OmieDataService omieDataService;

    @SneakyThrows
    public void chatFlux(String from, String mensagem, String tipo) {


        // 1. Verifier se o número está cadastrado
        if (!numRepo.existsByNumero(from)) {
            sendMessage.sendMessage(from, "Número não cadastrado no sistema.");
            return;
        }

        NumeroCadastrado contato = numRepo.findByNumero(from).get();

        // 2. Se não for mensagem de texto responde e NÃO muda a etapa
        if (!"text".equalsIgnoreCase(tipo) && !"interactive".equalsIgnoreCase(tipo)) {
            sendMessage.sendMessage(from, "Desculpe, só aceito mensagens de texto por enquanto.");
            return;
        }

        // 3. Primeiro contato: define etapa inicial
        if (
                contato.getEtapaFluxo() == null
                || contato.getEtapaFluxo().isBlank()
                || (contato.getUltimoContato() != null && contato.getUltimoContato().isBefore(LocalDateTime.now().minusMinutes(5)))
        ) {
            contato.setEtapaFluxo(EtapaFluxo.INICIO.name());
        }

        contato.setUltimoContato(LocalDateTime.now());

        // 4. Fluxo principal
        switch (EtapaFluxo.valueOf(contato.getEtapaFluxo())) {

            case INICIO -> {
                sendMessage.sendInteractiveMenuPrincipal(from);
                contato.setEtapaFluxo(EtapaFluxo.MENU_PRINCIPAL.name());
            }

            case MENU_PRINCIPAL -> {
                switch (mensagem) {
                    case "1" -> {
                        sendMessage.sendInteractiveResumoFinanceiro(from);
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

                    default -> sendMessage.sendMessage(from, """
                    Escolha uma opção do menu ou envie o número correspondente.
                    """);
                }
            }

            case RELATORIO_ESCOLHER_PERIODO -> {
                switch (mensagem) {
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
                        sendMessage.sendMessage(from, "Escolha uma opção do menu ou envie o número correspondente");
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
                Empresa empresa = contato.getEmpresa();
                String appKey = empresa.getOmieAppKey();
                String appSecret = empresa.getOmieAppSecret();

                LocalDate fim = LocalDate.now();
                LocalDate inicio = fim.minusDays(90); // últimos 90 dias é um bom padrão

                List<MovimentoEnriquecido> contas;

                if ("1".equals(mensagem.trim())) {
                    contas = omieDataService.buscarContasAReceber(appKey, appSecret, inicio, fim, true);
                    sendMessage.sendMessage(from, """
                CONTAS A RECEBER - JÁ RECEBIDAS
                Período: %s a %s
                Total encontrado: %d
                """.formatted(inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), contas.size()));
                } else if ("2".equals(mensagem.trim())) {
                    contas = omieDataService.buscarContasAReceber(appKey, appSecret, inicio, fim, false);
                    sendMessage.sendMessage(from, """
                CONTAS A RECEBER - PENDENTES
                Período: %s a %s
                Total encontrado: %d
                """.formatted(inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), contas.size()));
                } else {
                    sendMessage.sendMessage(from, "Por favor, digite 1 ou 2.");
                    return;
                }

                if (contas.isEmpty()) {
                    sendMessage.sendMessage(from, "Nenhuma conta encontrada nesse critério.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    int limite = Math.min(contas.size(), 15);
                    for (int i = 0; i < limite; i++) {
                        var m = contas.get(i);
                        String status = OmieDataService.isPagoOuRecebido(m.getStatus()) ? "✔" : "⏳";
                        sb.append(String.format("%s %s - R$ %s - %s%n",
                                m.getDataPagamento().format(DateTimeFormatter.ofPattern("dd/MM")),
                                status,
                                omieDataService.formatoMoeda(m.getValor()),
                                m.getDescCategoria() != null ? m.getDescCategoria().trim() : "Sem descrição"));
                    }
                    if (contas.size() > limite) {
                        sb.append(String.format("%n... e mais %d registros", contas.size() - limite));
                    }
                    sendMessage.sendMessage(from, sb.toString());
                }

                sendMessage.sendRepetirQuestion(from);
                contato.setEtapaFluxo(EtapaFluxo.REPETIR.name());
            }

            case CONTAS_PAGAR_TIPO -> {
                Empresa empresa = contato.getEmpresa();
                String appKey = empresa.getOmieAppKey();
                String appSecret = empresa.getOmieAppSecret();

                LocalDate fim = LocalDate.now();
                LocalDate inicio = fim.minusDays(90);

                List<MovimentoEnriquecido> contas;

                if ("1".equals(mensagem.trim())) {
                    contas = omieDataService.buscarContasAPagar(appKey, appSecret, inicio, fim, true);
                    sendMessage.sendMessage(from, """
                CONTAS A PAGAR - JÁ PAGAS
                Período: %s a %s
                Total: %d
                """.formatted(inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), contas.size()));
                }
                else if ("2".equals(mensagem.trim())) {
                    contas = omieDataService.buscarContasAPagar(appKey, appSecret, inicio, fim, false);
                    sendMessage.sendMessage(from, """
                CONTAS A PAGAR - PENDENTES
                Período: %s a %s
                Total: %d
                """.formatted(inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), contas.size()));
                }
                else {
                    sendMessage.sendMessage(from, "Por favor, digite 1 ou 2.");
                    return;
                }

                // mesmo padrão de exibição das contas a receber
                if (contas.isEmpty()) {
                    sendMessage.sendMessage(from, "Nenhuma conta encontrada.");
                }
                else {
                    StringBuilder sb = new StringBuilder();
                    int limite = Math.min(contas.size(), 15);
                    for (int i = 0; i < limite; i++) {
                        var m = contas.get(i);
                        String status = OmieDataService.isPagoOuRecebido(m.getStatus()) ? "✔" : "⏳";
                        sb.append(String.format("%s %s - R$ %s - %s%n",
                                m.getDataPagamento().format(DateTimeFormatter.ofPattern("dd/MM")),
                                status,
                                omieDataService.formatoMoeda(m.getValor()),
                                m.getDescCategoria() != null ? m.getDescCategoria().trim() : "Sem descrição"));
                    }
                    if (contas.size() > limite) sb.append("\n... e mais ").append(contas.size() - limite);
                    sendMessage.sendMessage(from, sb.toString());
                }

                sendMessage.sendRepetirQuestion(from);
                contato.setEtapaFluxo(EtapaFluxo.REPETIR.name());
            }

            case FLUXO_CAIXA_PERIODO -> {
                Empresa empresa = contato.getEmpresa();
                LocalDate inicio, fim = LocalDate.now();;

                if ("1".equals(mensagem.trim())) {
                    inicio = fim.minusDays(7);
                } else if ("2".equals(mensagem.trim())) {
                    inicio = fim.minusMonths(1).withDayOfMonth(1);
                } else {
                    sendMessage.sendMessage(from, "Opção inválida.");
                    return;
                }

                String resposta = "Fluxo de caixa";

                var linhas = omieDataService.gerarFluxoDeCaixaTexto(
                        empresa.getOmieAppKey(),
                        empresa.getOmieAppSecret(),
                        inicio, fim);

                for (String linha : linhas) {
                    resposta = resposta.concat("\n" + linha);
                }

                sendMessage.sendMessage(from, resposta);

                sendMessage.sendRepetirQuestion(from);
                contato.setEtapaFluxo(EtapaFluxo.REPETIR.name());
            }

            case REPETIR -> {
                if("sim".equalsIgnoreCase(mensagem.trim())){
                    sendMessage.sendInteractiveMenuPrincipal(from);
                    contato.setEtapaFluxo(EtapaFluxo.MENU_PRINCIPAL.name());
                } else if("não".equalsIgnoreCase(mensagem.trim()) || "nao".equalsIgnoreCase(mensagem.trim())){
                    sendMessage.sendAgradecerContato(from);
                    contato.setEtapaFluxo(EtapaFluxo.INICIO.name());
                } else{
                    sendMessage.sendMessage(from, "Opção invalida, digite sim ou não");
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

            // Formatação dos valores para garantir o padrão R$ X.XXX,XX
            DecimalFormat df = new DecimalFormat("R$ #,##0.00");
            df.setRoundingMode(RoundingMode.HALF_UP);

            String texto = """
        📈 *%s*
        
        🗓️ *Período:* %s a %s
        
        ~⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯~
        
        💰 *BALANÇO DE CAIXA*
        * Entradas: %s
        * Saídas: %s
        
        ~⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯~
        
        📊 *DEMONSTRAÇÃO DE RESULTADOS*
        * Receita Operacional: %s
        * Custos Variáveis: %s
        * Despesas Fixas: %s
        
        ~⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯~
        
        ⭐ *RESULTADO FINAL*
        * *Resultado Operacional:* *%s*
        """.formatted(
                    // 1. Título (passado como argumento, ex: "ÚLTIMOS 7 DIAS")
                    titulo,
                    // 2. Data de Início
                    inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    // 3. Data de Fim
                    fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    // 4. Entradas (Total Recebido)
                    df.format(rel.getTotalRecebido()),
                    // 5. Saídas (Total Pago)
                    df.format(rel.getTotalPago()),
                    // 6. Receita Operacional
                    df.format(rel.getReceitaOperacional_1_0()),
                    // 7. Custos Variáveis
                    df.format(rel.getCustosVariaveis_2_1()),
                    // 8. Despesas Fixas
                    df.format(rel.getDespesasFixas_3x()),
                    // 9. Resultado Operacional (Destaque máximo)
                    df.format(rel.getResultadoOperacional())
            );

            sendMessage.sendMessage(from, texto);
            sendMessage.sendRepetirQuestion(from);
            contato.setEtapaFluxo(EtapaFluxo.REPETIR.name());

        } catch (Exception e) {
            sendMessage.sendMessage(from, "Erro ao gerar relatório. Tente novamente mais tarde.");
        }
    }
}