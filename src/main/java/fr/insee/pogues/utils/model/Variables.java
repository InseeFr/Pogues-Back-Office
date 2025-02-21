package fr.insee.pogues.utils.model;

import fr.insee.pogues.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Variables {

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
}
