package com.fiap.mindcare.integration;

import com.fiap.mindcare.dto.AuthRequestDTO;
import com.fiap.mindcare.dto.AuthSignInDTO;
import com.fiap.mindcare.dto.TokenDTO;
import com.fiap.mindcare.dto.UsuarioRequestDTO;
import com.fiap.mindcare.model.Empresa;
import com.fiap.mindcare.model.UsuarioSistema;
import com.fiap.mindcare.repository.EmpresaRepository;
import com.fiap.mindcare.repository.UsuarioSistemaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthUsuarioIntegrationTest extends AbstractMySqlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioSistemaRepository usuarioRepository;

    @AfterEach
    void cleanup() {
        usuarioRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    @Test
    void registerSignInAndFetchUserFlow() throws Exception {
        Empresa empresa = empresaRepository.save(new Empresa(null, "12345678901234", "Acme Corp", null));

        AuthRequestDTO register = new AuthRequestDTO("Ana", "Acme", "ana@acme.com", "SenhaForte9");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        AuthSignInDTO signIn = new AuthSignInDTO("ana@acme.com", "SenhaForte9");
        MvcResult signinResult = mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signIn)))
                .andExpect(status().isOk())
                .andReturn();

        TokenDTO token = objectMapper.readValue(signinResult.getResponse().getContentAsString(), TokenDTO.class);
        assertNotNull(token.getAccessToken());

        UsuarioSistema usuario = usuarioRepository.findByEmail("ana@acme.com").orElseThrow();

        mockMvc.perform(get("/api/usuarios/{id}", usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@acme.com"));

        Principal principal = () -> "ana@acme.com";
        mockMvc.perform(get("/api/usuarios/me").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@acme.com"));

        UsuarioRequestDTO update = new UsuarioRequestDTO(
                "Ana Maria",
                "ana@acme.com",
                "NovaSenha9",
                "USER",
                empresa.getId()
        );
        mockMvc.perform(put("/api/usuarios/{id}", usuario.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Maria"));
    }
}
