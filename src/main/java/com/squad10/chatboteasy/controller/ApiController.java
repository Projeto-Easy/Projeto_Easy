package com.squad10.chatboteasy.controller;

import com.squad10.chatboteasy.dto.AtualizarNumeroRequest;
import com.squad10.chatboteasy.dto.CriarNumeroRequest;
import com.squad10.chatboteasy.dto.NumCadastradoGet;
import com.squad10.chatboteasy.service.NumeroCadastradoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final NumeroCadastradoService numService;

    // GET - Listar todos os números
    @GetMapping("/numeros")
    public ResponseEntity<List<NumCadastradoGet>> allNumbers() {
        return ResponseEntity.ok(numService.getAllNumbers());
    }

    // POST - Criar novo número
    @PostMapping("/numeros")
    public ResponseEntity<NumCadastradoGet> criar(@RequestBody @Valid CriarNumeroRequest request) {
        NumCadastradoGet criado = numService.criar(
                request.empresaId(),
                request.numero(),
                request.nome()
        );
        return ResponseEntity.status(201).body(criado);
    }

    // PUT - Atualizar nome e/ou status
    @PutMapping("/numeros/{id}")
    public ResponseEntity<NumCadastradoGet> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid AtualizarNumeroRequest request) {

        NumCadastradoGet atualizado = numService.atualizar(id, request.nome(), request.numero(), request.status());
        return ResponseEntity.ok(atualizado);
    }

    // DELETE - Remover número
    @DeleteMapping("/numeros/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        numService.deletar(id);
        return ResponseEntity.noContent().build(); // 204
    }
}