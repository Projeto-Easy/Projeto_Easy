package com.squad10.chatboteasy.dev;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.squad10.chatboteasy.repository.EmpresaRepository;
import com.squad10.chatboteasy.repository.NumeroCadastradoRepository;
import com.squad10.chatboteasy.tables.Empresa;
import com.squad10.chatboteasy.tables.NumeroCadastrado;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@ConditionalOnProperty(name = "terminal.chat.enabled", havingValue = "true", matchIfMissing = false)
@Component
public class DevSeedRunner implements CommandLineRunner {

    private final EmpresaRepository empresaRepo;
    private final NumeroCadastradoRepository numeroRepo;

    public DevSeedRunner(EmpresaRepository empresaRepo, NumeroCadastradoRepository numeroRepo) {
        this.empresaRepo = empresaRepo;
        this.numeroRepo = numeroRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // >>> AJUSTE APENAS SE QUISER <<<
        String numeroTeste = "5579999999999";
        String appKey = "5614700718627";
        String appSecret = "2ae8328ce879960d99ba83e7986805a3";

        // 1) Garante empresa
        Empresa empresa = empresaRepo.findByOmieAppKey(appKey).orElseGet(() -> {
            Empresa e = new Empresa();
            e.setNome("Empresa");
            e.setOmieAppKey(appKey);
            e.setOmieAppSecret(appSecret);
            return empresaRepo.save(e);
        });

        // 2) Garante número cadastrado
        boolean existe = numeroRepo.existsByEmpresaIdAndNumero(empresa.getId(), numeroTeste);
        if (!existe) {
            NumeroCadastrado n = new NumeroCadastrado();
            n.setEmpresa(empresa);
            n.setNumero(numeroTeste);
            n.setNome("Teste Terminal");
            numeroRepo.save(n);
            System.out.println("✅ Seed OK: número cadastrado = " + numeroTeste + " (empresaId=" + empresa.getId() + ")");
        } else {
            System.out.println("ℹ️ Seed: número já existia = " + numeroTeste + " (empresaId=" + empresa.getId() + ")");
        }
    }
}


