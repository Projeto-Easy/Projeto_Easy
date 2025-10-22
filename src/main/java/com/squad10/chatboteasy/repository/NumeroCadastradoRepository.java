package com.squad10.chatboteasy.repository;

import com.squad10.chatboteasy.tables.NumeroCadastrado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NumeroCadastradoRepository extends JpaRepository<NumeroCadastrado, Long> {

    Optional<NumeroCadastrado> findByEmpresaIdAndNumero(Long empresaId, String numero);

    List<NumeroCadastrado> findAllByEmpresaId(Long empresaId);

    Optional<NumeroCadastrado> findByNumero(String numero);
}