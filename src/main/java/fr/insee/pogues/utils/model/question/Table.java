package fr.insee.pogues.utils.model.question;

import fr.insee.pogues.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static fr.insee.pogues.utils.model.Variables.buildCollectedVariableFromDataType;
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

    private static boolean isDimensionBasedOnCodeListId(DimensionType dimension, String updatedCodeListId){
        return (DimensionTypeEnum.PRIMARY.equals(dimension.getDimensionType()) && updatedCodeListId.equals(dimension.getCodeListReference()));
    }

    private static boolean isDimensionBasedOnCodeListSecondaryId(DimensionType dimension, String updatedCodeListId){
        return DimensionTypeEnum.SECONDARY.equals(dimension.getDimensionType()) && updatedCodeListId.equals(dimension.getCodeListReference());
    }


    public static List<VariableType> updateTableQuestionAccordingToCodeList(QuestionType questionType, CodeList updatedCodeList, List<CodeList> codeListInQuestionnaire) {
        // 1: retrieve existing DataType for response to re-create Response with new collectedVariableReference according to codeList
        // 2: update "Mapping" inside "ResponseStructure" with id of response
        // 3: create Variables with id created in step 1
        // 4: return list all new Variables

        // Test if responses has to be re-compute (codeList use as PRIMARY or SECONDARY dimension)
        if (!questionType.getQuestionType().equals(QuestionTypeEnum.TABLE) &&
                questionType.getResponseStructure()
                        .getDimension().stream()
                        .anyMatch(dimension -> isDimensionBasedOnCodeListId(dimension, updatedCodeList.getId())))
            return List.of();


        // Step 1
        List<ResponseType> responsesPattern = new ArrayList<>();
        questionType.getResponse().forEach(responseType -> {
            if (responsesPattern.stream().noneMatch(r -> isResponseDataTypeEquals(r, responseType)))
                responsesPattern.add(cloneResponse(responseType));
        });
        if (questionType.getResponseStructure().getDimension().stream().anyMatch(dimension -> isDimensionBasedOnCodeListSecondaryId(dimension, updatedCodeList.getId()))) {
            // in this case responsesPattern has only one element
            ResponseType responsePattern = responsesPattern.getFirst();
            // we have to duplicate the response n x m (n: size of PRIMARY dimension, m: size of SECONDARY dimension)
            String otherCodeListIdDimension = questionType.getResponseStructure().getDimension().stream()
                    .filter(dimension -> !updatedCodeList.getId().equals(dimension.getCodeListReference()))
                    .findFirst()
                    .get()
                    .getCodeListReference();
            CodeList otherCodeListDimension = codeListInQuestionnaire.stream().filter(codeList -> otherCodeListIdDimension.equals(codeList.getId())).findFirst().get();
            // re-create response according to the two codeList
            List<ResponseType> newResponses = updatedCodeList.getCode().stream()
                    .map(codeType -> otherCodeListDimension.getCode().stream().map(codeType1 -> createNewResponseFrom(responsePattern)).toList())
                    .flatMap(Collection::stream)
                    .toList();
            questionType.getResponse().clear();
            questionType.getResponse().addAll(newResponses);
            // step 2 (mapping)
            List<MappingType> newMappings = IntStream.range(0, updatedCodeList.getCode().size())
                    .mapToObj(index -> IntStream.range(0, otherCodeListDimension.getCode().size())
                            .mapToObj(indexSecondary -> createNewMapping(
                                    newResponses.get(index + indexSecondary).getId(),
                                    String.format(MAPPING_TARGET_FORMAT, index+1, indexSecondary+1)))
                            .toList())
                    .flatMap(Collection::stream)
                    .toList();
            questionType.getResponseStructure().getMapping().clear();
            questionType.getResponseStructure().getMapping().addAll(newMappings);
            // step 3 (variable)
            List<VariableType> newVariables = IntStream.range(0, updatedCodeList.getCode().size())
                    .mapToObj(index -> IntStream.range(0, otherCodeListDimension.getCode().size())
                            .mapToObj(indexSecondary -> buildCollectedVariableFromDataType(
                                    responsePattern.getDatatype(),
                                    newResponses.get(index + indexSecondary).getCollectedVariableReference(),
                                    String.format("%s%d%d", questionType.getName(), index+1, indexSecondary+1),
                                    String.format(COLLECTED_LABEL_FORMAT,
                                            updatedCodeList.getCode().get(index).getLabel(),
                                    otherCodeListDimension.getCode().get(indexSecondary).getLabel())))
                            .toList())
                    .flatMap(Collection::stream)
                    .toList();
            return newVariables;
        }
        // re-create response according to updatedCodeList
        List<ResponseType> newResponses = responsesPattern.stream()
                .map(responsePattern -> updatedCodeList.getCode().stream().map(codeType -> createNewResponseFrom(responsePattern)).toList())
                .flatMap(Collection::stream)
                .toList();

        questionType.getResponse().clear();
        questionType.getResponse().addAll(newResponses);
        // step 2
        List<MappingType> newMappings = IntStream.range(0, responsesPattern.size())
                .mapToObj(index -> IntStream.range(0, updatedCodeList.getCode().size())
                        .mapToObj(codeListIndex -> createNewMapping(
                                newResponses.get(index+codeListIndex).getId(),
                                String.format(MAPPING_TARGET_FORMAT,index+1, codeListIndex+1))).toList())
                .flatMap(Collection::stream)
                .toList();
        questionType.getResponseStructure().getMapping().clear();
        questionType.getResponseStructure().getMapping().addAll(newMappings);

        List<DimensionType> measureDimensions = questionType.getResponseStructure().getDimension().stream()
                .filter(dimensionType -> DimensionTypeEnum.MEASURE.equals(dimensionType.getDimensionType()))
                .toList();
        // step 3
        List<VariableType> newVariables = IntStream.range(0, responsesPattern.size())
                .mapToObj(index -> IntStream.range(0, updatedCodeList.getCode().size())
                        .mapToObj(codeListIndex -> buildCollectedVariableFromDataType(
                                newResponses.get(index+codeListIndex).getDatatype(),
                                newResponses.get(index+codeListIndex).getCollectedVariableReference(),
                                String.format("%s%d%d", questionType.getName(), index+1, codeListIndex+1),
                                String.format(
                                        COLLECTED_LABEL_FORMAT,updatedCodeList.getCode().get(codeListIndex).getLabel(),
                                        measureDimensions.get(index).getLabel())))
                        .toList())
                .flatMap(Collection::stream)
                .toList();
        return newVariables;
    }
}
