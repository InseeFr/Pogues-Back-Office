package fr.insee.pogues.utils.model.question;

import fr.insee.pogues.model.*;

import java.util.*;

import static fr.insee.pogues.utils.model.CodesList.getCodeListWithRef;
import static fr.insee.pogues.utils.model.CodesList.getOnlyCodesWithoutChild;
import static fr.insee.pogues.utils.model.Variables.*;
import static fr.insee.pogues.utils.model.question.Common.*;

public class Table {


    private Table(){
        throw new IllegalStateException("Utility class");
    }

    private static boolean isDimensionBasedOnCodeListId(List<DimensionType> dimensions, String updatedCodeListId){
        return dimensions.stream().anyMatch(dimension -> (isDimensionPrimary(dimension) || isDimensionSecondary(dimension))
                && updatedCodeListId.equals(dimension.getCodeListReference()));
    }

    private static boolean isDimensionHasPrimaryAndSecondary(List<DimensionType> dimensions){
        return dimensions.stream().anyMatch(Table::isDimensionSecondary);
    }

    private static boolean isDimensionSecondary(DimensionType dimension){
        return DimensionTypeEnum.SECONDARY.equals(dimension.getDimensionType());
    }

    public static List<ResponseType> getUniqueResponseType(QuestionType table){
        if(!table.getQuestionType().equals(QuestionTypeEnum.TABLE)) throw new IllegalArgumentException("Arg for this function must be a Table pogues component.");
        Map<Integer, String> uniqueMappingSourceByColumn = getMappingSourceByColumn(table);

        return table.getResponse().stream()
                .filter(responseType -> uniqueMappingSourceByColumn.containsValue(responseType.getId()))
                .map(Common::cloneResponse)
                .toList();
    }

    private static Map<Integer, String> getMappingSourceByColumn(QuestionType table) {
        List<MappingType> actualMappings = table.getResponseStructure().getMapping();
        // Map of column number (start by 1), and its MappingSource, ie the id of response in table's responses
        Map<Integer, String> uniqueMappingSourceByColumn = new HashMap<>();
        for(MappingType mapping: actualMappings){
            int columnNumber = Integer.parseInt(mapping.getMappingTarget().split(" ")[1]);
            uniqueMappingSourceByColumn.put(columnNumber, mapping.getMappingSource());
        }
        return uniqueMappingSourceByColumn;
    }

