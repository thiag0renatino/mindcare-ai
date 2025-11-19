package com.fiap.mindcare.messaging.listener;

import com.fiap.mindcare.messaging.event.TriagemAvaliacaoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MindCheckAiEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MindCheckAiEventListener.class);

    @RabbitListener(queues = "#{@triagemQueue}")
    public void processarEvento(TriagemAvaliacaoEvent event) {
        LOGGER.info("Processando triagem {} (risco {}), encaminhamento automatico: {}",
                event.getTriagemId(),
                event.getRisco(),
                event.isEncaminhamentoCriado());

        if (event.isEncaminhamentoCriado()) {
            LOGGER.info("Especialidade sugerida: {}", event.getEspecialidadeSugerida());
        }
    }
}
