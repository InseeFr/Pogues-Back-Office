package fr.insee.pogues.utils.model.question;

import fr.insee.pogues.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class Common {

    public static String getNewUniqueId(){
        return UUID.randomUUID().toString();
    }

    public static final String MAPPING_TARGET_TWO_DIMENSIONS_FORMAT = "%d %d";
    public static final String MAPPING_TARGET_MULTIPLE_CHOICE_QUESTION_FORMAT = "%d";

    private Common(){
        throw new IllegalStateException("Utility class");
    }


    public static ResponseType cloneResponse(ResponseType response){
        ResponseType clone = new ResponseType();
        clone.setId(response.getId());
        clone.setCollectedVariableReference(response.getCollectedVariableReference());
        clone.setCodeListReference(response.getCodeListReference());
        clone.setDatatype(response.getDatatype());
        clone.setSimple(response.isSimple());
        clone.setMandatory(response.isMandatory());
        return clone;
    }

    public static ResponseType createNewResponseFrom(ResponseType response){
        ResponseType newResponse = new ResponseType();
        newResponse.setId(getNewUniqueId());
        newResponse.setCollectedVariableReference(getNewUniqueId());
        newResponse.setCodeListReference(response.getCodeListReference());
        newResponse.setDatatype(response.getDatatype());
        newResponse.setSimple(response.isSimple());
        newResponse.setMandatory(response.isMandatory());
        return newResponse;
    }

    public static MappingType createNewMapping(String mappingSource, String mappingTarget){
        MappingType mapping = new MappingType();
        mapping.setMappingSource(mappingSource);
        mapping.setMappingTarget(mappingTarget);
        return mapping;
    }

    public static List<MappingType> buildSimpleMappingForMultipleChoiceQuestion(List<ResponseType> responses){
        return IntStream.range(0, responses.size())
                .mapToObj(index -> createNewMapping(
                        responses.get(index).getId(),
                        String.format(MAPPING_TARGET_MULTIPLE_CHOICE_QUESTION_FORMAT, index+1)))
                .toList();
    }

    public static <A,B> List<MappingType> buildMappingsBasedOnTwoDimensions(List<A> firstList, List<B> secondList, List<ResponseType> responses){
        int responseIndex=0;
        List<MappingType> mappings = new ArrayList<>();
        for(int primaryIndex=0; primaryIndex < firstList.size(); primaryIndex++){
            for(int patternIndex=0; patternIndex < secondList.size(); patternIndex++){
                mappings.add(createNewMapping(
                        responses.get(responseIndex).getId(),
                        String.format(MAPPING_TARGET_TWO_DIMENSIONS_FORMAT,primaryIndex+1, patternIndex+1)));
                responseIndex++;
            }
        }
        return mappings;
    }

    public static void removeClarificationQuestion(QuestionType question){
        question.getClarificationQuestion().clear();
        question.getFlowControl().removeIf(flowControl -> FlowControlTypeEnum.CLARIFICATION.equals(flowControl.getFlowControlType()));
    }
}
