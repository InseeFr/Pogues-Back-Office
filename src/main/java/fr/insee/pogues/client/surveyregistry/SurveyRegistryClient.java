package fr.insee.pogues.client.surveyregistry;

import fr.insee.pogues.model.dto.nomenclatures.NomenclatureDTO;

import java.util.List;
import java.util.UUID;

/** Client to access registry data. */
public interface SurveyRegistryClient {
    List<NomenclatureDTO> getNomenclatures();
    NomenclatureDTO getNomenclatureMetadataById(UUID id);
}
