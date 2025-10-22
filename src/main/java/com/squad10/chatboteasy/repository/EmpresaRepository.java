package com.squad10.chatboteasy.repository;

import com.squad10.chatboteasy.tables.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByOmieAppKey(String omieAppKey);
}