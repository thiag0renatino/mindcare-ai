package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.ProfissionalRequestDTO;
import com.fiap.mindcare.dto.ProfissionalResponseDTO;
import com.fiap.mindcare.service.ProfissionalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.fiap.mindcare.security.jwt.JwtTokenProvider;
import com.fiap.mindcare.security.jwt.TokenBlacklistService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfissionalController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProfissionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProfissionalService profissionalService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    void criar_shouldReturnCreatedWhenValid() throws Exception {
        ProfissionalRequestDTO dto = new ProfissionalRequestDTO("Dra. Ana", "Plano A", "Psicologia", "9999-9999");
        ProfissionalResponseDTO response = new ProfissionalResponseDTO();
        response.setId(1L);

        when(profissionalService.criar(any(ProfissionalRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/profissionais")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(profissionalService).criar(any(ProfissionalRequestDTO.class));
    }

    @Test
    void criar_shouldReturnBadRequestWhenMissingNome() throws Exception {
        ProfissionalRequestDTO dto = new ProfissionalRequestDTO("", "Plano A", "Psicologia", "9999-9999");

        mockMvc.perform(post("/api/profissionais")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(profissionalService);
    }

    @Test
    void listar_shouldReturnOk() throws Exception {
        when(profissionalService.listar(any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/profissionais"))
                .andExpect(status().isOk());

        verify(profissionalService).listar(any());
    }

    @Test
    void buscarPorEspecialidade_shouldReturnOk() throws Exception {
        when(profissionalService.buscarPorEspecialidade(eq("Psicologia"), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/profissionais/especialidade")
                        .param("especialidade", "Psicologia"))
                .andExpect(status().isOk());

        verify(profissionalService).buscarPorEspecialidade(eq("Psicologia"), any());
    }

    @Test
    void atualizar_shouldReturnOkWhenValid() throws Exception {
        ProfissionalRequestDTO dto = new ProfissionalRequestDTO("Dra. Ana", "Plano A", "Psicologia", "9999-9999");
        ProfissionalResponseDTO response = new ProfissionalResponseDTO();
        response.setId(1L);

        when(profissionalService.atualizar(eq(1L), any(ProfissionalRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/profissionais/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(profissionalService).atualizar(eq(1L), any(ProfissionalRequestDTO.class));
    }
}
