package fr.insee.pogues.model.dto.mapping.codes.list.registre;

import java.util.UUID;

public record MappingCodesListRegistreResponseDTO(
        Long id,
        String poguesCodesListId,
        UUID registreCodesListId
) {}