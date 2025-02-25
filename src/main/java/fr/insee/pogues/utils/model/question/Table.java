package fr.insee.pogues.utils.model.question;

import com.google.errorprone.annotations.Var;
import fr.insee.pogues.model.*;
import fr.insee.pogues.utils.model.Variables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static fr.insee.pogues.utils.model.CodesList.getOnlyCodesWithoutChild;
import static fr.insee.pogues.utils.model.Variables.*;
import static fr.insee.pogues.utils.model.question.Common.*;

public class Table {

    private static ResponseType cloneResponse(ResponseType response){
        ResponseType clone = new ResponseType();
        clone.setId(response.getId());
        clone.setCodeListReference(response.getCodeListReference());
        clone.setDatatype(response.getDatatype());
        clone.setCollectedVariableReference(response.getCollectedVariableReference());
        clone.setSimple(response.isSimple());
        clone.setMandatory(response.isMandatory());
        return clone;
    }

    private static ResponseType createNewResponseFrom(ResponseType response){
        ResponseType newResponse = new ResponseType();
        newResponse.setId(getNewUniqueId());
        newResponse.setCollectedVariableReference(getNewUniqueId());
        newResponse.setCodeListReference(response.getCodeListReference());
        newResponse.setDatatype(response.getDatatype());
        newResponse.setSimple(response.isSimple());
        newResponse.setMandatory(response.isMandatory());
        return newResponse;
    }

    private static <T> boolean isAttributeAreEquals(T object1, T object2){
        if(object1 == null && object2 == null) return true;
        if(object1 == null || object2 == null) return false;
        return object1.equals(object2);
    }

    public static boolean isResponseDataTypeEquals(ResponseType response1, ResponseType response2){
        DatatypeType datatypeType1 = response1.getDatatype();
        DatatypeType datatypeType2 = response2.getDatatype();
        return isAttributeAreEquals(datatypeType1.getTypeName(), datatypeType2.getTypeName()) &&
                isAttributeAreEquals(datatypeType1.getVisualizationHint(), datatypeType2.getVisualizationHint()) &&
                isAttributeAreEquals(datatypeType1.getVisualizationHint(), datatypeType2.getVisualizationHint()) &&
                datatypeType1.isAllowArbitraryResponse() == datatypeType2.isAllowArbitraryResponse();
    };

    private static boolean isDimensionBasedOnCodeListId(List<DimensionType> dimensions, String updatedCodeListId){
        return dimensions.stream().anyMatch(dimension -> (isDimensionPrimary(dimension) || isDimensionSecondary(dimension))
                && updatedCodeListId.equals(dimension.getCodeListReference()));
    }

    private static List<ResponseType> buildResponseAccordingWithTwoAxis(List<CodeType> primaryCodes, List<CodeType> secondaryCodes, ResponseType responsePattern){
        return primaryCodes.stream()
                .map(primaryCode -> secondaryCodes.stream()
                        .map(secondaryCode -> createNewResponseFrom(responsePattern))
                        .toList())
                .flatMap(Collection::stream)
                .toList();
    }

    private static boolean isDimensionHasPrimaryAndSecondary(List<DimensionType> dimensions){
        return dimensions.stream().anyMatch(dimension -> isDimensionSecondary(dimension));
    }
    private static boolean isDimensionPrimaryAndBasedOnCodeListUpdated(List<DimensionType> dimensions, String updatedCodeListId){
        return dimensions.stream().anyMatch(dimension -> isDimensionPrimary(dimension)
                && updatedCodeListId.equals(dimension.getCodeListReference()));
    }
    private static boolean isDimensionPrimary(DimensionType dimension){
        return DimensionTypeEnum.PRIMARY.equals(dimension.getDimensionType());
    }
    private static boolean isDimensionSecondary(DimensionType dimension){
        return DimensionTypeEnum.SECONDARY.equals(dimension.getDimensionType());
    }

