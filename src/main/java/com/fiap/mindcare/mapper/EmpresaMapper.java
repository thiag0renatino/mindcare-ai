package com.fiap.mindcare.mapper;

import com.fiap.mindcare.dto.EmpresaRequestDTO;
import com.fiap.mindcare.dto.EmpresaResponseDTO;
import com.fiap.mindcare.model.Empresa;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    EmpresaResponseDTO toResponse(Empresa entity);

    Empresa toEntity(EmpresaRequestDTO dto);
}
