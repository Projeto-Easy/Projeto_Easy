package com.squad10.chatboteasy.dto;
import com.squad10.chatboteasy.enums.StatusNumero;
import com.squad10.chatboteasy.tables.NumeroCadastrado;


public record NumCadastradoGet(long id, String nome, String numero, StatusNumero status) {

    public static NumCadastradoGet from(NumeroCadastrado n){
        return new NumCadastradoGet(
                n.getId(),
                n.getNome(),
                n.getNumero(),
                n.getStatus()
        );
    }
}
