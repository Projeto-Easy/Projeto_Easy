package com.squad10.chatboteasy.dto.omie;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OmieListResponse<T> {

    @JsonAlias({
        "cadastro",
        "movimentos",
        "categorias",
        "lista", "listaCadastro", "listaMovimentos"
    })
    @JsonProperty("cadastro")
    private List<T> cadastro;

    private Integer total_de_registros;
    private Integer pagina;
    private Integer total_de_paginas;
    private Integer registros_por_pagina;

    public List<T> getCadastro() { return cadastro; }
    public void setCadastro(List<T> cadastro) { this.cadastro = cadastro; }

    public Integer getTotal_de_registros() { return total_de_registros; }
    public void setTotal_de_registros(Integer v) { this.total_de_registros = v; }

    public Integer getPagina() { return pagina; }
    public void setPagina(Integer pagina) { this.pagina = pagina; }

    public Integer getTotal_de_paginas() { return total_de_paginas; }
    public void setTotal_de_paginas(Integer total_de_paginas) { this.total_de_paginas = total_de_paginas; }

    public Integer getRegistros_por_pagina() { return registros_por_pagina; }
    public void setRegistros_por_pagina(Integer registros_por_pagina) { this.registros_por_pagina = registros_por_pagina; }

    public boolean isLastPage(int pageSize) {
        return cadastro == null || cadastro.size() < pageSize;
    }
}
