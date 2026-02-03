package com.fiap.mindcare.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.mindcare.dto.EncaminhamentoResponseDTO;
import com.fiap.mindcare.dto.MindCheckAiRequestDTO;
import com.fiap.mindcare.dto.MindCheckAiResponseDTO;
import com.fiap.mindcare.dto.TriagemResponseDTO;
import com.fiap.mindcare.enuns.PrioridadeEncaminhamento;
import com.fiap.mindcare.enuns.RiscoTriagem;
import com.fiap.mindcare.enuns.StatusEncaminhamento;
import com.fiap.mindcare.enuns.TipoEncaminhamento;
import com.fiap.mindcare.mapper.EncaminhamentoMapper;
import com.fiap.mindcare.mapper.EnumMapper;
import com.fiap.mindcare.mapper.TriagemMapper;
import com.fiap.mindcare.messaging.event.TriagemAvaliacaoEvent;
import com.fiap.mindcare.messaging.publisher.MindCheckAiEventPublisher;
import com.fiap.mindcare.model.Encaminhamento;
import com.fiap.mindcare.model.Triagem;
import com.fiap.mindcare.model.UsuarioSistema;
import com.fiap.mindcare.repository.EncaminhamentoRepository;
import com.fiap.mindcare.repository.TriagemRepository;
import com.fiap.mindcare.service.exception.AccessDeniedException;
import com.fiap.mindcare.service.exception.MindCheckAiException;
import com.fiap.mindcare.service.security.UsuarioAutenticadoProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Answers;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MindCheckAiServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @Mock
    private TriagemRepository triagemRepository;

    @Mock
    private EncaminhamentoRepository encaminhamentoRepository;

    @Mock
    private TriagemMapper triagemMapper;

    @Mock
    private EncaminhamentoMapper encaminhamentoMapper;

    @Mock
    private EnumMapper enumMapper;

    @Mock
    private ObjectProvider<MindCheckAiEventPublisher> eventPublisherProvider;

    @Mock
    private MindCheckAiEventPublisher eventPublisher;

    @Mock
    private UsuarioAutenticadoProvider usuarioAutenticadoProvider;

    private MindCheckAiService service;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        when(chatClientBuilder.defaultSystem(anyString())).thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(eventPublisherProvider.getIfAvailable()).thenReturn(eventPublisher);

        service = new MindCheckAiService(
                chatClientBuilder,
                objectMapper,
                triagemRepository,
                encaminhamentoRepository,
                triagemMapper,
                encaminhamentoMapper,
                enumMapper,
                eventPublisherProvider,
                usuarioAutenticadoProvider
        );
    }

    @Test
    void analisar_shouldThrowWhenUserNotAuthenticated() {
        MindCheckAiRequestDTO request = buildRequest();
        when(usuarioAutenticadoProvider.getUsuarioAutenticado())
                .thenThrow(new AccessDeniedException("Usuário não autenticado"));

        assertThrows(AccessDeniedException.class, () -> service.analisar(request));

        verifyNoInteractions(triagemRepository);
        verifyNoInteractions(encaminhamentoRepository);
    }

    @Test
    void analisar_shouldPersistTriagemAndReturnWithoutEncaminhamentoOnBaixo() {
        MindCheckAiRequestDTO request = buildRequest();
        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setId(1L);

        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);
        when(chatClient.prompt().user(anyString()).call().content())
                .thenReturn(jsonResponse("BAIXO", List.of("Beba agua"), List.of(), "Tudo ok"));
        when(enumMapper.toRiscoTriagem("BAIXO")).thenReturn(RiscoTriagem.BAIXO);
        when(triagemRepository.save(any(Triagem.class))).thenAnswer(invocation -> {
            Triagem triagem = invocation.getArgument(0);
            triagem.setId(10L);
            return triagem;
        });
        when(triagemMapper.toResponse(any(Triagem.class))).thenReturn(new TriagemResponseDTO());

        MindCheckAiResponseDTO result = service.analisar(request);

        assertNotNull(result.getTriagem());
        assertNull(result.getEncaminhamento());
        verify(encaminhamentoRepository, never()).save(any(Encaminhamento.class));

        ArgumentCaptor<Triagem> captor = ArgumentCaptor.forClass(Triagem.class);
        verify(triagemRepository).save(captor.capture());
        assertEquals(RiscoTriagem.BAIXO, captor.getValue().getRisco());
        assertEquals("Beba agua", captor.getValue().getSugestao());
        verify(eventPublisher).publicarTriagem(any(TriagemAvaliacaoEvent.class));
    }

    @Test
    void analisar_shouldCreateEncaminhamentoOnModerado() {
        MindCheckAiRequestDTO request = buildRequest();
        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setId(1L);

        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);
        when(chatClient.prompt().user(anyString()).call().content())
                .thenReturn(jsonResponse("MODERADO", List.of("Respiração"), List.of("Psicologia"), "Busque apoio"));
        when(enumMapper.toRiscoTriagem("MODERADO")).thenReturn(RiscoTriagem.MODERADO);
        when(triagemRepository.save(any(Triagem.class))).thenAnswer(invocation -> {
            Triagem triagem = invocation.getArgument(0);
            triagem.setId(10L);
            return triagem;
        });
        when(encaminhamentoRepository.save(any(Encaminhamento.class))).thenAnswer(invocation -> {
            Encaminhamento encaminhamento = invocation.getArgument(0);
            encaminhamento.setId(20L);
            return encaminhamento;
        });
        when(triagemMapper.toResponse(any(Triagem.class))).thenReturn(new TriagemResponseDTO());
        when(encaminhamentoMapper.toResponse(any(Encaminhamento.class))).thenReturn(new EncaminhamentoResponseDTO());

        MindCheckAiResponseDTO result = service.analisar(request);

        assertNotNull(result.getTriagem());
        assertNotNull(result.getEncaminhamento());

        ArgumentCaptor<Encaminhamento> captor = ArgumentCaptor.forClass(Encaminhamento.class);
        verify(encaminhamentoRepository).save(captor.capture());
        Encaminhamento saved = captor.getValue();
        assertEquals(TipoEncaminhamento.ESPECIALIDADE, saved.getTipo());
        assertEquals(PrioridadeEncaminhamento.MEDIA, saved.getPrioridade());
        assertEquals(StatusEncaminhamento.PENDENTE, saved.getStatus());
        assertEquals("Psicologia", saved.getEspecialidade());
        assertEquals("Busque apoio", saved.getObservacao());
        verify(eventPublisher).publicarTriagem(any(TriagemAvaliacaoEvent.class));
    }

    @Test
    void analisar_shouldUseFallbacksWhenEncaminhamentosEmptyAndJustificativaBlank() {
        MindCheckAiRequestDTO request = buildRequest();
        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setId(1L);

        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);
        when(chatClient.prompt().user(anyString()).call().content())
                .thenReturn(jsonResponse("MODERADO", List.of(), List.of(), " "));
        when(enumMapper.toRiscoTriagem("MODERADO")).thenReturn(RiscoTriagem.MODERADO);
        when(triagemRepository.save(any(Triagem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(encaminhamentoRepository.save(any(Encaminhamento.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(triagemMapper.toResponse(any(Triagem.class))).thenReturn(new TriagemResponseDTO());
        when(encaminhamentoMapper.toResponse(any(Encaminhamento.class))).thenReturn(new EncaminhamentoResponseDTO());

        service.analisar(request);

        ArgumentCaptor<Encaminhamento> captor = ArgumentCaptor.forClass(Encaminhamento.class);
        verify(encaminhamentoRepository).save(captor.capture());
        Encaminhamento saved = captor.getValue();
        assertEquals("Avaliação clínica geral", saved.getEspecialidade());
        assertEquals("Encaminhamento automático MindCheck AI.", saved.getObservacao());
    }

    @Test
    void analisar_shouldThrowWhenAiResponseInvalidJson() {
        MindCheckAiRequestDTO request = buildRequest();
        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setId(1L);

        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);
        when(chatClient.prompt().user(anyString()).call().content()).thenReturn("not-json");

        assertThrows(MindCheckAiException.class, () -> service.analisar(request));

        verify(triagemRepository, never()).save(any(Triagem.class));
    }

    @Test
    void analisar_shouldThrowWhenAiRiscoInvalido() {
        MindCheckAiRequestDTO request = buildRequest();
        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setId(1L);

        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);
        when(chatClient.prompt().user(anyString()).call().content())
                .thenReturn(jsonResponse("X", List.of("a"), List.of("b"), "c"));
        when(enumMapper.toRiscoTriagem("X")).thenReturn(null);

        assertThrows(MindCheckAiException.class, () -> service.analisar(request));

        verify(triagemRepository, never()).save(any(Triagem.class));
    }

    private MindCheckAiRequestDTO buildRequest() {
        MindCheckAiRequestDTO request = new MindCheckAiRequestDTO();
        request.setRelato("Relato de teste com mais de dez caracteres");
        request.setSintomas("cansaco");
        request.setHumor("baixo");
        request.setRotina("rotina intensa");
        return request;
    }

    private String jsonResponse(String risco, List<String> sugestoes, List<String> encaminhamentos, String justificativa) {
        return String.format(
                "{" +
                        "\"risco\":\"%s\"," +
                        "\"sugestoes\":%s," +
                        "\"encaminhamentos\":%s," +
                        "\"justificativa\":\"%s\"" +
                        "}",
                risco,
                toJsonArray(sugestoes),
                toJsonArray(encaminhamentos),
                justificativa.replace("\"", "\\\"")
        );
    }

    private String toJsonArray(List<String> values) {
        if (values == null) {
            return "null";
        }
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append('"').append(values.get(i).replace("\"", "\\\"")).append('"');
        }
        builder.append(']');
        return builder.toString();
    }
}
