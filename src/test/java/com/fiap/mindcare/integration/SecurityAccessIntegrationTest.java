package com.fiap.mindcare.integration;

import com.fiap.mindcare.dto.AuthSignInDTO;
import com.fiap.mindcare.dto.TokenDTO;
import com.fiap.mindcare.enuns.TipoUsuario;
import com.fiap.mindcare.model.Empresa;
import com.fiap.mindcare.model.UsuarioSistema;
import com.fiap.mindcare.repository.EmpresaRepository;
import com.fiap.mindcare.repository.UsuarioSistemaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("security-test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:mindcare-security-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.sql.init.mode=never",
        "spring.jpa.open-in-view=true",
        "security.jwt.token.secret-key=test-secret",
        "security.jwt.token.expire-length=3600000",
        "spring.ai.azure.openai.api-key=test",
        "spring.ai.azure.openai.endpoint=http://localhost",
        "spring.ai.azure.openai.chat.options.deployment-name=test",
        "mindcheck.rabbitmq.enabled=false",
        "spring.rabbitmq.listener.simple.auto-startup=false",
        "spring.rabbitmq.listener.direct.auto-startup=false",
        "spring.rabbitmq.addresses=",
        "app.bootstrap-admin.enabled=false",
        "springdoc.api-docs.enabled=false",
        "springdoc.swagger-ui.enabled=false"
})
class SecurityAccessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioSistemaRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Empresa empresa;

    @BeforeEach
    void setUp() {
        empresa = empresaRepository.save(new Empresa(null, "12345678901234", "Acme Corp", "Plano A"));
        createUser("user@acme.com", "SenhaForte9@", TipoUsuario.USER);
        createUser("admin@acme.com", "SenhaForte9@", TipoUsuario.ADMIN);
    }

    @AfterEach
    void tearDown() {
        usuarioRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    @Test
    void me_shouldReturnUnauthorizedWhenNoToken() throws Exception {
        mockMvc.perform(get("/api/usuarios/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_shouldReturnForbiddenWhenTokenInvalid() throws Exception {
        mockMvc.perform(get("/api/usuarios/me")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarUsuarios_shouldReturnForbiddenForUserRole() throws Exception {
        String token = signInAndGetToken("user@acme.com", "SenhaForte9@");

        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarUsuarios_shouldReturnOkForAdminRole() throws Exception {
        String token = signInAndGetToken("admin@acme.com", "SenhaForte9@");

        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private void createUser(String email, String rawPassword, TipoUsuario tipo) {
        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setNome("Usuario " + tipo.name());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(rawPassword));
        usuario.setTipo(tipo);
        usuario.setEmpresa(empresa);
        usuarioRepository.save(usuario);
    }

    private String signInAndGetToken(String email, String password) throws Exception {
        AuthSignInDTO dto = new AuthSignInDTO(email, password);
        MvcResult result = mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        TokenDTO token = objectMapper.readValue(result.getResponse().getContentAsString(), TokenDTO.class);
        assertNotNull(token.getAccessToken());
        return token.getAccessToken();
    }
}
