package com.fiap.mindcare.mapper;

import com.fiap.mindcare.dto.UsuarioRequestDTO;
import com.fiap.mindcare.dto.UsuarioResponseDTO;
import com.fiap.mindcare.model.UsuarioSistema;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {EmpresaMapper.class, EnumMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UsuarioMapper {

    UsuarioResponseDTO toResponse(UsuarioSistema entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    UsuarioSistema toEntity(UsuarioRequestDTO dto);
}
