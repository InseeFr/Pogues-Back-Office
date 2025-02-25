package fr.insee.pogues.utils.model;

import fr.insee.pogues.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;


public class Variables {

    // ${QUESTION_NAME}_${PRIMARY_INDEX}_${SECONDARY|MEASURE_INDEX}
    public static final String VARIABLE_FORMAT_TWO_AXIS = "%s_%d_%d";
    // ${PRIMARY_CODE_LABEL}_${SECONDARY|CODE_LABEL}
    public static final String COLLECTED_LABEL_FORMAT = "%s - %s";

    public static VariableType buildBooleanVariableFromCode(CodeType codeType, String variableId, String name){
        CollectedVariableType collectedVariableType = new CollectedVariableType();
        collectedVariableType.setId(variableId);
        collectedVariableType.setName(name);
        collectedVariableType.setLabel(String.format("%s - %s",codeType.getValue(), codeType.getLabel()));
        DatatypeType booleanType = new BooleanDatatypeType();
        booleanType.setTypeName(DatatypeTypeEnum.BOOLEAN);
        collectedVariableType.setDatatype(booleanType);
        return collectedVariableType;
    }

    public static VariableType buildCollectedVariableFromDataType(DatatypeType datatype, String variableId, String name, String label){
        CollectedVariableType collectedVariableType = new CollectedVariableType();
        collectedVariableType.setDatatype(datatype);
        collectedVariableType.setId(variableId);
        collectedVariableType.setName(name);
        collectedVariableType.setLabel(label);
        return collectedVariableType;
    }

    public static List<String> getNeededCollectedVariablesInQuestionnaire(Questionnaire questionnaire){
        return questionnaire.getChild().stream()
                .map(componentType -> getNeededCollectedVariablesInQuestionnaire(componentType))
                .flatMap(Collection::stream).toList();
    }

    public static List<String> getNeededCollectedVariablesInQuestionnaire(ComponentType poguesComponent){
        List<String> variablesIds = new ArrayList<>();
        if(poguesComponent.getClass().equals(SequenceType.class)){
            ((SequenceType) poguesComponent).getChild().stream().forEach(childComponent -> {
                variablesIds.addAll(getNeededCollectedVariablesInQuestionnaire(childComponent));
            });
        }
        if(poguesComponent.getClass().equals(QuestionType.class)){
            ((QuestionType) poguesComponent).getResponse().forEach(responseType -> {
                String collectedVariable = responseType.getCollectedVariableReference();
                if(collectedVariable != null) variablesIds.add(collectedVariable);
            });
        }
        return variablesIds;
    }

    public static List<VariableType> buildVariablesAccordingWithTwoAxisAndResponses(List<CodeType> primaryCodes, List<CodeType> secondaryCodes, ResponseType responsePattern, List<ResponseType> responses, String questionName){
        return IntStream.range(0, primaryCodes.size())
                .mapToObj(codeListIndex -> IntStream.range(0, secondaryCodes.size())
                        .mapToObj(indexSecondary -> buildCollectedVariableFromDataType(
                                responsePattern.getDatatype(),
                                responses.get(codeListIndex + indexSecondary).getCollectedVariableReference(),
                                String.format(VARIABLE_FORMAT_TWO_AXIS, questionName, codeListIndex+1, indexSecondary+1),
                                String.format(COLLECTED_LABEL_FORMAT,
                                        primaryCodes.get(codeListIndex).getLabel(),
                                        secondaryCodes.get(indexSecondary).getLabel())))
                        .toList())
                .flatMap(Collection::stream)
                .toList();
    }

    public static List<VariableType> buildVariablesAccordingWithOneAxeAndResponses(List<CodeType> primaryCodes, List<ResponseType> responsesPattern, List<ResponseType> responses, String questionName, List<DimensionType> measures) {
       return IntStream.range(0, primaryCodes.size())
                .mapToObj(codeListIndex -> IntStream.range(0, responsesPattern.size())
                        .mapToObj(responsePatternIndex -> buildCollectedVariableFromDataType(
                                responses.get(responsePatternIndex+codeListIndex).getDatatype(),
                                responses.get(responsePatternIndex+codeListIndex).getCollectedVariableReference(),
                                String.format(VARIABLE_FORMAT_TWO_AXIS, questionName, codeListIndex+1, responsePatternIndex+1),
                                String.format(COLLECTED_LABEL_FORMAT,
                                        primaryCodes.get(codeListIndex).getLabel(),
                                        measures.get(responsePatternIndex).getLabel())))
                        .toList())
                .flatMap(Collection::stream)
                .toList();
    }
}
