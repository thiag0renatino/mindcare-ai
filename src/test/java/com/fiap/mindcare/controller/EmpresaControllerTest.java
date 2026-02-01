package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.EmpresaRequestDTO;
import com.fiap.mindcare.dto.EmpresaResponseDTO;
import com.fiap.mindcare.service.EmpresaService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpresaController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class EmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmpresaService empresaService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void criar_shouldReturnCreatedWhenValid() throws Exception {
        EmpresaRequestDTO dto = new EmpresaRequestDTO("12345678901234", "Acme", "Plano A");
        EmpresaResponseDTO response = new EmpresaResponseDTO(1L, "Plano A", "Acme", "12345678901234");

        when(empresaService.criar(any(EmpresaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/empresas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(empresaService).criar(any(EmpresaRequestDTO.class));
    }

    @Test
    void criar_shouldReturnBadRequestWhenInvalidCnpj() throws Exception {
        EmpresaRequestDTO dto = new EmpresaRequestDTO("123", "Acme", "Plano A");

        mockMvc.perform(post("/api/empresas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(empresaService);
    }

    @Test
    void listar_shouldReturnOk() throws Exception {
        when(empresaService.listar(any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/empresas"))
                .andExpect(status().isOk());

        verify(empresaService).listar(any());
    }

    @Test
    void atualizar_shouldReturnOkWhenValid() throws Exception {
        EmpresaRequestDTO dto = new EmpresaRequestDTO("12345678901234", "Acme Nova", "Plano B");
        EmpresaResponseDTO response = new EmpresaResponseDTO(1L, "Plano B", "Acme Nova", "12345678901234");

        when(empresaService.atualizar(eq(1L), any(EmpresaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/empresas/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(empresaService).atualizar(eq(1L), any(EmpresaRequestDTO.class));
    }

    @Test
    void excluir_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/empresas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(empresaService).excluir(1L);
    }
}
