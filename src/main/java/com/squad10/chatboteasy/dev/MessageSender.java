package com.squad10.chatboteasy.dev;

public interface MessageSender {
    void sendMessage(String to, String text);

    void sendInteractiveMenuPrincipal(String to);
    void sendInteractiveResumoFinanceiro(String to);

    void sendRepetirQuestion(String to);
    void sendAgradecerContato(String to);

    void sendPdf(String to, String filename, byte[] pdfBytes);
    void sendInteractivePdfPeriodo(String to);


}
