package com.fiap.mindcare.config.openApi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MindCheck AI API",
                version = "1.0.0",
                description = "Núcleo tecnológico da MindCheck AI — a ferramenta de triagem inteligente focada no bem-estar físico e mental dos trabalhadores. "
                        + "Esta API organiza o fluxo de check-ins periódicos (voz ou texto), interpreta relatos com IA para classificar riscos (baixo/moderado/alto), "
                        + "gera sugestões imediatas, recomenda exames ou especialidades conveniadas e registra o acompanhamento pós-atendimento. "
                        + "O objetivo é guiar o colaborador e promover autocuidado, sem substituir o diagnóstico médico.",
                contact = @Contact(
                        name = "MindCheck AI"
                )
        ),
        tags = {
                @Tag(name = "Empresas", description = "Cadastro e manutenção de empresas clientes"),
                @Tag(name = "Usuários", description = "Gestão dos usuários da plataforma corporativa"),
                @Tag(name = "Triagens", description = "Check-ins inteligentes e avaliação inicial dos colaboradores"),
                @Tag(name = "Encaminhamentos", description = "Encaminhamentos automáticos gerados a partir das triagens"),
                @Tag(name = "Acompanhamentos", description = "Eventos e desfechos pós-encaminhamento"),
                @Tag(name = "Profissionais", description = "Profissionais parceiros disponíveis para atendimento")
        }
)
public class OpenApiConfig {
}
