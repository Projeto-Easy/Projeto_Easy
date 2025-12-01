package com.squad10.chatboteasy.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.squad10.chatboteasy.dto.report.WeeklyReportDTO;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfReportService {

    private final TemplateEngine templateEngine;

    public byte[] renderWeeklyReport(Map<String, Object> model) {
        Context ctx = new Context(new Locale("pt", "BR"));
        ctx.setVariables(model);

        String html = templateEngine.process("report-weekly", ctx);

// remove BOM e qualquer “lixo” antes do <html>
html = html.replace("\uFEFF", "");      // BOM real
html = html.replace("ï»¿", "");         // BOM “visível” quando veio zoado
html = html.trim();


        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }
    public byte[] gerarResumoFinanceiroPdf(String empresaNome, LocalDate inicio, LocalDate fim, WeeklyReportDTO rel) {
    DecimalFormat df = new DecimalFormat("R$ #,##0.00");
    df.setRoundingMode(RoundingMode.HALF_UP);

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    Map<String, Object> model = new HashMap<>();
    model.put("title", "Resumo Financeiro (Semanal)");
    model.put("empresaNome", empresaNome);
    model.put("periodo", inicio.format(dtf) + " a " + fim.format(dtf));

    model.put("totalRecebido", df.format(rel.getTotalRecebido()));
    model.put("totalPago", df.format(rel.getTotalPago()));
    model.put("resultado", df.format(rel.getResultadoOperacional()));

    // Se seu HTML tiver tabela "linhas", você pode preencher depois.
    model.put("linhas", java.util.List.of()); // vazio por enquanto

    return renderWeeklyReport(model);
}

}
