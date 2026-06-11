package fr.insee.pogues.service;

import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.mapper.CodesListMapper;
import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.dto.nomenclatures.ExtendedNomenclatureDTO;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureDTO;
import fr.insee.pogues.persistence.service.IQuestionnaireService;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.utils.model.CodesList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static fr.insee.pogues.utils.model.CodesList.getListOfQuestionNameWhereCodesListIsUsed;

@Service
@Slf4j
public class NomenclatureService {

    private final IQuestionnaireService questionnaireService;
    private final VersionService versionService;

    public NomenclatureService(IQuestionnaireService questionnaireService,
                               VersionService versionService) {
        this.questionnaireService = questionnaireService;
        this.versionService = versionService;
    }

    /**
     * Fetch the nomenclatures of a questionnaire.
     * @param questionnaireId ID of the questionnaire to fetch the nomenclatures from
     * @throws Exception Could not read from the DB
     * @throws PoguesException 404 questionnaire not found
     */
    public List<ExtendedNomenclatureDTO> getQuestionnaireNomenclatures(String questionnaireId) throws Exception {
        Questionnaire questionnaire = questionnaireService.getQuestionnaireModelByID(questionnaireId);
        List<CodeList> nomenclatures = getQuestionnaireNomenclatures(questionnaire);
        return computeNomenclatureDTO(nomenclatures, questionnaire);
    }

    /**
     * Fetch the nomenclatures of a questionnaire's version.
     * @param versionId ID of the questionnaire's version to fetch the nomenclatures from
     * @throws Exception Could not read from the DB
     * @throws PoguesException 404 questionnaire not found
     */
    public List<ExtendedNomenclatureDTO> getVersionNomenclatures(UUID versionId) throws Exception {
        Questionnaire questionnaire = versionService.getVersionDataQuestionnaireModelByVersionId(versionId);
        List<CodeList> nomenclatures = getQuestionnaireNomenclatures(questionnaire);
        return computeNomenclatureDTO(nomenclatures, questionnaire);
    }

    public List<CodeList> getQuestionnaireNomenclatures(Questionnaire questionnaire) {
        return questionnaire.getCodeLists().getCodeList().stream()
                .filter(CodesList::isNomenclatureCodeList)
                .toList();
    }

    private List<ExtendedNomenclatureDTO> computeNomenclatureDTO(List<CodeList> nomenclatures, Questionnaire questionnaire) {
        return nomenclatures.stream()
                .map(CodesListMapper::toNomenclatureDTO)
                .map(nomenclature -> new ExtendedNomenclatureDTO(
                        nomenclature,
                        getListOfQuestionNameWhereCodesListIsUsed(questionnaire, nomenclature.getId())
                )).toList();
    }

    public List<NomenclatureDTO> getAllNomenclatures() {
        ObjectMapper objectMapper = JsonMapper.builder().build();

        Resource mockResource = new ClassPathResource("nomenclatures/mock.json");
        Map<String, NomenclatureDTO> mockNomenclatures = null;
        try {
            mockNomenclatures = objectMapper.readValue(
                    mockResource.getInputStream(),
                    new TypeReference<>() {});
        } catch (IOException e) {
            throw new PoguesException(500, "Error when retrieve nomenclatures", e.getMessage());
        }
        return mockNomenclatures.values().stream().toList();
    }
}
