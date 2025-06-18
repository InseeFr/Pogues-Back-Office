package fr.insee.pogues.service;

import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.utils.CodesListConverter;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.model.CodesList;
import fr.insee.pogues.webservice.model.dtd.nomenclatures.ExtendedNomenclature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static fr.insee.pogues.utils.model.CodesList.getListOfQuestionNameWhereCodesListIsUsed;

@Service
@Slf4j
public class NomenclatureService {

    private final QuestionnairesService questionnairesService;
    private final VersionService versionService;

    public NomenclatureService(QuestionnairesService questionnairesService,
                               VersionService versionService) {
        this.questionnairesService = questionnairesService;
        this.versionService = versionService;
    }

    public List<ExtendedNomenclature> getNomenclaturesDTD(Questionnaire questionnaire) {
        return questionnaire.getCodeLists().getCodeList().stream()
                .filter(CodesList::isNomenclatureCodeList)
                .map(CodesListConverter::convertFromCodeListNomenclatureModelToNomenclatureDTD)
                .map(nomenclature -> new ExtendedNomenclature(nomenclature, getListOfQuestionNameWhereCodesListIsUsed(questionnaire, nomenclature.getId())))
                .toList();
    }

    public List<ExtendedNomenclature> getNomenclaturesDTDByQuestionnaireId(String questionnaireId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        return getNomenclaturesDTD(questionnaire);
    }

    public List<ExtendedNomenclature> getNomenclaturesDTDByVersionId(UUID versionId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByVersionId(versionId);
        return getNomenclaturesDTD(questionnaire);
    }

    private Questionnaire retrieveQuestionnaireByQuestionnaireId(String id) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(questionnairesService.getQuestionnaireByID(id));
    }

    private Questionnaire retrieveQuestionnaireByVersionId(UUID versionId) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(versionService.getVersionDataByVersionId(versionId));
    }
}
