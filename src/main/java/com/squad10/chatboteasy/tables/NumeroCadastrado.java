package com.squad10.chatboteasy.tables;

import com.squad10.chatboteasy.enums.StatusNumero;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "numero_cadastrado", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"empresa_id", "numero"}, name = "uk_empresa_numero")
})
@Data
public class NumeroCadastrado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(nullable = true, length = 100)
    private String nome;

    @Column(name = "data_primeiro_contato", nullable = false, updatable = false)
    private LocalDateTime dataPrimeiroContato;

    @Column(name = "ultimo_contato", nullable = false)
    private LocalDateTime ultimoContato;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusNumero status = StatusNumero.ATIVO;

    @Column(name = "etapa_fluxo", length = 50)
    private String etapaFluxo;

    @PrePersist
    protected void onCreate() {
        this.dataPrimeiroContato = LocalDateTime.now();
        this.ultimoContato = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.ultimoContato = LocalDateTime.now();
    }
}
