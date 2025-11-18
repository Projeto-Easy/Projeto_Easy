package com.squad10.chatboteasy.service;

import com.squad10.chatboteasy.dto.report.WeeklyReportDTO;
import com.squad10.chatboteasy.repository.NumeroCadastradoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ChatLogic {

    private final SendMessage sendMessage;
    private final NumeroCadastradoRepository numRepo;
    private final OmieDataService omieDataService;

    public void chatFlux (String from, String mensagem, String tipo){

        if(numRepo.existsByNumero(from)){

            if (tipo.equals("text")){
                handleTextMessage(from, mensagem);
            } else handleNonTextMessage(from, mensagem);

        }
    }

    public void handleTextMessage(String from, String mensagem) {
        switch (mensagem) {
            case "relatorio":
                WeeklyReportDTO rel = omieDataService.gerarRelatorio("5614700718627", "2ae8328ce879960d99ba83e7986805a3", LocalDate.now().minusDays(365), LocalDate.now(), true);
                String resposta = String.format(
                        """
                                \n==== RELATÓRIO EASY ====
                                Período .............: %s a %s
                                Total Recebido ......: R$ %s
                                Total Pago ..........: R$ %s
                                
                                1.0 Receita Operacional: R$ %s
                                2.1 Custos Variáveis ..: R$ %s
                                3.x Despesas Fixas ....: R$ %s
                                
                                Resultado Operacional : R$ %s
                                ==========================
                                """,
                        rel.getInicio(),
                        rel.getFim(),
                        rel.getTotalRecebido(),
                        rel.getTotalPago(),
                        rel.getReceitaOperacional_1_0(),
                        rel.getCustosVariaveis_2_1(),
                        rel.getDespesasFixas_3x(),
                        rel.getResultadoOperacional()
                );
                sendMessage.sendMessage(from, resposta);
                break;

            case "oi":
                sendMessage.sendMessage(from, "olá!");
                break;

            default:
                sendMessage.sendMessage(from, "mensagem não compreendida!");
                break;
        }
    }

    public void handleNonTextMessage(String from, String mensagem) {
        String resposta = String.format("Mensagems do tipo %s não são suportadas.", mensagem);
        sendMessage.sendMessage(from, resposta);
    }

}

