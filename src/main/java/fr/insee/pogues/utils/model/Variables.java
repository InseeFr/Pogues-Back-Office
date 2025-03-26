package fr.insee.pogues.utils.model;

import fr.insee.pogues.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;


public class Variables {

    private Variables(){
        throw new IllegalStateException("Utility class");
    }

    /**
     * This format is filled like that ${QUESTION_NAME}${PRIMARY_INDEX}${SECONDARY|MEASURE_INDEX}
     */
    public static final String VARIABLE_FORMAT_TWO_AXIS = "%s%d%d";
    /**
     * This format is filled like that ${QUESTION_NAME}${INDEX}
     */
    public static final String VARIABLE_FORMAT_MULTIPLE_CHOICE = "%s%d";
    /**
     * This format is filled like that ${PRIMARY_CODE_LABEL}_${SECONDARY|CODE_LABEL}
     */
    public static final String COLLECTED_LABEL_FORMAT = "%s - %s";

    public static String getCleanedName(String dirtyName){
        return dirtyName
                .toUpperCase()
                .replaceAll("\\W","");
    }

    public static VariableType buildCollectedVariableFromDataType(DatatypeType datatype, String variableId, String name, String label, String codeListRef){
        CollectedVariableType collectedVariableType = new CollectedVariableType();
        collectedVariableType.setDatatype(datatype);
        collectedVariableType.setId(variableId);
        collectedVariableType.setName(getCleanedName(name));
        collectedVariableType.setLabel(label);
        collectedVariableType.setCodeListReference(codeListRef);
        return collectedVariableType;
    }

    public static List<String> getNeededCollectedVariablesInQuestionnaire(Questionnaire questionnaire){
        return questionnaire.getChild().stream()
                .map(Variables::getNeededCollectedVariablesInQuestionnaire)
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
                                        labelFactory.apply(secondList.get(secondaryIndex))),
                                responses.get(responseIndex).getCodeListReference())
                );
                responseIndex++;
            }
        }
        return variables;
    }
}
