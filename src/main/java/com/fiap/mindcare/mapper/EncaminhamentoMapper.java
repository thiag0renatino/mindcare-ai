package com.fiap.mindcare.mapper;


import com.fiap.mindcare.dto.EncaminhamentoRequestDTO;
import com.fiap.mindcare.dto.EncaminhamentoResponseDTO;
import com.fiap.mindcare.model.Encaminhamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {TriagemMapper.class, ProfissionalMapper.class, EnumMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EncaminhamentoMapper {

    EncaminhamentoResponseDTO toResponse(Encaminhamento entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "triagem", ignore = true)
    @Mapping(target = "profissional", ignore = true)
    Encaminhamento toEntity(EncaminhamentoRequestDTO dto);
}
