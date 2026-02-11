package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.UsuarioRequestDTO;
import com.fiap.mindcare.dto.UsuarioResponseDTO;
import com.fiap.mindcare.service.AuthService;
import com.fiap.mindcare.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fiap.mindcare.security.jwt.JwtTokenProvider;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void listar_shouldReturnOk() throws Exception {
        when(usuarioService.listar(any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk());

        verify(usuarioService).listar(any());
    }

    @Test
    void listarPorEmpresa_shouldReturnOk() throws Exception {
        when(usuarioService.listarPorEmpresa(eq(10L), any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/usuarios/empresa/{empresaId}", 10L))
                .andExpect(status().isOk());

        verify(usuarioService).listarPorEmpresa(eq(10L), any());
    }

    @Test
    void me_shouldReturnOk() throws Exception {
        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setEmail("ana@acme.com");
        when(usuarioService.me(any(Principal.class))).thenReturn(response);

        mockMvc.perform(get("/api/usuarios/me").principal((Principal) () -> "ana@acme.com"))
                .andExpect(status().isOk());

        verify(usuarioService).me(any(Principal.class));
    }

    @Test
    void atualizar_shouldReturnBadRequestWhenInvalidEmail() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Ana", "invalid", "SenhaForte9", "USER", 10L);

        mockMvc.perform(put("/api/usuarios/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(usuarioService);
    }

    @Test
    void excluir_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/usuarios/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(usuarioService).excluir(1L);
    }
}
