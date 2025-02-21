package fr.insee.pogues.utils.model.question;

import fr.insee.pogues.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static fr.insee.pogues.utils.model.Variables.buildCollectedVariableFromDataType;
import static fr.insee.pogues.utils.model.question.Common.createNewMapping;
import static fr.insee.pogues.utils.model.question.Common.getNewUniqueId;

public class Table {



    private static ResponseType cloneResponse(ResponseType responseType){
        ResponseType response = new ResponseType();
        response.setId(response.getId());
        response.setCodeListReference(responseType.getCodeListReference());
        response.setDatatype(response.getDatatype());
        response.setCollectedVariableReference(responseType.getCollectedVariableReference());
        response.setSimple(response.isSimple());
        response.setMandatory(response.isMandatory());
        return response;
    }

    private static ResponseType createNewResponseFrom(ResponseType responseType){
        ResponseType response = new ResponseType();
        response.setId(getNewUniqueId());
        response.setCollectedVariableReference(getNewUniqueId());
        response.setCodeListReference(responseType.getCodeListReference());
        response.setDatatype(response.getDatatype());
        response.setSimple(response.isSimple());
        response.setMandatory(response.isMandatory());
        return response;
    }

    public static boolean isResponseDataTypeEquals(ResponseType response1, ResponseType response2){
        DatatypeType datatypeType1 = response1.getDatatype();
        DatatypeType datatypeType2 = response2.getDatatype();
        return datatypeType1.getTypeName().equals(datatypeType2.getTypeName()) &&
                datatypeType1.getVisualizationHint().equals(datatypeType2.getVisualizationHint()) &&
                datatypeType1.getVisualizationHint().equals(datatypeType2.getVisualizationHint()) &&
                datatypeType1.isAllowArbitraryResponse() == datatypeType2.isAllowArbitraryResponse();
    };

    private static  boolean isDimensionBasedOnCodeListId(DimensionType dimension, String updatedCodeListId){
        return (DimensionTypeEnum.PRIMARY.equals(dimension.getDimensionType()) && updatedCodeListId.equals(dimension.getCodeListReference()));
    }

    private static  boolean isDimensionBasedOnCodeListSecondaryId(DimensionType dimension, String updatedCodeListId){
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
                            .mapToObj(indexSecondary -> createNewMapping(newResponses.get(index + indexSecondary).getId(), String.format("%d %d", index, indexSecondary)))
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
                                    newResponses.get(index + indexSecondary).getId(),
                                    String.format("%s - %s",updatedCodeList.getCode().get(index).getLabel()), otherCodeListDimension.getCode().get(indexSecondary).getLabel()))
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
                                String.format("%s - %s",index, codeListIndex))).toList())
                .flatMap(Collection::stream)
                .toList();
        questionType.getResponseStructure().getMapping().clear();
        questionType.getResponseStructure().getMapping().addAll(newMappings);
        // step 3
        List<VariableType> newVariables = IntStream.range(0, responsesPattern.size())
                .mapToObj(index -> IntStream.range(0, updatedCodeList.getCode().size())
                        .mapToObj(codeListIndex -> buildCollectedVariableFromDataType(
                                newResponses.get(index+codeListIndex).getDatatype(),
                                newResponses.get(index+codeListIndex).getCollectedVariableReference(),
                                String.format("%s%d%d", questionType.getName(), index, codeListIndex),
                                String.format("%s - %s",index, codeListIndex))).toList())
                .flatMap(Collection::stream)
                .toList();
        return newVariables;
    }
}
