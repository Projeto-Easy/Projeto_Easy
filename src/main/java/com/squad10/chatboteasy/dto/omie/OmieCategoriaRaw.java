package com.squad10.chatboteasy.dto.omie;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmieCategoriaRaw {

    @JsonProperty("cCodCateg")
    private String cCodCateg;

    @JsonProperty("descricao")
    @JsonAlias({"cDescr"})
    private String descricao;
}
