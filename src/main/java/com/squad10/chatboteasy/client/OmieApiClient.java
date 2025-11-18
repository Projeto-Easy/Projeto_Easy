package com.squad10.chatboteasy.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squad10.chatboteasy.dto.omie.OmieCategoriaRaw;
import com.squad10.chatboteasy.dto.omie.OmieListResponse;
import com.squad10.chatboteasy.dto.omie.OmieMoviment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OmieApiClient {

    private final RestClient omieRestClient;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String MF_ENDPOINT    = "/api/v1/financas/mf/";
    private static final String CATEG_ENDPOINT = "/api/v1/geral/categorias/";

    public List<OmieMoviment> listarMovimentos(String appKey, String appSecret) {
        int page = 1;
        int pageSize = 500;
        List<OmieMoviment> out = new ArrayList<>();

        while (true) {
            Map<String, Object> body = new HashMap<>();
            body.put("call", "ListarMovimentos");
            body.put("app_key", appKey);
            body.put("app_secret", appSecret);
            body.put("param", List.of(Map.of(
                    "nPagina", page,
                    "nRegPorPagina", pageSize
            )));
            String raw = omieRestClient.post()
                    .uri(MF_ENDPOINT)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            OmieListResponse<OmieMoviment> resp;
            try {
                resp = MAPPER.readValue(raw, new TypeReference<OmieListResponse<OmieMoviment>>() {});
            } catch (Exception e) {
                System.err.println("Falha ao parsear resposta de ListarMovimentos: " + e.getMessage());
                break;
            }

            if (resp == null || resp.getCadastro() == null || resp.getCadastro().isEmpty()) {
                break;
            }

            out.addAll(resp.getCadastro());
            if (resp.isLastPage(pageSize)) break;
            page++;
        }
        return out;
    }

public List<OmieCategoriaRaw> listarCategorias(String appKey, String appSecret) {
    int page = 1;
    int pageSize = 500;
    List<OmieCategoriaRaw> out = new ArrayList<>();

    while (true) {
        Map<String, Object> body = new HashMap<>();
        body.put("call", "ListarCategorias");
        body.put("app_key", appKey);
        body.put("app_secret", appSecret);
        body.put("param", List.of(Map.of(
                "pagina", page,
                "registros_por_pagina", pageSize
        )));
        String raw = omieRestClient.post()
                .uri(CATEG_ENDPOINT)
                .body(body)
                .retrieve()
                .body(String.class);
        try {
            var root = MAPPER.readTree(raw);

            List<String> candidateKeys = List.of(
                    "categorias", "categoria", "cadastro",
                    "categoriasCadastro", "categoriaCadastro",
                    "lista", "listaCadastro", "listaCategorias"
            );

            List<OmieCategoriaRaw> pageItems = null;

            for (String key : candidateKeys) {
                var node = root.get(key);
                if (node != null && node.isArray() && node.size() > 0) {

                    pageItems = MAPPER.convertValue(node, new com.fasterxml.jackson.core.type.TypeReference<List<OmieCategoriaRaw>>() {});
                    break;
                }
            }

            // 2) Fallback: achar QUALQUER array com objetos que tenham cCodCateg/descricao
            if (pageItems == null) {
                var fields = root.fields();
                while (fields.hasNext()) {
                    var entry = fields.next();
                    var node = entry.getValue();
                    if (node.isArray() && node.size() > 0 && node.get(0).isObject()) {
                        var first = node.get(0);
                        boolean looksLikeCategoria =
                                (first.has("cCodCateg") || first.has("descricao"));
                        if (looksLikeCategoria) {
                            pageItems = MAPPER.convertValue(node, new com.fasterxml.jackson.core.type.TypeReference<List<OmieCategoriaRaw>>() {});
                            break;
                        }
                    }
                }
            }

            if (pageItems == null || pageItems.isEmpty()) {
                // Sem itens nesta página -> encerra
                break;
            }

            out.addAll(pageItems);

            // 3) Paginação: se existir total_de_paginas/pagina, respeita
            Integer totalDePaginas = getIntSafely(root, "total_de_paginas");
            Integer paginaAtual     = getIntSafely(root, "pagina");

            if (totalDePaginas != null && paginaAtual != null) {
                if (paginaAtual >= totalDePaginas) {
                    break;
                }
            } else {
                // sem metadados -> heurística: se veio menos que pageSize, acabou
                if (pageItems.size() < pageSize) {
                    break;
                }
            }

            page++;

        } catch (Exception e) {
            System.err.println("Falha ao processar resposta de ListarCategorias: " + e.getMessage());
            break;
        }
    }

    return out;
}

private static Integer getIntSafely(com.fasterxml.jackson.databind.JsonNode root, String field) {
    var n = root.get(field);
    if (n != null && n.isInt()) return n.asInt();
    try {
        if (n != null && n.isTextual()) return Integer.valueOf(n.asText());
    } catch (Exception ignored) {}
    return null;
}
    // util para logar JSON sem lançar exceção
    private static String toJsonSafe(Object o) {
        try { return MAPPER.writeValueAsString(o); }
        catch (Exception e) { return String.valueOf(o); }
    }
}
