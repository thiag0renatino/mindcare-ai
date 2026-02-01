package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.MindCheckAiRequestDTO;
import com.fiap.mindcare.dto.MindCheckAiResponseDTO;
import com.fiap.mindcare.service.MindCheckAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.fiap.mindcare.security.jwt.JwtTokenProvider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MindCheckAiController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class MindCheckAiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MindCheckAiService mindCheckAiService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void analisar_shouldReturnOkWhenValid() throws Exception {
        MindCheckAiRequestDTO dto = new MindCheckAiRequestDTO();
        dto.setUsuarioId(1L);
        dto.setRelato("Relato com tamanho suficiente");
        dto.setSintomas("cansaco");
        dto.setHumor("baixo");
        dto.setRotina("rotina intensa");

        when(mindCheckAiService.analisar(any(MindCheckAiRequestDTO.class))).thenReturn(new MindCheckAiResponseDTO());

        mockMvc.perform(post("/api/mindcheck-ai/analises")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(mindCheckAiService).analisar(any(MindCheckAiRequestDTO.class));
    }

    @Test
    void analisar_shouldReturnBadRequestWhenRelatoTooShort() throws Exception {
        MindCheckAiRequestDTO dto = new MindCheckAiRequestDTO();
        dto.setUsuarioId(1L);
        dto.setRelato("curto");

        mockMvc.perform(post("/api/mindcheck-ai/analises")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(mindCheckAiService);
    }
}
