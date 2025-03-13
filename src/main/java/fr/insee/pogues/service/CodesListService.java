package fr.insee.pogues.service;

import fr.insee.pogues.exception.CodesListException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.utils.CodesListConverter;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.webservice.model.dtd.codeList.CodesList;
import fr.insee.pogues.webservice.model.dtd.codeList.ExtendedCodesList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static fr.insee.pogues.utils.CodesListConverter.*;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static fr.insee.pogues.utils.model.CodesList.*;
import static fr.insee.pogues.utils.model.Variables.getNeededCollectedVariablesInQuestionnaire;
import static fr.insee.pogues.utils.model.question.Common.removeClarificationQuestion;
import static fr.insee.pogues.utils.model.question.MultipleChoice.updateMultipleChoiceQuestionAccordingToCodeList;
import static fr.insee.pogues.utils.model.question.Table.updateTableQuestionAccordingToCodeList;

@Service
@Slf4j
public class CodesListService {
    private QuestionnairesService questionnairesService;

    @Autowired
    public CodesListService(QuestionnairesService questionnairesService){
        this.questionnairesService = questionnairesService;
    }

    /**
     *
     * @param questionnaireId
     * @param idCodesList
     * @param codesList
     * @return the list of question's id updated (or null if created)
     * @throws Exception
     */
    public List<String> updateOrAddCodeListToQuestionnaire(String questionnaireId, String idCodesList, CodesList codesList) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireWithId(questionnaireId);
        List<String> updatedQuestionIds = updateOrAddCodeListToQuestionnaire(questionnaire, idCodesList, codesList);
        updateQuestionnaireInDataBase(questionnaire);
        return updatedQuestionIds;
    }
    /**
     *
     * @param questionnaire
     * @param idCodesList
     * @param codesList
     * @return the list of question's id updated (or null if created)
     * @throws Exception
     */
    public List<String> updateOrAddCodeListToQuestionnaire(Questionnaire questionnaire, String idCodesList, CodesList codesList) {
        List<fr.insee.pogues.model.CodeList> codesLists = questionnaire.getCodeLists().getCodeList();
        boolean created = updateOrAddCodeListDTD(codesLists, idCodesList, codesList);
        if(!created)  return updateQuestionAndVariablesAccordingToCodesList(questionnaire, idCodesList);
        return null;
    }

    public void deleteCodeListOfQuestionnaireWithId(String questionnaireId, String codesListId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireWithId(questionnaireId);
        deleteCodeListOfQuestionnaire(questionnaire, codesListId);
        updateQuestionnaireInDataBase(questionnaire);
    }

    public void deleteCodeListOfQuestionnaire(Questionnaire questionnaire, String codesListId) throws Exception {
        List<String> questionIds = getListOfQuestionIdWhereCodesListIsUsed(questionnaire, codesListId);
        if(!questionIds.isEmpty()){
            throw new CodesListException(400, String.format("CodesList with id %s is required.", codesListId), "", questionIds);

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
        return false;
    }


    void addCodeListDTD(List<CodeList> existingCodeLists, CodesList codesListDtdToAdd){
        existingCodeLists.add(convertFromCodeListDTDtoCodeListModel(codesListDtdToAdd));
    }

    void removeCodeListDTD(List<CodeList> existingCodeLists, String id) throws CodesListException {
        boolean deleted = existingCodeLists.removeIf(codeList -> id.equals(codeList.getId()));
        if(!deleted) throw new CodesListException(404, "Not found", String.format("CodesList with id %s doesn't exist in questionnaire", id),null);
    }

    List<String> updateQuestionAndVariablesAccordingToCodesList(Questionnaire questionnaire, String updatedCodeListId){
        // Retrieve updatedCodeList in questionnaire
        CodeList codeList = questionnaire.getCodeLists().getCodeList().stream().filter(cL -> updatedCodeListId.equals(cL.getId())).findFirst().get();
        // Just retrieve MULTIPLE_CHOICE and TABLE questions
        List<QuestionType> questionsToModify = getListOfQuestionWhereCodesListIsUsed(questionnaire, updatedCodeListId);
        // Clear Clarification question for concerned question
        questionsToModify.forEach(question -> removeClarificationQuestion(question));
        // modify Multiple and Table question and get there new Variables
        List<QuestionType> multipleAndTableQuestion = questionsToModify.stream()
                .filter(questionType -> {
                    QuestionTypeEnum questionTypeEnum = questionType.getQuestionType();
                    return QuestionTypeEnum.MULTIPLE_CHOICE.equals(questionTypeEnum) || QuestionTypeEnum.TABLE.equals(questionTypeEnum);})
                .toList();
        List<VariableType> variables = multipleAndTableQuestion.stream()
                .map(questionType -> {
                    QuestionTypeEnum questionTypeEnum = questionType.getQuestionType();
                    if(QuestionTypeEnum.MULTIPLE_CHOICE.equals(questionTypeEnum)) return updateMultipleChoiceQuestionAccordingToCodeList(questionType, codeList);
                    return updateTableQuestionAccordingToCodeList(questionType, codeList, questionnaire.getCodeLists().getCodeList());
                })
                .flatMap(Collection::stream)
                .toList();
        // add new variables to questionnaire
        questionnaire.getVariables().getVariable().addAll(variables);
        // Delete variables that are not referenced in response
        List<String> neededCollectedVariables = getNeededCollectedVariablesInQuestionnaire(questionnaire);
        questionnaire.getVariables().getVariable().removeIf(variableType -> !neededCollectedVariables.contains(variableType.getId()));
        return questionsToModify.stream().map(question -> question.getId()).toList();
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

    public List<ExtendedCodesList> getCodesListsDTD(Questionnaire questionnaire) throws Exception {
        return questionnaire.getCodeLists().getCodeList().stream()
                .map(CodesListConverter::convertFromCodeListModelToCodeListDTD)
                .map(codesList -> new ExtendedCodesList(codesList, getListOfQuestionNameWhereCodesListIsUsed(questionnaire, codesList.getId())))
                .toList();
    }

    public List<ExtendedCodesList> getCodesListsDTDWithId(String questionnaireId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireWithId(questionnaireId);
        return getCodesListsDTD(questionnaire);
    }
}
