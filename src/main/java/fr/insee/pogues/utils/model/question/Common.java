package fr.insee.pogues.utils.model.question;

import fr.insee.pogues.model.CodeType;
import fr.insee.pogues.model.MappingType;
import fr.insee.pogues.model.ResponseType;

import java.util.Collection;
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

    public static List<MappingType> buildMappingsAccordingWithTowAxisAndResponses(List<CodeType> primaryCodes, List<CodeType> secondaryCodes, List<ResponseType> responses){
        return IntStream.range(0, primaryCodes.size())
                .mapToObj(index -> IntStream.range(0, secondaryCodes.size())
                        .mapToObj(indexSecondary -> createNewMapping(
                                responses.get(index + indexSecondary).getId(),
                                String.format(MAPPING_TARGET_FORMAT, index+1, indexSecondary+1)))
                        .toList())
                .flatMap(Collection::stream)
                .toList();
    }

    public static List<MappingType> buildSimpleMappingForMultipleChoiceQuestion(List<ResponseType> responses){
        return IntStream.range(0, responses.size())
                .mapToObj(index -> createNewMapping(
                        responses.get(index).getId(),
                        String.valueOf(index + 1)))
                .toList();
    }

    public static List<MappingType> buildMappingsAccordingToOneAxeAndResponses(List<CodeType> primaryCodes, List<ResponseType> responsesPattern, List<ResponseType> responses){
        return IntStream.range(0, primaryCodes.size())
                .mapToObj(index -> IntStream.range(0, responsesPattern.size())
                        .mapToObj(codeListIndex -> createNewMapping(
                                responses.get(index+codeListIndex).getId(),
                                String.format(MAPPING_TARGET_FORMAT,index+1, codeListIndex+1))).toList())
                .flatMap(Collection::stream)
                .toList();
    }
}
