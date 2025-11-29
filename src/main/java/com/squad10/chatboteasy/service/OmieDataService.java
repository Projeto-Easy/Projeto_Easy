package com.squad10.chatboteasy.service;

import com.squad10.chatboteasy.client.OmieApiClient;
import com.squad10.chatboteasy.dto.omie.OmieCategoriaRaw;
import com.squad10.chatboteasy.dto.omie.OmieMoviment;
import com.squad10.chatboteasy.model.MovimentoEnriquecido;
import com.squad10.chatboteasy.tables.Empresa;
import com.squad10.chatboteasy.tables.NumeroCadastrado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OmieDataService {

    private final OmieApiClient omie;

    public Map<String, OmieCategoriaRaw> mapCategorias(String appKey, String appSecret) {
        var lista = omie.listarCategorias(appKey, appSecret);
        System.out.println("Categorias (brutas) recebidas: " + lista.size());

        lista.stream()
                .filter(c -> c.getCCodCateg() == null || c.getCCodCateg().isBlank())
                .limit(10)
                .forEach(c -> System.out.println("Categoria sem código: " + c.getDescricao()));

        return lista.stream()
                .filter(c -> c.getCCodCateg() != null && !c.getCCodCateg().isBlank())
                .collect(Collectors.toMap(OmieCategoriaRaw::getCCodCateg, c -> c, (a, b) -> a));
    }

    public List<MovimentoEnriquecido> buscarMovimentosEnriquecidos(
            String appKey, String appSecret,
            LocalDate inicio, LocalDate fim,
            boolean somentePagoOuRecebido
    ) {
        var categorias = mapCategorias(appKey, appSecret);
        var brutos = omie.listarMovimentos(appKey, appSecret);
        System.out.println("Movimentos (brutos) recebidos: " + brutos.size());

        var mapeados = brutos.stream()
                .map(m -> toEnriquecido(m, categorias))
                .filter(Objects::nonNull)
                .toList();
        System.out.println("Após mapear/enriquecer: " + mapeados.size());

        var comData = mapeados.stream()
                .filter(m -> m.getDataPagamento() != null)
                .toList();
        System.out.println("Com dataPagamento != null: " + comData.size());

        var noPeriodo = comData.stream()
                .filter(m -> (inicio == null || !m.getDataPagamento().isBefore(inicio)))
                .filter(m -> (fim == null || !m.getDataPagamento().isAfter(fim)))
                .toList();
        System.out.println("Dentro do período: " + noPeriodo.size() +
                " (inicio=" + inicio + ", fim=" + fim + ")");

        var porStatus = noPeriodo.stream()
                .filter(m -> !somentePagoOuRecebido || isPagoOuRecebido(m.getStatus()))
                .toList();
        System.out.println("Após filtro status (somente=" + somentePagoOuRecebido + "): " + porStatus.size());

        porStatus.stream().limit(5).forEach(m ->
                System.out.println("%s | %s | %s | R$ %s | %s (%s)".formatted(
                        m.getDataPagamento(), m.getGrupo(), m.getStatus(),
                        m.getValor(), m.getDescCategoria(), m.getEasyCode()
                ))
        );

        return porStatus;
    }

    private MovimentoEnriquecido toEnriquecido(OmieMoviment raw, Map<String, OmieCategoriaRaw> catById) {
        if (raw == null || raw.getDetalhes() == null || raw.getResumo() == null) return null;

        var det = raw.getDetalhes();
        var res = raw.getResumo();

        String cod = det.getCCodCateg();

        String desc = Optional.ofNullable(catById.get(cod))
                .map(OmieCategoriaRaw::getDescricao)
                .orElse("Sem descrição (" + cod + ")");

        String easy = extrairEasyCode(desc, cod);

        LocalDate data = parseDateFlexible(det.getDDtPagamento());
        if (data == null) data = parseDateFlexible(det.getDDtPrevisao());
        if (data == null) data = parseDateFlexible(det.getDDtEmissao());

        if (data == null) return null;

        return MovimentoEnriquecido.builder()
                .dataPagamento(data)
                .grupo(det.getCGrupo())
                .status(det.getCStatus())
                .valor(Optional.ofNullable(res.getNValPago()).orElse(BigDecimal.ZERO))
                .codCategoria(cod)
                .descCategoria(desc)
                .easyCode(easy)
                .build();
    }

    private static LocalDate parseDateFlexible(String s) {
        if (s == null || s.isBlank()) return null;
        s = s.trim();
        try {
            if (s.contains("-")) return LocalDate.parse(s);
            if (s.contains("/")) return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception ignored) {}
        return null;
    }

    public static boolean isPagoOuRecebido(String s) {
        if (s == null) return false;
        String x = s.trim().toUpperCase();
        return x.equals("PAGO") || x.equals("RECEBIDO")
                || x.equals("BAIXADO") || x.equals("LIQUIDADO");
    }

    private static String extrairEasyCode(String descricao, String cCodCateg) {
        if (descricao != null) {
            int sp = descricao.indexOf(' ');
            if (sp > 0) {
                String prefixo = descricao.substring(0, sp).trim();
                if (prefixo.matches("\\d+(\\.\\d+)?")) return prefixo;
            }
        }
        if (cCodCateg == null) return null;
        String cod = cCodCateg.trim();
        if (cod.startsWith("1.")) return "1.0";
        if (cod.startsWith("2.")) return "2.1";
        if (cod.startsWith("3.0")) return "3.0";
        if (cod.startsWith("3.1")) return "3.1";
        if (cod.startsWith("3.2")) return "3.2";
        return null;
    }
private static BigDecimal somaPorEasy(
        List<MovimentoEnriquecido> itens,
        String easyCode
) {
    return itens.stream()
            .filter(i -> easyCode.equals(i.getEasyCode()))
            .map(i -> i.getValor() == null ? BigDecimal.ZERO : i.getValor())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}

public com.squad10.chatboteasy.dto.report.WeeklyReportDTO gerarRelatorio(
        String appKey, String appSecret,
        LocalDate inicio, LocalDate fim,
        boolean somentePagoOuRecebido
) {
    var itens = buscarMovimentosEnriquecidos(appKey, appSecret, inicio, fim, somentePagoOuRecebido);

    var totalRecebido = itens.stream()
            .filter(i -> "CONTA_A_RECEBER".equalsIgnoreCase(i.getGrupo()))
            .map(i -> i.getValor() == null ? BigDecimal.ZERO : i.getValor())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    var totalPago = itens.stream()
            .filter(i -> "CONTA_A_PAGAR".equalsIgnoreCase(i.getGrupo()))
            .map(i -> i.getValor() == null ? BigDecimal.ZERO : i.getValor())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    var v1_0 = somaPorEasy(itens, "1.0");
    var v2_1 = somaPorEasy(itens, "2.1");
    var v3_0 = somaPorEasy(itens, "3.0");
    var v3_1 = somaPorEasy(itens, "3.1");
    var v3_2 = somaPorEasy(itens, "3.2");
    var v3x  = v3_0.add(v3_1).add(v3_2);

    var resultado = v1_0.subtract(v2_1).subtract(v3_0).subtract(v3_1).subtract(v3_2);

    return com.squad10.chatboteasy.dto.report.WeeklyReportDTO.builder()
            .inicio(inicio)
            .fim(fim)
            .somentePagoOuRecebido(somentePagoOuRecebido)
            .totalRecebido(totalRecebido)
            .totalPago(totalPago)
            .receitaOperacional_1_0(v1_0)
            .custosVariaveis_2_1(v2_1)
            .despesasFixas_3x(v3x)
            .resultadoOperacional(resultado)
            .itens(itens) // remova se não quiser retornar itens
            .build();
}

    public com.squad10.chatboteasy.dto.report.WeeklyReportDTO gerarRelatorioPorContato(NumeroCadastrado contato,
                                                                                       LocalDate dataInicio, LocalDate dataFim, boolean detalhado) {
        Empresa empresa = contato.getEmpresa();

        if (empresa == null || empresa.getOmieAppKey() == null) {
            throw new RuntimeException("Empresa não configurada para este número");
        }

        return gerarRelatorio(
                empresa.getOmieAppKey(),
                empresa.getOmieAppSecret(),
                dataInicio,
                dataFim,
                detalhado
        );
}
    // ======== NOVOS MÉTODOS PARA CONTAS A RECEBER/PAGAR ========
    public List<MovimentoEnriquecido> buscarContasAReceber(String appKey, String appSecret,
                                                           LocalDate inicio, LocalDate fim,
                                                           boolean apenasPagas) {
        var todos = buscarMovimentosEnriquecidos(appKey, appSecret, inicio, fim, false); // traz tudo

        return todos.stream()
                .filter(m -> "CONTA_A_RECEBER".equalsIgnoreCase(m.getGrupo()))
                .filter(m -> !apenasPagas || isPagoOuRecebido(m.getStatus())) // se apenasPagas=true → só pagas
                .filter(m -> apenasPagas || !isPagoOuRecebido(m.getStatus())) // se apenasPagas=false → só pendentes
                .sorted((a, b) -> b.getDataPagamento().compareTo(a.getDataPagamento())) // mais recente primeiro
                .toList();
    }

    public List<MovimentoEnriquecido> buscarContasAPagar(String appKey, String appSecret,
                                                         LocalDate inicio, LocalDate fim,
                                                         boolean apenasPagas) {
        var todos = buscarMovimentosEnriquecidos(appKey, appSecret, inicio, fim, false);

        return todos.stream()
                .filter(m -> "CONTA_A_PAGAR".equalsIgnoreCase(m.getGrupo()))
                .filter(m -> !apenasPagas || isPagoOuRecebido(m.getStatus()))
                .filter(m -> apenasPagas || !isPagoOuRecebido(m.getStatus()))
                .sorted((a, b) -> b.getDataPagamento().compareTo(a.getDataPagamento()))
                .toList();
    }

    // ======== FLUXO DE CAIXA SIMPLES (SALDO DIA A DIA) ========
    public List<String> gerarFluxoDeCaixaTexto(String appKey, String appSecret,
                                               LocalDate inicio, LocalDate fim) {
        var movimentos = buscarMovimentosEnriquecidos(appKey, appSecret, inicio, fim, true); // só pagos/recebidos

        Map<LocalDate, BigDecimal> saldoPorDia = movimentos.stream()
                .collect(Collectors.groupingBy(
                        MovimentoEnriquecido::getDataPagamento,
                        Collectors.reducing(BigDecimal.ZERO,
                                m -> "CONTA_A_RECEBER".equalsIgnoreCase(m.getGrupo()) ? m.getValor() : m.getValor().negate(),
                                BigDecimal::add)
                ));

        BigDecimal saldoAcumulado = BigDecimal.ZERO;
        List<String> linhas = new ArrayList<>();
        linhas.add("FLUXO DE CAIXA");
        linhas.add("Período: " + inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " a " + fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        linhas.add("══════════════════");

        for (LocalDate dia = inicio; !dia.isAfter(fim); dia = dia.plusDays(1)) {
            BigDecimal movimentoDoDia = saldoPorDia.getOrDefault(dia, BigDecimal.ZERO);
            saldoAcumulado = saldoAcumulado.add(movimentoDoDia);

            String linha = String.format("%s → %s %8s   (saldo: %s)",
                    dia.format(DateTimeFormatter.ofPattern("dd/MM")),
                    movimentoDoDia.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "-",
                    formatoMoeda(movimentoDoDia.abs()),
                    formatoMoeda(saldoAcumulado));

            linhas.add(linha);
        }

        linhas.add("══════════════════");
        linhas.add("SALDO FINAL: " + formatoMoeda(saldoAcumulado));

        return linhas;
    }

    public String formatoMoeda(BigDecimal valor) {
        if (valor == null) return "R$ 0,00";
        return "R$ " + String.format("%,.2f", valor).replace(",", "X")
                .replace(".", ",").replace("X", ".");
    }
}