package fr.insee.pogues.client.mapper;

import fr.insee.pogues.client.surveyregistry.mapper.SurveyRegistryMapper;
import fr.insee.pogues.client.surveyregistry.model.CodesListMetadataDto;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SurveyRegistryMapperTest {

    @Test
    @DisplayName("Should convert survey registry codes list metadata DTO into nomenclature DTO")
    void codesListMetadaDtoToDTO_success() {
        // Given a DTO codes list metadata
        UUID uuid = UUID.randomUUID();
        CodesListMetadataDto codesListMetadataDTO = new CodesListMetadataDto(
                uuid,
                "mon-label",
                3,
                "mon-id-externe",
                null,
                "theme",
                "2026"
        );
        NomenclatureDTO expected = new NomenclatureDTO(
                uuid.toString(),
                "mon-label",
                "3",
                "mon-id-externe",
                null,
                "theme",
                "2026"
        );

        // When we convert it to Pogues DTO
        NomenclatureDTO res = SurveyRegistryMapper.toDTO(codesListMetadataDTO);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

}
