package fr.insee.pogues.client.surveyregistry.mapper;

import fr.insee.pogues.client.surveyregistry.model.CodesListMetadataDto;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureDTO;

public final class SurveyRegistryMapper {
    private SurveyRegistryMapper() {}

    public static NomenclatureDTO toDTO(CodesListMetadataDto codesListMetadata) {
        return new NomenclatureDTO(
                codesListMetadata.id().toString(),
                codesListMetadata.label(),
                Integer.toString(codesListMetadata.version()),
                codesListMetadata.urn(),
                codesListMetadata.searchConfiguration(),
                codesListMetadata.theme(),
                codesListMetadata.referenceYear()
        );
    }

}
