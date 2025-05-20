package fr.insee.pogues.service;

import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.utils.CodesListConverter;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.model.CodesList;
import fr.insee.pogues.webservice.model.dtd.nomenclatures.ExtendedNomenclature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.insee.pogues.utils.model.CodesList.getListOfQuestionNameWhereCodesListIsUsed;

@Service
@Slf4j
public class NomenclatureService {

    private final QuestionnairesService questionnairesService;

    public NomenclatureService(QuestionnairesService questionnairesService) {
        this.questionnairesService = questionnairesService;
    }

    public List<ExtendedNomenclature> getNomenclaturesDTD(Questionnaire questionnaire) {
        return questionnaire.getCodeLists().getCodeList().stream()
                .filter(CodesList::isNomenclatureCodeList)
                .map(CodesListConverter::convertFromCodeListNomenclatureModelToNomenclatureDTD)
                .map(nomenclature -> new ExtendedNomenclature(nomenclature, getListOfQuestionNameWhereCodesListIsUsed(questionnaire, nomenclature.getId())))
                .toList();
    }

    public List<ExtendedNomenclature> getNomenclaturesDTDById(String questionnaireId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireById(questionnaireId);
        return getNomenclaturesDTD(questionnaire);
    }

    private Questionnaire retrieveQuestionnaireById(String id) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(questionnairesService.getQuestionnaireByID(id));
    }
}
