package com.squad10.chatboteasy.service;

import com.squad10.chatboteasy.dto.NumCadastradoGet;
import com.squad10.chatboteasy.enums.StatusNumero;
import com.squad10.chatboteasy.tables.NumeroCadastrado;
import com.squad10.chatboteasy.repository.EmpresaRepository;
import com.squad10.chatboteasy.repository.NumeroCadastradoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NumeroCadastradoService {

    private final NumeroCadastradoRepository numRepo;
    private final EmpresaRepository empresaRepo;

    public NumeroCadastradoService(NumeroCadastradoRepository numRepo,
                                   EmpresaRepository empresaRepo) {
        this.numRepo = numRepo;
        this.empresaRepo = empresaRepo;
    }

    public List<NumCadastradoGet> getAllNumbers() {
        return numRepo.findAll().stream()
                .map(NumCadastradoGet::from)
                .toList();
    }

    // POST – Criar
    @Transactional
    public NumCadastradoGet criar(Long empresaId, String numero, String nome) {
        var empresa = empresaRepo.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        if (numRepo.existsByEmpresaIdAndNumero(empresaId, numero)) {
            throw new IllegalArgumentException("Número já cadastrado para esta empresa");
        }

        NumeroCadastrado entity = new NumeroCadastrado();
        entity.setEmpresa(empresa);
        entity.setNumero(numero);
        entity.setNome(nome);
        entity.setStatus(StatusNumero.ATIVO); // padrão

        entity = numRepo.save(entity);
        return NumCadastradoGet.from(entity);
    }

    // PUT – Atualizar
    @Transactional
    public NumCadastradoGet atualizar(Long id, String nome, String numero, StatusNumero status) {
        NumeroCadastrado entity = numRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Número não encontrado"));

        if (nome != null) entity.setNome(nome);
        if (numero != null) entity.setNumero(numero);
        if (status != null) entity.setStatus(status);

        entity = numRepo.save(entity);
        return NumCadastradoGet.from(entity);
    }

    // DELETE
    @Transactional
    public void deletar(Long id) {
        if (!numRepo.existsById(id)) {
            throw new EntityNotFoundException("Número não encontrado");
        }
        numRepo.deleteById(id);
    }
}