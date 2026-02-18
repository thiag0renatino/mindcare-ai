package com.fiap.mindcare.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.fiap.mindcare.service.exception.MindCheckAiException;
import com.fiap.mindcare.service.security.UsuarioAutenticadoProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MindCheckAiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final TriagemRepository triagemRepository;
    private final EncaminhamentoRepository encaminhamentoRepository;
    private final TriagemMapper triagemMapper;
    private final EncaminhamentoMapper encaminhamentoMapper;
    private final EnumMapper enumMapper;
    private final MindCheckAiEventPublisher eventPublisher;
    private final UsuarioAutenticadoProvider usuarioAutenticadoProvider;
    private final RateLimiterService rateLimiterService;
    private final StringRedisTemplate redisTemplate;

    public MindCheckAiService(ChatClient.Builder chatClientBuilder,
                              ObjectMapper objectMapper,
                              TriagemRepository triagemRepository,
                              EncaminhamentoRepository encaminhamentoRepository,
                              TriagemMapper triagemMapper,
                              EncaminhamentoMapper encaminhamentoMapper,
                              EnumMapper enumMapper,
                              ObjectProvider<MindCheckAiEventPublisher> eventPublisherProvider,
                              UsuarioAutenticadoProvider usuarioAutenticadoProvider,
                              RateLimiterService rateLimiterService,
                              StringRedisTemplate redisTemplate) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        Você é a MindCheck AI, um assistente de triagem corporativa.
                        Utilize APENAS os valores BAIXO, MODERADO ou ALTO para o campo "risco".
                        Sempre responda em JSON com os campos:
                        {
                           "risco": "BAIXO|MODERADO|ALTO",
                           "sugestoes": ["texto"],
                           "encaminhamentos": ["especialidade ou ação a ser tomada"],
                           "justificativa": "explicação curta"
                        }
                        Para riscos MODERADO ou ALTO, liste pelo menos um encaminhamento objetivo (ex: "Psicologia", "Clínico Geral").
                        Nunca inclua texto fora do JSON.
                        """)
                .build();
        this.objectMapper = objectMapper;
        this.triagemRepository = triagemRepository;
        this.encaminhamentoRepository = encaminhamentoRepository;
        this.triagemMapper = triagemMapper;
        this.encaminhamentoMapper = encaminhamentoMapper;
        this.enumMapper = enumMapper;
        this.eventPublisher = eventPublisherProvider.getIfAvailable();
        this.usuarioAutenticadoProvider = usuarioAutenticadoProvider;
        this.rateLimiterService = rateLimiterService;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public MindCheckAiResponseDTO analisar(MindCheckAiRequestDTO request) {
        // Valida se o limite já foi excedido
        UsuarioSistema usuario = usuarioAutenticadoProvider.getUsuarioAutenticado();
        rateLimiterService.checkRateLimit(usuario.getId());

        // Verifica se já existe uma resposta cacheada
        String idempotencyKey = buildIdempotencyKey(usuario.getId(), request);
        String cached = redisTemplate.opsForValue().get(idempotencyKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, MindCheckAiResponseDTO.class);
            } catch (JsonProcessingException e) {
                redisTemplate.delete(idempotencyKey);
            }
        }

        MindCheckAiResponseDTO aiPayload = callAi(request);
        Triagem triagem = salvarTriagem(usuario, request, aiPayload);
        Encaminhamento encaminhamento = salvarEncaminhamentoSeNecessario(triagem, aiPayload);

        TriagemResponseDTO triagemResponseDTO = triagemMapper.toResponse(triagem);
        aiPayload.setTriagem(triagemResponseDTO);

        if (encaminhamento != null) {
            EncaminhamentoResponseDTO encResponse = encaminhamentoMapper.toResponse(encaminhamento);
            aiPayload.setEncaminhamento(encResponse);
        }

        publicarEvento(triagem, encaminhamento);
        cacheResponse(idempotencyKey, aiPayload);
        return aiPayload;
    }

    private String buildIdempotencyKey(Long userId, MindCheckAiRequestDTO request) {
        String raw = userId + valueOrDefault(request.getRelato()) + valueOrDefault(request.getSintomas()) + valueOrDefault(request.getHumor())
                + valueOrDefault(request.getRotina());
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return "mindcheck:idempotency:" + HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new MindCheckAiException("Erro ao gerar hash de idempotência.", e);
        }
    }

    private void cacheResponse(String key, MindCheckAiResponseDTO response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(key, json, 5, TimeUnit.MINUTES);
        } catch (JsonProcessingException ignored) {
        }
    }

    private MindCheckAiResponseDTO callAi(MindCheckAiRequestDTO request) {
        String prompt = buildPrompt(request);
        String responseContent = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        try {
            return objectMapper.readValue(responseContent, MindCheckAiResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new MindCheckAiException("Não foi possível interpretar a resposta da IA.", e);
        }
    }

    private Triagem salvarTriagem(UsuarioSistema usuario, MindCheckAiRequestDTO request, MindCheckAiResponseDTO aiPayload) {
        RiscoTriagem risco = resolveRisco(aiPayload.getRisco());

        Triagem triagem = new Triagem();
        triagem.setUsuario(usuario);
        triagem.setDataHora(LocalDateTime.now());
        triagem.setRelato(request.getRelato());
        triagem.setRisco(risco);
        triagem.setSugestao(formatSugestoes(aiPayload.getSugestoes()));

        return triagemRepository.save(triagem);
    }

    private Encaminhamento salvarEncaminhamentoSeNecessario(Triagem triagem, MindCheckAiResponseDTO aiPayload) {
        if (triagem.getRisco() == RiscoTriagem.BAIXO) {
            return null;
        }

        Encaminhamento encaminhamento = new Encaminhamento();
        encaminhamento.setTriagem(triagem);
        encaminhamento.setTipo(TipoEncaminhamento.ESPECIALIDADE);
        encaminhamento.setPrioridade(definirPrioridade(triagem.getRisco()));
        encaminhamento.setStatus(StatusEncaminhamento.PENDENTE);
        encaminhamento.setEspecialidade(primeiroItem(aiPayload.getEncaminhamentos(), "Avaliação clínica geral"));
        encaminhamento.setObservacao(definirObservacao(aiPayload));

        return encaminhamentoRepository.save(encaminhamento);
    }

    private PrioridadeEncaminhamento definirPrioridade(RiscoTriagem risco) {
        return risco == RiscoTriagem.ALTO ? PrioridadeEncaminhamento.ALTA : PrioridadeEncaminhamento.MEDIA;
    }

    private String primeiroItem(List<String> itens, String fallback) {
        if (itens == null || itens.isEmpty()) {
            return fallback;
        }
        return itens.get(0);
    }

    private String definirObservacao(MindCheckAiResponseDTO aiPayload) {
        String justificativa = aiPayload.getJustificativa();
        if (justificativa == null || justificativa.isBlank()) {
            return "Encaminhamento automático MindCheck AI.";
        }
        return justificativa;
    }

    private String formatSugestoes(List<String> sugestoes) {
        if (sugestoes == null || sugestoes.isEmpty()) {
            return "Sem sugestões adicionais.";
        }
        return String.join("\n", sugestoes);
    }

    private RiscoTriagem resolveRisco(String riscoTexto) {
        RiscoTriagem risco = enumMapper.toRiscoTriagem(riscoTexto);
        if (risco == null) {
            throw new MindCheckAiException("A IA retornou um nível de risco inválido: " + riscoTexto);
        }
        return risco;
    }

    private String buildPrompt(MindCheckAiRequestDTO request) {
        return """
                Avalie o relato do colaborador e responda exclusivamente em JSON.
                Relato principal: %s
                Sintomas: %s
                Humor: %s
                Rotina: %s
                Contexto corporativo: priorize orientações seguras, de triagem inicial.
                """.formatted(
                valueOrDefault(request.getRelato()),
                valueOrDefault(request.getSintomas()),
                valueOrDefault(request.getHumor()),
                valueOrDefault(request.getRotina())
        );
    }

    private String valueOrDefault(String value) {
        return (value == null || value.isBlank()) ? "Não informado" : value.trim();
    }

    private void publicarEvento(Triagem triagem, Encaminhamento encaminhamento) {
        if (triagem == null || eventPublisher == null) {
            return;
        }
        TriagemAvaliacaoEvent event = new TriagemAvaliacaoEvent(
                triagem.getId(),
                triagem.getUsuario() != null ? triagem.getUsuario().getId() : null,
                triagem.getRisco() != null ? triagem.getRisco().name() : null,
                encaminhamento != null,
                encaminhamento != null ? encaminhamento.getEspecialidade() : null,
                triagem.getDataHora()
        );
        eventPublisher.publicarTriagem(event);
    }
}
