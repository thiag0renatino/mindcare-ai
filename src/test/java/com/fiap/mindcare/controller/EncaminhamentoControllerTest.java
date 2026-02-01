package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.EncaminhamentoRecomendadoDTO;
import com.fiap.mindcare.dto.EncaminhamentoRequestDTO;
import com.fiap.mindcare.dto.EncaminhamentoResponseDTO;
import com.fiap.mindcare.service.EncaminhamentoService;
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

@WebMvcTest(EncaminhamentoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class EncaminhamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EncaminhamentoService encaminhamentoService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void criar_shouldReturnCreatedWhenValid() throws Exception {
        EncaminhamentoRequestDTO dto = new EncaminhamentoRequestDTO(
                "ESPECIALIDADE", 10L, null, "obs", "Psicologia", "MEDIA", "PENDENTE", null
        );
        EncaminhamentoResponseDTO response = new EncaminhamentoResponseDTO();
        response.setId(1L);

        when(encaminhamentoService.criar(any(EncaminhamentoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/encaminhamentos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(encaminhamentoService).criar(any(EncaminhamentoRequestDTO.class));
    }

    @Test
    void criar_shouldReturnBadRequestWhenMissingTipo() throws Exception {
        EncaminhamentoRequestDTO dto = new EncaminhamentoRequestDTO(
                null, 10L, null, "obs", "Psicologia", "MEDIA", "PENDENTE", null
        );

        mockMvc.perform(post("/api/encaminhamentos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(encaminhamentoService);
    }

    @Test
    void listarRecomendados_shouldReturnOk() throws Exception {
        when(encaminhamentoService.listarRecomendados(eq(1L), eq("Psi"), any()))
                .thenReturn(new PageImpl<EncaminhamentoRecomendadoDTO>(List.of()));

        mockMvc.perform(get("/api/encaminhamentos/recomendados")
                        .param("empresaId", "1")
                        .param("especialidade", "Psi"))
                .andExpect(status().isOk());

        verify(encaminhamentoService).listarRecomendados(eq(1L), eq("Psi"), any());
    }

    @Test
    void atualizar_shouldReturnOkWhenValid() throws Exception {
        EncaminhamentoRequestDTO dto = new EncaminhamentoRequestDTO(
                "ESPECIALIDADE", 10L, null, "obs", "Psicologia", "MEDIA", "PENDENTE", null
        );
        EncaminhamentoResponseDTO response = new EncaminhamentoResponseDTO();
        response.setId(1L);

        when(encaminhamentoService.atualizar(eq(1L), any(EncaminhamentoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/encaminhamentos/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(encaminhamentoService).atualizar(eq(1L), any(EncaminhamentoRequestDTO.class));
    }
}
