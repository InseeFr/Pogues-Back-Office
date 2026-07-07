package fr.insee.pogues.client.surveyregistry.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

/** Codes list metadata returned by the survey registry. */
@Schema(description = "Metadata information for a code list")
public record CodesListMetadataDto(

        @Schema(
                name = "id",
                example = "123e4567-e89b-12d3-a456-426614174000",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        UUID id,

        @NotBlank
        @Schema(
                name = "label",
                description = "Human-readable label for the code list",
                example = "Communes du Nord",
                requiredMode = Schema.RequiredMode.REQUIRED,
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String label,

        @Schema(
                name = "version",
                description = "Version of the code list (auto-incremented)",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Integer version,

        @Schema(
                name = "urn",
                description = "Optional urn. Must refer to an existing external resource if provided.",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String urn,

        @Schema(
                name = "urn",
                description = "Optional searchConfiguration. Refer to suggesterParameters for lunatic, if provided.",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Object searchConfiguration,


        @Schema(
                name = "theme",
                description = "Generic theme for the code list (stable between versions).",
                requiredMode = Schema.RequiredMode.REQUIRED,
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String theme,


        @Schema(
                name = "referenceYear",
                description = "Reference year (4 digits). Should be match pattern \"\\d{4}\".",
                requiredMode = Schema.RequiredMode.REQUIRED,
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String referenceYear
) { }
