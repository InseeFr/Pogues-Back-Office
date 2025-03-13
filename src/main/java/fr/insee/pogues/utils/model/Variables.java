package fr.insee.pogues.utils.model;

import fr.insee.pogues.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;


public class Variables {

    // ${QUESTION_NAME}_${PRIMARY_INDEX}_${SECONDARY|MEASURE_INDEX}
    public static final String VARIABLE_FORMAT_TWO_AXIS = "%s_%d_%d";
    // ${PRIMARY_CODE_LABEL}_${SECONDARY|CODE_LABEL}
    public static final String COLLECTED_LABEL_FORMAT = "%s - %s";

    public static String getCleanedName(String dirtyName){
        return dirtyName
                .toUpperCase()
                .replaceAll("[^A-Z0-9a-z_]","");
    }

    public static VariableType buildBooleanVariableFromCode(CodeType codeType, String variableId, String name){
        CollectedVariableType collectedVariableType = new CollectedVariableType();
        collectedVariableType.setId(variableId);
        collectedVariableType.setName(getCleanedName(name));
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
        collectedVariableType.setName(getCleanedName(name));
        collectedVariableType.setLabel(label);
        return collectedVariableType;
    }

    public static List<String> getNeededCollectedVariablesInQuestionnaire(Questionnaire questionnaire){
        return questionnaire.getChild().stream()
                .map(componentType -> getNeededCollectedVariablesInQuestionnaire(componentType))
                .flatMap(Collection::stream).toList();
    }

    private static List<String> getNeededCollectedVariablesInQuestionnaire(ComponentType poguesComponent){
        List<String> variablesIds = new ArrayList<>();
        if(poguesComponent.getClass().equals(SequenceType.class)){
            ((SequenceType) poguesComponent).getChild().stream().forEach(childComponent ->
                variablesIds.addAll(getNeededCollectedVariablesInQuestionnaire(childComponent))
            );
        }
        if(poguesComponent.getClass().equals(QuestionType.class)){
            QuestionType question = (QuestionType) poguesComponent;
            // Retrieve collected variable in Responses
            question.getResponse().forEach(responseType -> {
                String collectedVariable = responseType.getCollectedVariableReference();
                if(collectedVariable != null) variablesIds.add(collectedVariable);
            });
            // Retrieve collected variable in ClarificationQuestion
            if(!question.getClarificationQuestion().isEmpty()){
                question.getClarificationQuestion().forEach(clarificationQuestion ->
                        variablesIds.addAll(getNeededCollectedVariablesInQuestionnaire(clarificationQuestion))
                );
            }
            // Retrieve collected variable in ArbitraryResponse
            if(question.getArbitraryResponse() != null){
                variablesIds.add(question.getArbitraryResponse().getCollectedVariableReference());
            }
        }
        return variablesIds;
    }

    public static <A> List<VariableType> buildVariablesBasedOnTwoDimensions(List<CodeType> codesList, List<A> secondList, List<ResponseType> responses, String questionName, Function<A,String> labelFactory){
        int responseIndex=0;
        List<VariableType> variables = new ArrayList<>();
        for(int primaryIndex=0; primaryIndex < codesList.size(); primaryIndex++){
            for(int secondaryIndex=0; secondaryIndex < secondList.size(); secondaryIndex++){
                variables.add(
                        buildCollectedVariableFromDataType(
                                responses.get(responseIndex).getDatatype(),
                                responses.get(responseIndex).getCollectedVariableReference(),
                                String.format(VARIABLE_FORMAT_TWO_AXIS, questionName, primaryIndex+1, secondaryIndex+1),
                                String.format(COLLECTED_LABEL_FORMAT,
                                        codesList.get(primaryIndex).getLabel(),
                                        labelFactory.apply(secondList.get(secondaryIndex))))
                );
                responseIndex++;
            }
        }
        return variables;
    }
}
