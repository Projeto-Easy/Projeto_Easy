package com.squad10.chatboteasy.tables;

import com.squad10.chatboteasy.enums.StatusEmpresa;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "empresa")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "omie_app_key", nullable = false, unique = true, length = 255)
    private String omieAppKey;

    @Column(name = "omie_app_secret", nullable = false, length = 255)
    private String omieAppSecret;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEmpresa status = StatusEmpresa.ATIVO;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_ultima_atualizacao")
    private LocalDateTime dataUltimaAtualizacao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

}