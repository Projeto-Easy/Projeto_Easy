package com.squad10.chatboteasy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SendMessage {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${whats.api.token}")
    String apiToken;

    @Value("${whats.phone.number.id}")
    String phoneId;
    @Value("${whats.mock:false}")

    public void sendMessage(String to, String text) {

        String url = "https://graph.facebook.com/v22.0/" + phoneId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("to", to);
        payload.put("type", "text");

        Map<String, String> textObj = new HashMap<>();
        textObj.put("body", text);
        payload.put("text", textObj);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.printf("Resposta enviada para : %s\n", to);
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem: " + e);
        }

    }

    public void sendInteractiveMenuPrincipal(String to){

        String url = "https://graph.facebook.com/v22.0/" + phoneId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();

        payload.put("messaging_product", "whatsapp");
        payload.put("recipient_type", "individual");
        payload.put("to", to);
        payload.put("type", "interactive");

        // interactive root
        Map<String, Object> interactiveObj = new HashMap<>();
        interactiveObj.put("type", "list");

        // header
        Map<String, Object> headerObj = new HashMap<>();
        headerObj.put("type", "text");
        headerObj.put("text", "EasyBOT");

        // body
        Map<String, Object> bodyObj = new HashMap<>();
        bodyObj.put("text", "Olá sou o assistente da Easy financeira Como eu posso te ajudar hoje ?");

        // footer
        Map<String, Object> footerObj = new HashMap<>();
        footerObj.put("text", "Pressione o menu abaixo ou escreva o número correspondente.");

        // action
        Map<String, Object> actionObj = new HashMap<>();
        actionObj.put("button", "Menu");

        // rows
        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(Map.of(
                "id", "1",
                "title", "1. Resumo do financeiro",
                "description", "Solicite seu resumo financeiro"
        ));
        rows.add(Map.of(
                "id", "2",
                "title", "2. Contas a receber",
                "description", "Lista de contas a serem recebidas"
        ));
        rows.add(Map.of(
                "id", "3",
                "title", "3. Contas a pagar",
                "description", "Lista de contas a serem pagas"
        ));
        rows.add(Map.of(
                "id", "4",
                "title", "4. Fluxo de caixa",
                "description", "Fluxo de caixa da empresa"
        ));
        rows.add(Map.of(
                "id", "5",
                "title", "5. Relatório em PDF",
                "description", "Gerar PDF do resumo das movimentações recentes"
        ));
        rows.add(Map.of(
           "id", "6",
            "title", "6. Sair",
            "description", "Encerra a conversa"
        ));

        // section
        Map<String, Object> section = new HashMap<>();
        section.put("title", "Menu de opções Easy");
        section.put("rows", rows);

        List<Map<String, Object>> sections = new ArrayList<>();
        sections.add(section);

        actionObj.put("sections", sections);

        // mount interactive object
        interactiveObj.put("header", headerObj);
        interactiveObj.put("body", bodyObj);
        interactiveObj.put("footer", footerObj);
        interactiveObj.put("action", actionObj);

        payload.put("interactive", interactiveObj);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.printf("Resposta enviada para : %s\n", to);
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem: " + e);
        }

    }

    public void sendInteractiveResumoFinanceiro(String to){

        String url = "https://graph.facebook.com/v22.0/" + phoneId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();

        payload.put("messaging_product", "whatsapp");
        payload.put("recipient_type", "individual");
        payload.put("to", to);
        payload.put("type", "interactive");

        // interactive root
        Map<String, Object> interactiveObj = new HashMap<>();
        interactiveObj.put("type", "list");

        // header
        Map<String, Object> headerObj = new HashMap<>();
        headerObj.put("type", "text");
        headerObj.put("text", "EasyBOT: Resumo financeiro");

        // body
        Map<String, Object> bodyObj = new HashMap<>();
        bodyObj.put("text", "Escolha o periodo que deseja consultar");

        // footer
        Map<String, Object> footerObj = new HashMap<>();
        footerObj.put("text", "");

        // action
        Map<String, Object> actionObj = new HashMap<>();
        actionObj.put("button", "Periodos");

        // rows
        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(Map.of(
                "id", "1",
                "title", "1. Últimos 7 dias"
        ));
        rows.add(Map.of(
                "id", "2",
                "title", "2. Últimos 15 dias"
        ));
        rows.add(Map.of(
                "id", "3",
                "title", "3. Últimos 30 dias"
        ));
        rows.add(Map.of(
                "id", "4",
                "title", "4. Período personalizado"
        ));

        // section
        Map<String, Object> section = new HashMap<>();
        section.put("title", "Resumo financeiro");
        section.put("rows", rows);

        List<Map<String, Object>> sections = new ArrayList<>();
        sections.add(section);

        actionObj.put("sections", sections);

        interactiveObj.put("header", headerObj);
        interactiveObj.put("body", bodyObj);
        interactiveObj.put("footer", footerObj);
        interactiveObj.put("action", actionObj);

        payload.put("interactive", interactiveObj);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.printf("Resposta enviada para : %s\n", to);
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem: " + e);
        }

    }
    public void sendInteractivePdfPeriodo(String to){

    String url = "https://graph.facebook.com/v22.0/" + phoneId + "/messages";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(apiToken);
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, Object> payload = new HashMap<>();
    payload.put("messaging_product", "whatsapp");
    payload.put("recipient_type", "individual");
    payload.put("to", to);
    payload.put("type", "interactive");

    Map<String, Object> interactiveObj = new HashMap<>();
    interactiveObj.put("type", "list");

    Map<String, Object> headerObj = new HashMap<>();
    headerObj.put("type", "text");
    headerObj.put("text", "EasyBOT: PDF");

    Map<String, Object> bodyObj = new HashMap<>();
    bodyObj.put("text", "Escolha o período do relatório em PDF:");

    Map<String, Object> footerObj = new HashMap<>();
    footerObj.put("text", "Selecione uma opção abaixo.");

    Map<String, Object> actionObj = new HashMap<>();
    actionObj.put("button", "Períodos PDF");

    List<Map<String, Object>> rows = new ArrayList<>();
    rows.add(Map.of(
            "id", "1",
            "title", "Últimos 7 dias",
            "description", "Gera o PDF do resumo dos últimos 7 dias"
    ));
    rows.add(Map.of(
            "id", "2",
            "title", "Período personalizado",
            "description", "Escolha datas no formato dd/MM/aaaa até dd/MM/aaaa"
    ));

    Map<String, Object> section = new HashMap<>();
    section.put("title", "Relatório em PDF");
    section.put("rows", rows);

    actionObj.put("sections", List.of(section));

    interactiveObj.put("header", headerObj);
    interactiveObj.put("body", bodyObj);
    interactiveObj.put("footer", footerObj);
    interactiveObj.put("action", actionObj);

    payload.put("interactive", interactiveObj);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

    try {
        restTemplate.postForEntity(url, request, String.class);
        System.out.printf("Resposta enviada para : %s\n", to);
    } catch (Exception e) {
        System.out.println("Erro ao enviar mensagem: " + e);
    }
}

    public void sendRepetirQuestion(String to){
        sendMessage(to, "Deseja realizar outra consulta ? sim ou não");
    }

    public void sendAgradecerContato(String to){
        sendMessage(to, "Até logo!");
    }
}
