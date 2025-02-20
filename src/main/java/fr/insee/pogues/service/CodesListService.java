package fr.insee.pogues.service;

import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.webservice.model.dtd.codeList.CodesList;
import fr.insee.pogues.webservice.rest.PoguesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static fr.insee.pogues.utils.CodesListConverter.*;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

@Service
@Slf4j
public class CodesListService {

    @Autowired
    QuestionnairesService questionnairesService;

    public boolean updateOrAddCodeListToQuestionnaire(String questionnaireId, String idCodesList, CodesList codesList) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireWithId(questionnaireId);
        boolean created = updateOrAddCodeListToQuestionnaire(questionnaire, idCodesList, codesList);
        updateQuestionnaireInDataBase(questionnaire);
        return created;
    }

    public boolean updateOrAddCodeListToQuestionnaire(Questionnaire questionnaire, String idCodesList, CodesList codesList) throws Exception {
        List<fr.insee.pogues.model.CodeList> codesLists = questionnaire.getCodeLists().getCodeList();
        return updateOrAddCodeListDTD(codesLists, idCodesList, codesList);
    }

    public void deleteCodeListOfQuestionnaireWithId(String questionnaireId, String codesListId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireWithId(questionnaireId);
        deleteCodeListOfQuestionnaire(questionnaire, codesListId);
        updateQuestionnaireInDataBase(questionnaire);
    }

    public void deleteCodeListOfQuestionnaire(Questionnaire questionnaire, String codesListId) throws Exception {
        List<String> questionIds = getListOfQuestionIdWhereCodesListIsUsed(questionnaire, codesListId);
        if(!questionIds.isEmpty()){
            throw new PoguesException(400,"The codesList is required", String.format(
                    "CodesList with id %s is required for following questions %s", codesListId, questionIds
            ));

        }
        List<fr.insee.pogues.model.CodeList> codesLists = questionnaire.getCodeLists().getCodeList();
        removeCodeListDTD(codesLists, codesListId);
    }

    private Questionnaire retrieveQuestionnaireWithId(String id) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(questionnairesService.getQuestionnaireByID(id));
    }

    private void updateQuestionnaireInDataBase(Questionnaire questionnaire) throws Exception {
        questionnairesService.updateQuestionnaire(
                questionnaire.getId(),
                jsonStringtoJsonNode(PoguesSerializer.questionnaireJavaToString(questionnaire)));
    }

    /**
     *
     * @param existingCodeLists
     * @param idCodesList
     * @param codesListDtdToUpdate
     * @return true if new codeList is created
     */
    boolean updateOrAddCodeListDTD(List<CodeList> existingCodeLists, String idCodesList, CodesList codesListDtdToUpdate) {
        if(existingCodeLists.stream().noneMatch(codeList -> Objects.equals(idCodesList, codeList.getId()))){
            addCodeListDTD(existingCodeLists, codesListDtdToUpdate);
            return true;
        }
        codesListDtdToUpdate.setId(idCodesList);
        replaceElementInListAccordingCondition(
                existingCodeLists,
                codeList -> Objects.equals(idCodesList, codeList.getId()),
                codesListDtdToUpdate,
                codesList -> convertFromCodeListDTDtoCodeListModel(codesListDtdToUpdate));
        // TODO: update variables according to newCodeList needed
        return false;
    }

    List<String> getListOfQuestionIdWhereCodesListIsUsed(Questionnaire questionnaire, String codesListId){
        return questionnaire.getChild().stream()
                .map(componentType -> getListOfQuestionIdWhereCodesListIsUsed(componentType, codesListId))
                .flatMap(Collection::stream).toList();
    }

    List<String> getListOfQuestionIdWhereCodesListIsUsed(ComponentType poguesComponent, String codesListId){
        List<String> idList = new ArrayList<>();
        if(poguesComponent.getClass().equals(SequenceType.class)){
            ((SequenceType) poguesComponent).getChild().stream().forEach(childComponent -> {
                idList.addAll(getListOfQuestionIdWhereCodesListIsUsed(childComponent, codesListId));
            });
        }
        if(poguesComponent.getClass().equals(QuestionType.class)){
            String questionId = poguesComponent.getId();
            QuestionTypeEnum questionType = ((QuestionType) poguesComponent).getQuestionType();
            ((QuestionType) poguesComponent).getResponse().forEach(responseType -> {
                if(codesListId.equals(responseType.getCodeListReference())) idList.add(questionId);
            });
            if(questionType.equals(QuestionTypeEnum.TABLE) || questionType.equals(QuestionTypeEnum.MULTIPLE_CHOICE)){
                ((QuestionType) poguesComponent).getResponseStructure().getDimension().forEach(dimensionType -> {
                    if(codesListId.equals(dimensionType.getCodeListReference())) idList.add(questionId);
                });
            }
        }
        return idList;
    }


    void addCodeListDTD(List<CodeList> existingCodeLists, CodesList codesListDtdToAdd){
        existingCodeLists.add(convertFromCodeListDTDtoCodeListModel(codesListDtdToAdd));
    }

    void removeCodeListDTD(List<CodeList> existingCodeLists, String id) throws PoguesException {
        boolean deleted = existingCodeLists.removeIf(codeList -> id.equals(codeList.getId()));
        if(!deleted) throw new PoguesException(404,"Not found", String.format("CodesList with id %s doesn't exist in questionnaire", id));
    }

    private <T,G> void replaceElementInListAccordingCondition(List<T> elements, Predicate<T> conditionFunction, G newElement, Function<G,T> factory){
        for (int i = 0; i < elements.size(); i++) {
            T element = elements.get(i);
            if (conditionFunction.test(element)) {
                elements.set(i, factory.apply(newElement));
                break;
            }
        }
    }

}