    public static List<VariableType> updateTableQuestionAccordingToCodeList(QuestionType questionType, CodeList updatedCodeList, List<CodeList> codeListInQuestionnaire) {
        // 1: retrieve existing DataType for response to re-create Response with new collectedVariableReference according to codeList
        // 2: update "Mapping" inside "ResponseStructure" with id of response
        // 3: create Variables with id created in step 1
        // 4: return list all new Variables

        List<VariableType> earlyReturnedValue = List.of();
        if(!questionType.getQuestionType().equals(QuestionTypeEnum.TABLE)) return earlyReturnedValue;

        List<DimensionType> dimensionsOfTable = questionType.getResponseStructure().getDimension();
        // Test if responses has to be re-compute (codeList use as PRIMARY or SECONDARY dimension)
        if (!isDimensionBasedOnCodeListId(dimensionsOfTable, updatedCodeList.getId())) return earlyReturnedValue;
        // Step 1
        List<ResponseType> responsesPattern = getUniqueResponseType(questionType);

        if (isDimensionHasPrimaryAndSecondary(dimensionsOfTable)) {
            // in this case responsesPattern has only one element
            ResponseType responsePattern = responsesPattern.getFirst();
            // we have to duplicate the response n x m (n: size of PRIMARY dimension, m: size of SECONDARY dimension)
            String primaryCodeListId = dimensionsOfTable.stream().filter(Common::isDimensionPrimary).findFirst().get().getCodeListReference();
            String secondaryCodeListId = dimensionsOfTable.stream().filter(Table::isDimensionSecondary).findFirst().get().getCodeListReference();
            CodeList primaryCodeList = getCodeListWithRef(primaryCodeListId, updatedCodeList, codeListInQuestionnaire);
            CodeList secondaryCodeList = getCodeListWithRef(secondaryCodeListId, updatedCodeList, codeListInQuestionnaire);

            List<CodeType> primaryCodeListWithoutChild = getOnlyCodesWithoutChild(primaryCodeList);
            List<CodeType> secondaryCodeListWithoutChild = getOnlyCodesWithoutChild(secondaryCodeList);
            // re-create response according to the two codeList
            // build response column by column (for each code of secondList, create n response where n is length of primaryCodeList)
            List<ResponseType> newResponses = secondaryCodeListWithoutChild.stream()
                    .map(responseType -> primaryCodeListWithoutChild.stream().map(code -> createNewResponseFrom(responsePattern)).toList())
                    .flatMap(Collection::stream)
                    .toList();

            questionType.getResponse().clear();
            questionType.getResponse().addAll(newResponses);
            // step 2 (mapping)
            List<MappingType> newMappings = buildMappingsBasedOnTwoDimensions(
                    secondaryCodeListWithoutChild,
                    primaryCodeListWithoutChild,
                    newResponses);
            questionType.getResponseStructure().getMapping().clear();
            questionType.getResponseStructure().getMapping().addAll(newMappings);
            // remove NotCollected variables (mark in attribute)
            questionType.getResponseStructure().getAttribute().clear();
            // step 3 (variable)
            return buildVariablesBasedOnTwoDimensions(
                    secondaryCodeListWithoutChild, primaryCodeListWithoutChild,
                    newResponses, questionType.getName(),
                    CodeType::getLabel, CodeType::getLabel);
        }
        // Here only PRIMARY dimension and MEASURE
        // re-create response according to updatedCodeList only updatedCodeList is used as primaryDimension
        if(!isDimensionPrimaryAndBasedOnCodeListUpdated(dimensionsOfTable, updatedCodeList.getId())) return earlyReturnedValue;

        // HERE Primary dimension has been updated, we have to build new responses/variables according to existing MEASURE
        String primaryCodeListId = dimensionsOfTable.stream().filter(Common::isDimensionPrimary).findFirst().get().getCodeListReference();
        CodeList primaryCodeList = getCodeListWithRef(primaryCodeListId, updatedCodeList, codeListInQuestionnaire);
        List<CodeType> primaryCodeListWithoutChild = getOnlyCodesWithoutChild(primaryCodeList);

        // build response column by column (for each responsePattern, create n response where n is length of primaryCodeList)
        List<ResponseType> newResponses = responsesPattern.stream()
                .map(responseType -> primaryCodeListWithoutChild.stream().map(code -> createNewResponseFrom(responseType)).toList())
                .flatMap(Collection::stream)
                .toList();

        questionType.getResponse().clear();
        questionType.getResponse().addAll(newResponses);
        // step 2
        List<MappingType> newMappings = buildMappingsBasedOnTwoDimensions(responsesPattern, primaryCodeListWithoutChild, newResponses);

        questionType.getResponseStructure().getMapping().clear();
        questionType.getResponseStructure().getMapping().addAll(newMappings);
        // remove NotCollected variables (mark in attribute)
        questionType.getResponseStructure().getAttribute().clear();

        List<DimensionType> measureDimensions = questionType.getResponseStructure().getDimension().stream()
                .filter(dimensionType -> DimensionTypeEnum.MEASURE.equals(dimensionType.getDimensionType()))
                .toList();
        // step 3
        // in this step, responsesPattern and measureDimensions have the same size :
        return buildVariablesBasedOnTwoDimensions(
                measureDimensions, primaryCodeListWithoutChild,
                newResponses, questionType.getName(),
                DimensionType::getLabel, CodeType::getLabel);
    }
}
