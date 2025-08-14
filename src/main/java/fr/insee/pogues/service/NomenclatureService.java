package fr.insee.pogues.service;

import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.service.IQuestionnaireService;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.mapper.CodesListMapper;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.model.CodesList;
import fr.insee.pogues.model.dto.nomenclatures.ExtendedNomenclatureDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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

    private Questionnaire retrieveQuestionnaireByQuestionnaireId(String id) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(questionnaireService.getQuestionnaireByID(id));
    }

    private Questionnaire retrieveQuestionnaireByVersionId(UUID versionId) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(versionService.getVersionDataByVersionId(versionId));
    }

    /**
     * Fetch the nomenclatures of a questionnaire.
     * @param questionnaireId ID of the questionnaire to fetch the nomenclatures from
     * @throws Exception Could not read from the DB
     * @throws PoguesException 404 questionnaire not found
     */
    public List<ExtendedNomenclatureDTO> getQuestionnaireNomenclatures(String questionnaireId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
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
        Questionnaire questionnaire = retrieveQuestionnaireByVersionId(versionId);
        List<CodeList> nomenclatures = getQuestionnaireNomenclatures(questionnaire);
        return computeNomenclatureDTO(nomenclatures, questionnaire);
    }

    private List<CodeList> getQuestionnaireNomenclatures(Questionnaire questionnaire) {
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
}
