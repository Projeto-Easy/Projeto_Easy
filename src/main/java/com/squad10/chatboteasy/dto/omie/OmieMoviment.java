package com.squad10.chatboteasy.dto.omie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmieMoviment {

    @JsonProperty("detalhes")
    private Detalhes detalhes;

    @JsonProperty("resumo")
    private Resumo resumo;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true) // <- redundante
    public static class Detalhes {
        @JsonProperty("cGrupo")
        private String cGrupo;

        @JsonProperty("cStatus")
        private String cStatus;

        @JsonProperty("dDtPagamento")
        private String dDtPagamento;

        @JsonProperty("cCodCateg")
        private String cCodCateg;

        @JsonProperty("dDtEmissao")
        private String dDtEmissao;
        
        @JsonProperty("dDtPrevisao")
        private String dDtPrevisao;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Resumo {
        @JsonProperty("nValPago")
        private BigDecimal nValPago;
    }
}
