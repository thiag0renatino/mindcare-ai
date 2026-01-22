package com.fiap.mindcare;

import com.fiap.mindcare.enuns.TipoUsuario;
import com.fiap.mindcare.model.Empresa;
import com.fiap.mindcare.model.UsuarioSistema;
import com.fiap.mindcare.repository.EmpresaRepository;
import com.fiap.mindcare.repository.UsuarioSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MindcareApplication implements CommandLineRunner {

    @Autowired
    private UsuarioSistemaRepository usuarioSistemaRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(MindcareApplication.class, args);
	}

    @Override
    public void run(String... args) {
        final String emailAdmin = "admin@mindcareai.com";
        final String senhaAdmin = "admin123mindcareai";

        if (!usuarioSistemaRepository.existsByEmail(emailAdmin)) {

            Empresa empresa = empresaRepository.findById(1L).orElseGet(() ->
                    empresaRepository.findAll()
                            .stream()
                            .findFirst()
                            .orElseGet(() -> {
                                Empresa emp = new Empresa();
                                emp.setCnpj("00000000000000"); // 14 chars sem máscara
                                emp.setNome("MindCare AI - Empresa Padrão");
                                emp.setPlanoSaude("Padrão");
                                return empresaRepository.save(emp);
                            })
            );

            UsuarioSistema admin = new UsuarioSistema();
            admin.setNome("Administrador MindCare AI");
            admin.setEmail(emailAdmin);
            admin.setSenha(passwordEncoder.encode(senhaAdmin));
            admin.setTipo(TipoUsuario.ADMIN);
            admin.setEmpresa(empresa);

            usuarioSistemaRepository.save(admin);

            System.out.println("\n=== Usuário ADMIN criado com sucesso ===");
            System.out.println("Email: " + emailAdmin);
            System.out.println("Senha: " + senhaAdmin);
            System.out.println("Empresa vinculada: " + empresa.getNome() + " (ID " + empresa.getId() + ")");
            System.out.println("=======================================\n");

        } else {
            System.out.println("\nUsuário ADMIN já existente\n");
            System.out.println("Email: " + emailAdmin);
            System.out.println("Senha: " + senhaAdmin);
        }
    }

}
