package com.fiap.mindcare.mapper;

import com.fiap.mindcare.dto.ProfissionalRequestDTO;
import com.fiap.mindcare.dto.ProfissionalResponseDTO;
import com.fiap.mindcare.model.Profissional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProfissionalMapper {

    ProfissionalResponseDTO toResponse(Profissional entity);

    @Mapping(target = "id", ignore = true)
    Profissional toEntity(ProfissionalRequestDTO dto);
}
