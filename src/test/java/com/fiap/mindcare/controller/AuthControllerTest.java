package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.AuthRequestDTO;
import com.fiap.mindcare.dto.AuthSignInDTO;
import com.fiap.mindcare.dto.TokenDTO;
import com.fiap.mindcare.repository.EmpresaRepository;
import com.fiap.mindcare.repository.UsuarioSistemaRepository;
import com.fiap.mindcare.security.jwt.JwtTokenProvider;
import com.fiap.mindcare.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UsuarioSistemaRepository usuarioSistemaRepository;

    @MockitoBean
    private EmpresaRepository empresaRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void signIn_shouldReturnForbiddenWhenInvalid() throws Exception {
        AuthSignInDTO dto = new AuthSignInDTO("", "");

        mockMvc.perform(post("/auth/signin")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Requisição inválida"));

        verifyNoInteractions(authService);
    }

    @Test
    void signIn_shouldReturnTokenWhenValid() throws Exception {
        AuthSignInDTO dto = new AuthSignInDTO("ana@acme.com", "SenhaForte9");
        TokenDTO token = new TokenDTO("ana@acme.com", "refresh", "access", new Date(), new Date(), true);

        when(authService.signIn(any(AuthSignInDTO.class))).thenReturn(ResponseEntity.ok(token));

        mockMvc.perform(post("/auth/signin")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));

        verify(authService).signIn(any(AuthSignInDTO.class));
    }

    @Test
    void refreshToken_shouldReturnForbiddenWhenInvalid() throws Exception {
        mockMvc.perform(put("/auth/refresh/{email}", "ana@acme.com")
                        .header("Authorization", ""))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Requisição inválida"));

        verifyNoInteractions(authService);
    }

    @Test
    void refreshToken_shouldReturnTokenWhenValid() throws Exception {
        TokenDTO token = new TokenDTO("ana@acme.com", "refresh", "access", new Date(), new Date(), true);

        when(authService.refreshToken(any(String.class), any(String.class))).thenReturn(ResponseEntity.ok(token));

        mockMvc.perform(put("/auth/refresh/{email}", "ana@acme.com")
                        .header("Authorization", "refresh-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));

        verify(authService).refreshToken("ana@acme.com", "refresh-token");
    }

    @Test
    void register_shouldReturnCreatedWhenServiceCreatesUser() throws Exception {
        AuthRequestDTO dto = new AuthRequestDTO("Ana", "Acme", "ana@acme.com", "SenhaForte9");

        when(authService.register(any(AuthRequestDTO.class)))
                .thenReturn(new ResponseEntity<>("Usuário registrado com sucesso!", HttpStatus.CREATED));

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(authService).register(any(AuthRequestDTO.class));
    }
}
