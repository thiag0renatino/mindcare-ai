package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.AcompanhamentoRequestDTO;
import com.fiap.mindcare.dto.AcompanhamentoResponseDTO;
import com.fiap.mindcare.service.AcompanhamentoService;
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

import java.time.LocalDateTime;
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

@WebMvcTest(AcompanhamentoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AcompanhamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AcompanhamentoService acompanhamentoService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    void criar_shouldReturnCreatedWhenValid() throws Exception {
        AcompanhamentoRequestDTO dto = new AcompanhamentoRequestDTO(
                10L, "url", "desc", "AGENDAMENTO", LocalDateTime.now()
        );
        AcompanhamentoResponseDTO response = new AcompanhamentoResponseDTO();
        response.setId(1L);

        when(acompanhamentoService.criar(any(AcompanhamentoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/acompanhamentos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(acompanhamentoService).criar(any(AcompanhamentoRequestDTO.class));
    }

    @Test
    void criar_shouldReturnBadRequestWhenMissingDataEvento() throws Exception {
        AcompanhamentoRequestDTO dto = new AcompanhamentoRequestDTO(
                10L, "url", "desc", "AGENDAMENTO", null
        );

        mockMvc.perform(post("/api/acompanhamentos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(acompanhamentoService);
    }

    @Test
    void listar_shouldReturnOk() throws Exception {
        when(acompanhamentoService.listar(any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/acompanhamentos"))
                .andExpect(status().isOk());

        verify(acompanhamentoService).listar(any());
    }

    @Test
    void atualizar_shouldReturnOkWhenValid() throws Exception {
        AcompanhamentoRequestDTO dto = new AcompanhamentoRequestDTO(
                10L, "url", "desc", "AGENDAMENTO", LocalDateTime.now()
        );
        AcompanhamentoResponseDTO response = new AcompanhamentoResponseDTO();
        response.setId(1L);

        when(acompanhamentoService.atualizar(eq(1L), any(AcompanhamentoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/acompanhamentos/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(acompanhamentoService).atualizar(eq(1L), any(AcompanhamentoRequestDTO.class));
    }
}