    private static CodeList getCodeListWithRef(String codeListRef, CodeList updatedCodeList, List<CodeList> codeListInQuestionnaire){
        if(updatedCodeList.getId().equals(codeListRef)) return updatedCodeList;
        return codeListInQuestionnaire.stream().filter(codeList -> codeList.getId().equals(codeListRef)).findFirst().get();
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
        List<ResponseType> responsesPattern = new ArrayList<>();
        questionType.getResponse().forEach(responseType -> {
            if (responsesPattern.stream().noneMatch(r -> isResponseDataTypeEquals(r, responseType)))
                responsesPattern.add(cloneResponse(responseType));
        });
        if (isDimensionHasPrimaryAndSecondary(dimensionsOfTable)) {
            // in this case responsesPattern has only one element
            ResponseType responsePattern = responsesPattern.getFirst();
            // we have to duplicate the response n x m (n: size of PRIMARY dimension, m: size of SECONDARY dimension)
            String primaryCodeListId = dimensionsOfTable.stream().filter(dimension -> isDimensionPrimary(dimension)).findFirst().get().getCodeListReference();
            String secondaryCodeListId = dimensionsOfTable.stream().filter(dimension -> isDimensionSecondary(dimension)).findFirst().get().getCodeListReference();
            CodeList primaryCodeList = getCodeListWithRef(primaryCodeListId, updatedCodeList, codeListInQuestionnaire);
            CodeList secondaryCodeList = getCodeListWithRef(secondaryCodeListId, updatedCodeList, codeListInQuestionnaire);

            List<CodeType> primaryCodeListWithoutChild = getOnlyCodesWithoutChild(primaryCodeList);
            List<CodeType> secondaryCodeListWithoutChild = getOnlyCodesWithoutChild(secondaryCodeList);
            // re-create response according to the two codeList
            List<ResponseType> newResponses = buildResponseAccordingWithTwoAxis(primaryCodeListWithoutChild, secondaryCodeListWithoutChild, responsePattern);
            questionType.getResponse().clear();
            questionType.getResponse().addAll(newResponses);
            // step 2 (mapping)
            List<MappingType> newMappings = buildMappingsBasedOnTwoDimensions(primaryCodeListWithoutChild, secondaryCodeListWithoutChild, newResponses);
            questionType.getResponseStructure().getMapping().clear();
            questionType.getResponseStructure().getMapping().addAll(newMappings);
            // step 3 (variable)
            List<VariableType> newVariables = buildVariablesBasedOnTwoDimensions(
                    primaryCodeListWithoutChild, secondaryCodeListWithoutChild,
                    newResponses, questionType.getName(),
                    secondaryCode -> secondaryCode.getLabel());
            return newVariables;
        }
        // Here only PRIMARY dimension and MEASURE
        // re-create response according to updatedCodeList only updatedCodeList is used as primaryDimension
        if(!isDimensionPrimaryAndBasedOnCodeListUpdated(dimensionsOfTable, updatedCodeList.getId())) return earlyReturnedValue;
        String primaryCodeListId = dimensionsOfTable.stream().filter(dimension -> isDimensionPrimary(dimension)).findFirst().get().getCodeListReference();
        CodeList primaryCodeList = getCodeListWithRef(primaryCodeListId, updatedCodeList, codeListInQuestionnaire);
        List<CodeType> primaryCodeListWithoutChild = getOnlyCodesWithoutChild(primaryCodeList);
        List<ResponseType> newResponses = primaryCodeListWithoutChild.stream()
                .map(codeType -> responsesPattern.stream().map(responsePattern -> createNewResponseFrom(responsePattern)).toList())
                .flatMap(Collection::stream)
                .toList();

        questionType.getResponse().clear();
        questionType.getResponse().addAll(newResponses);
        // step 2
        List<MappingType> newMappings = buildMappingsBasedOnTwoDimensions(primaryCodeListWithoutChild, responsesPattern, newResponses);
        questionType.getResponseStructure().getMapping().clear();
        questionType.getResponseStructure().getMapping().addAll(newMappings);

        List<DimensionType> measureDimensions = questionType.getResponseStructure().getDimension().stream()
                .filter(dimensionType -> DimensionTypeEnum.MEASURE.equals(dimensionType.getDimensionType()))
                .toList();
        // step 3
        List<VariableType> newVariables = buildVariablesBasedOnTwoDimensions(
                primaryCodeListWithoutChild, measureDimensions, newResponses,
                questionType.getName(), measureDimension -> measureDimension.getLabel());
        return newVariables;
    }
}
