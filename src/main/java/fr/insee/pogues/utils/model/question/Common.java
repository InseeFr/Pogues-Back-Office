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

    public static final String MAPPING_TARGET_FORMAT = "%d %d";

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
                        String.valueOf(index + 1)))
                .toList();
    }

    public static <A,B> List<MappingType> buildMappingsBasedOnTwoDimensions(List<A> firstList, List<B> secondList, List<ResponseType> responses){
        int responseIndex=0;
        List<MappingType> mappings = new ArrayList<>();
        for(int primaryIndex=0; primaryIndex < firstList.size(); primaryIndex++){
            for(int patternIndex=0; patternIndex < secondList.size(); patternIndex++){
                mappings.add(createNewMapping(
                        responses.get(responseIndex).getId(),
                        String.format(MAPPING_TARGET_FORMAT,primaryIndex+1, patternIndex+1)));
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
