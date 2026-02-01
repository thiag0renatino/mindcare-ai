package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.TriagemRequestDTO;
import com.fiap.mindcare.dto.TriagemResponseDTO;
import com.fiap.mindcare.service.TriagemService;
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

@WebMvcTest(TriagemController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TriagemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TriagemService triagemService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void criar_shouldReturnCreatedWhenValid() throws Exception {
        TriagemRequestDTO dto = new TriagemRequestDTO(10L, LocalDateTime.now(), "Relato valido", "ALTO", "Sugestao");
        TriagemResponseDTO response = new TriagemResponseDTO();
        response.setId(1L);

        when(triagemService.criar(any(TriagemRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/triagens")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(triagemService).criar(any(TriagemRequestDTO.class));
    }

    @Test
    void criar_shouldReturnBadRequestWhenRelatoTooShort() throws Exception {
        TriagemRequestDTO dto = new TriagemRequestDTO(10L, LocalDateTime.now(), "1234", "ALTO", "Sugestao");

        mockMvc.perform(post("/api/triagens")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(triagemService);
    }

    @Test
    void listar_shouldReturnOk() throws Exception {
        when(triagemService.listar(any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/triagens"))
                .andExpect(status().isOk());

        verify(triagemService).listar(any());
    }

    @Test
    void atualizar_shouldReturnOkWhenValid() throws Exception {
        TriagemRequestDTO dto = new TriagemRequestDTO(10L, LocalDateTime.now(), "Relato valido", "MODERADO", "Sugestao");
        TriagemResponseDTO response = new TriagemResponseDTO();
        response.setId(1L);

        when(triagemService.atualizar(eq(1L), any(TriagemRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/triagens/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(triagemService).atualizar(eq(1L), any(TriagemRequestDTO.class));
    }
}
