package fr.insee.pogues.utils.model;

import fr.insee.pogues.model.*;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CodesList {

    private CodesList() {
        throw new IllegalStateException("Utility class");
    }

    public static List<QuestionType> getListOfQuestionWhereCodesListIsUsed(Questionnaire questionnaire, String codesListId){
        return questionnaire.getChild().stream()
                .map(componentType -> getListOfQuestionWhereCodesListIsUsed(componentType, codesListId))
                .flatMap(Collection::stream)
                .toList();
    }

    public static List<String> getListOfQuestionIdWhereCodesListIsUsed(Questionnaire questionnaire, String codesListId){
        return getListOfQuestionWhereCodesListIsUsed(questionnaire, codesListId).stream()
                .map(ComponentType::getId)
                .distinct()
                .toList();
    }

    public static List<String> getListOfQuestionNameWhereCodesListIsUsed(Questionnaire questionnaire, String codesListId){
        return getListOfQuestionWhereCodesListIsUsed(questionnaire, codesListId).stream()
                .map(ComponentType::getName)
                .distinct()
                .toList();
    }

    public static List<QuestionType> getListOfQuestionWhereCodesListIsUsed(ComponentType poguesComponent, String codesListId){
        List<QuestionType> questions = new ArrayList<>();
        if(poguesComponent instanceof SequenceType){
            ((SequenceType) poguesComponent).getChild().forEach(childComponent -> {
                questions.addAll(getListOfQuestionWhereCodesListIsUsed(childComponent, codesListId));
            });
        }
        if(poguesComponent instanceof QuestionType){
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

    public static CodeList getCodeListWithRef(String codeListRef, CodeList updatedCodeList, List<CodeList> codeListInQuestionnaire){
        if(updatedCodeList.getId().equals(codeListRef)) return updatedCodeList;
        return codeListInQuestionnaire.stream().filter(codeList -> codeList.getId().equals(codeListRef)).findFirst().get();
    }

    public static List<CodeType> getOnlyCodesWithoutChild(CodeList codeList){
        // Retrieve parent Value in List
        List<String> parentValue = codeList.getCode().stream()
                .filter(code -> code.getParent() != null && !code.getParent().isEmpty())
                .map(CodeType::getParent)
                .distinct()
                .toList();
        // Keep codes that are not parent
        return codeList.getCode().stream()
                .filter(code -> !parentValue.contains(code.getValue()))
                .toList();
    }

    public static boolean isNomenclatureCodeList(CodeList codeList){
        return codeList.getSuggesterParameters() != null;
    }
}
