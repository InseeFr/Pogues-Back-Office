package fr.insee.pogues.model.dto.mapping.codes.list.registre;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MappingCodesListRegistreCreateDTO(

        @NotBlank
        String poguesCodesListId,

        @NotNull
        UUID registreCodesListId
) {}