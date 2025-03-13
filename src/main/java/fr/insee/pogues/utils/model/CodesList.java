package fr.insee.pogues.utils.model;

import fr.insee.pogues.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CodesList {

    public static List<QuestionType> getListOfQuestionWhereCodesListIsUsed(Questionnaire questionnaire, String codesListId){
        return questionnaire.getChild().stream()
                .map(componentType -> getListOfQuestionWhereCodesListIsUsed(componentType, codesListId))
                .flatMap(Collection::stream).toList();
    }

    public static List<String> getListOfQuestionIdWhereCodesListIsUsed(Questionnaire questionnaire, String codesListId){
        return getListOfQuestionWhereCodesListIsUsed(questionnaire, codesListId).stream()
                .map(componentType -> componentType.getId())
                .toList();
    }

    public static List<String> getListOfQuestionNameWhereCodesListIsUsed(Questionnaire questionnaire, String codesListId){
        return getListOfQuestionWhereCodesListIsUsed(questionnaire, codesListId).stream()
                .map(componentType -> componentType.getName())
                .toList();
    }

    public static List<QuestionType> getListOfQuestionWhereCodesListIsUsed(ComponentType poguesComponent, String codesListId){
        List<QuestionType> questions = new ArrayList<>();
        if(poguesComponent.getClass().equals(SequenceType.class)){
            ((SequenceType) poguesComponent).getChild().stream().forEach(childComponent -> {
                questions.addAll(getListOfQuestionWhereCodesListIsUsed(childComponent, codesListId));
            });
        }
        if(poguesComponent.getClass().equals(QuestionType.class)){
            QuestionTypeEnum questionType = ((QuestionType) poguesComponent).getQuestionType();
            ((QuestionType) poguesComponent).getResponse().forEach(responseType -> {
                if(codesListId.equals(responseType.getCodeListReference())) questions.add((QuestionType) poguesComponent);
            });
            if(questionType.equals(QuestionTypeEnum.TABLE) || questionType.equals(QuestionTypeEnum.MULTIPLE_CHOICE)){
                ((QuestionType) poguesComponent).getResponseStructure().getDimension().forEach(dimensionType -> {
                    if(codesListId.equals(dimensionType.getCodeListReference())) questions.add((QuestionType) poguesComponent);
                });
            }
        }
        return questions;
    }

    public static List<CodeType> getOnlyCodesWithoutChild(CodeList codeList){
        // Retrieve parent Value in List
        List<String> parentValue = codeList.getCode().stream()
                .filter(code -> code.getParent() != null && !code.getParent().isEmpty())
                .map(code -> code.getParent())
                .distinct()
                .toList();
        // Keep codes that are not parent
        return codeList.getCode().stream().filter(code -> !parentValue.contains(code.getValue())).toList();
    }
}
