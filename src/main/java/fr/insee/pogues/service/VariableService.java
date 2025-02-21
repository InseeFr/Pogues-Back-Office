package fr.insee.pogues.service;

import fr.insee.pogues.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

import static fr.insee.pogues.utils.model.CodesList.getListOfQuestionWhereCodesListIsUsed;
import static fr.insee.pogues.utils.model.question.MultipleChoice.updateMultipleChoiceQuestionAccordingToCodeList;
import static fr.insee.pogues.utils.model.question.Table.updateTableQuestionAccordingToCodeList;
import static fr.insee.pogues.utils.model.Variables.*;

@Service
public class VariableService {

    public void updateQuestionAndVariablesAccordingToCodesList(Questionnaire questionnaire, String updatedCodeListId){
        // Retrieve updatedCodeList in questionnaire
        CodeList codeList = questionnaire.getCodeLists().getCodeList().stream().filter(cL -> updatedCodeListId.equals(cL.getId())).findFirst().get();
        // Just retrieve MULTIPLE_CHOICE and TABLE questions
        List<QuestionType> questionsToModify = getListOfQuestionWhereCodesListIsUsed(questionnaire, updatedCodeListId).stream()
                .filter(questionType -> {
                    QuestionTypeEnum questionTypeEnum = questionType.getQuestionType();
                    return QuestionTypeEnum.MULTIPLE_CHOICE.equals(questionTypeEnum) || QuestionTypeEnum.TABLE.equals(questionTypeEnum);
                })
                .toList();
        // modify Multiple and Table question and get there new Variables
        List<VariableType> variables = questionsToModify.stream()
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
    }

}
