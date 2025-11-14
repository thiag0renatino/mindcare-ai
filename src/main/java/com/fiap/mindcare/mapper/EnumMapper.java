package com.fiap.mindcare.mapper;

import com.fiap.mindcare.enuns.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnumMapper {

    default TipoUsuario toTipoUsuario(String value) {
        if (value == null) return null;
        return TipoUsuario.valueOf(value.trim().toUpperCase());
    }

    default String fromTipoUsuario(TipoUsuario value) {
        return value != null ? value.name() : null;
    }

    default RiscoTriagem toRiscoTriagem(String value) {
        if (value == null) return null;
        return RiscoTriagem.valueOf(value.trim().toUpperCase());
    }

    default String fromRiscoTriagem(RiscoTriagem value) {
        return value != null ? value.name() : null;
    }

    default TipoEncaminhamento toTipoEncaminhamento(String value) {
        if (value == null) return null;
        return TipoEncaminhamento.valueOf(value.trim().toUpperCase());
    }

    default String fromTipoEncaminhamento(TipoEncaminhamento value) {
        return value != null ? value.name() : null;
    }

    default PrioridadeEncaminhamento toPrioridadeEncaminhamento(String value) {
        if (value == null) return null;
        return PrioridadeEncaminhamento.valueOf(value.trim().toUpperCase());
    }

    default String fromPrioridadeEncaminhamento(PrioridadeEncaminhamento value) {
        return value != null ? value.name() : null;
    }

    default StatusEncaminhamento toStatusEncaminhamento(String value) {
        if (value == null) return null;
        return StatusEncaminhamento.valueOf(value.trim().toUpperCase());
    }

    default String fromStatusEncaminhamento(StatusEncaminhamento value) {
        return value != null ? value.name() : null;
    }

    default TipoEventoAcompanhamento toTipoEventoAcompanhamento(String value) {
        if (value == null) return null;
        return TipoEventoAcompanhamento.valueOf(value.trim().toUpperCase());
    }

    default String fromTipoEventoAcompanhamento(TipoEventoAcompanhamento value) {
        return value != null ? value.name() : null;
    }
}
