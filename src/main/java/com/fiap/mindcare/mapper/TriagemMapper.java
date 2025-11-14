package com.fiap.mindcare.mapper;

import com.fiap.mindcare.dto.TriagemRequestDTO;
import com.fiap.mindcare.dto.TriagemResponseDTO;
import com.fiap.mindcare.model.Triagem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class, EnumMapper.class})
public interface TriagemMapper {

    TriagemResponseDTO toResponse(Triagem entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Triagem toEntity(TriagemRequestDTO dto);
}
