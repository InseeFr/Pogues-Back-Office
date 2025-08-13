package fr.insee.pogues.service;

import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.service.QuestionnaireService;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.webservice.mapper.CodesListMapper;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.model.CodesList;
import fr.insee.pogues.webservice.model.dto.nomenclatures.ExtendedNomenclatureDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static fr.insee.pogues.utils.model.CodesList.getListOfQuestionNameWhereCodesListIsUsed;

@Service
@Slf4j
public class NomenclatureService {

    private final QuestionnaireService questionnaireService;
    private final VersionService versionService;

    public NomenclatureService(QuestionnaireService questionnaireService,
                               VersionService versionService) {
        this.questionnaireService = questionnaireService;
        this.versionService = versionService;
    }

    public List<ExtendedNomenclatureDTO> getNomenclaturesDTO(Questionnaire questionnaire) {
        return questionnaire.getCodeLists().getCodeList().stream()
                .filter(CodesList::isNomenclatureCodeList)
                .map(CodesListMapper::convertFromCodeListNomenclatureModelToNomenclatureDTO)
                .map(nomenclature -> new ExtendedNomenclatureDTO(nomenclature, getListOfQuestionNameWhereCodesListIsUsed(questionnaire, nomenclature.getId())))
                .toList();
    }

    public List<ExtendedNomenclatureDTO> getNomenclaturesDTOByQuestionnaireId(String questionnaireId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        return getNomenclaturesDTO(questionnaire);
    }

    public List<ExtendedNomenclatureDTO> getNomenclaturesDTOByVersionId(UUID versionId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByVersionId(versionId);
        return getNomenclaturesDTO(questionnaire);
    }

    private Questionnaire retrieveQuestionnaireByQuestionnaireId(String id) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(questionnaireService.getQuestionnaireByID(id));
    }

    private Questionnaire retrieveQuestionnaireByVersionId(UUID versionId) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(versionService.getVersionDataByVersionId(versionId));
    }
}
