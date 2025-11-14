package com.fiap.mindcare.mapper;


import com.fiap.mindcare.dto.AcompanhamentoRequestDTO;
import com.fiap.mindcare.dto.AcompanhamentoResponseDTO;
import com.fiap.mindcare.model.Acompanhamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {EncaminhamentoMapper.class, EnumMapper.class})
public interface AcompanhamentoMapper {

    AcompanhamentoResponseDTO toResponse(Acompanhamento entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "encaminhamento", ignore = true)
    Acompanhamento toEntity(AcompanhamentoRequestDTO dto);
}
