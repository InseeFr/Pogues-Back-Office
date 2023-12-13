package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UpdateReferencedVariablesScopeTest {

    @Test
    void noLoop_nullScopesShouldBeUnchanged() throws DeReferencingException {
        //
        Questionnaire questionnaire = new Questionnaire();
        //
        Questionnaire referencedQuestionnaire = new Questionnaire();
        CollectedVariableType collectedVariableType = new CollectedVariableType();
        CalculatedVariableType calculatedVariableType = new CalculatedVariableType();
        ExternalVariableType externalVariableType = new ExternalVariableType();
        referencedQuestionnaire.setVariables(new Questionnaire.Variables());
        referencedQuestionnaire.getVariables().getVariable().add(collectedVariableType);
        referencedQuestionnaire.getVariables().getVariable().add(calculatedVariableType);
        referencedQuestionnaire.getVariables().getVariable().add(externalVariableType);

        //
        new UpdateReferencedVariablesScope().apply(questionnaire, referencedQuestionnaire);

        //
        assertEquals(3, referencedQuestionnaire.getVariables().getVariable().size());
        referencedQuestionnaire.getVariables().getVariable().forEach(variableType ->
                assertNull(variableType.getScope()));
    }

    @Test
    void noLoop_scopesShouldBeUnchanged() throws DeReferencingException {
        //
        Questionnaire questionnaire = new Questionnaire();
        //
        Questionnaire referencedQuestionnaire = new Questionnaire();
        CollectedVariableType collectedVariableType = new CollectedVariableType();
        collectedVariableType.setScope("foo-scope");
        CalculatedVariableType calculatedVariableType = new CalculatedVariableType();
        calculatedVariableType.setScope("foo-scope");
        ExternalVariableType externalVariableType = new ExternalVariableType();
        externalVariableType.setScope("foo-scope");
        referencedQuestionnaire.setVariables(new Questionnaire.Variables());
        referencedQuestionnaire.getVariables().getVariable().add(collectedVariableType);
        referencedQuestionnaire.getVariables().getVariable().add(calculatedVariableType);
        referencedQuestionnaire.getVariables().getVariable().add(externalVariableType);

        //
        new UpdateReferencedVariablesScope().apply(questionnaire, referencedQuestionnaire);

        //
        assertEquals(3, referencedQuestionnaire.getVariables().getVariable().size());
        referencedQuestionnaire.getVariables().getVariable().forEach(variableType ->
                assertEquals("foo-scope", variableType.getScope()));
    }

    @Test
    void loopInReferencing_scopesShouldBeUpdated() throws DeReferencingException {
        //
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setIterations(new Questionnaire.Iterations());
        DynamicIterationType iterationType = new DynamicIterationType();
        iterationType.setId("iteration-id");
        iterationType.getMemberReference().add("seq1");
        iterationType.getMemberReference().add("seq3");
        questionnaire.getIterations().getIteration().add(iterationType);
        SequenceType sequenceType1 = new SequenceType();
        sequenceType1.setId("seq1");
        SequenceType sequenceType2 = new SequenceType();
        sequenceType2.setId("ref-id");
        SequenceType sequenceType3= new SequenceType();
        sequenceType3.setId("seq3");
        questionnaire.getChild().add(sequenceType1);
        questionnaire.getChild().add(sequenceType2);
        questionnaire.getChild().add(sequenceType3);
        //
        Questionnaire referencedQuestionnaire = new Questionnaire();
        referencedQuestionnaire.setId("ref-id");
        CollectedVariableType collectedVariableType = new CollectedVariableType();
        CalculatedVariableType calculatedVariableType = new CalculatedVariableType();
        ExternalVariableType externalVariableType = new ExternalVariableType();
        referencedQuestionnaire.setVariables(new Questionnaire.Variables());
        referencedQuestionnaire.getVariables().getVariable().add(collectedVariableType);
        referencedQuestionnaire.getVariables().getVariable().add(calculatedVariableType);
        referencedQuestionnaire.getVariables().getVariable().add(externalVariableType);

        //
        new UpdateReferencedVariablesScope().apply(questionnaire, referencedQuestionnaire);

        //
        referencedQuestionnaire.getVariables().getVariable().forEach(variableType ->
                assertEquals("iteration-id", variableType.getScope()));
    }

    @Test
    void linkedLoopInReferencing_scopeShouldBeEqualToMainLoopId() throws DeReferencingException {
        //
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setIterations(new Questionnaire.Iterations());
        DynamicIterationType iterationType = new DynamicIterationType();
        iterationType.setId("linked-loop-id");
        iterationType.setIterableReference("main-loop-id");
        iterationType.getMemberReference().add("seq1");
        iterationType.getMemberReference().add("seq3");
        questionnaire.getIterations().getIteration().add(iterationType);
        SequenceType sequenceType1 = new SequenceType();
        sequenceType1.setId("seq1");
        SequenceType sequenceType2 = new SequenceType();
        sequenceType2.setId("ref-id");
        SequenceType sequenceType3= new SequenceType();
        sequenceType3.setId("seq3");
        questionnaire.getChild().add(sequenceType1);
        questionnaire.getChild().add(sequenceType2);
        questionnaire.getChild().add(sequenceType3);
        //
        Questionnaire referencedQuestionnaire = new Questionnaire();
        referencedQuestionnaire.setId("ref-id");
        CollectedVariableType collectedVariableType = new CollectedVariableType();
        CalculatedVariableType calculatedVariableType = new CalculatedVariableType();
        ExternalVariableType externalVariableType = new ExternalVariableType();
        referencedQuestionnaire.setVariables(new Questionnaire.Variables());
        referencedQuestionnaire.getVariables().getVariable().add(collectedVariableType);
        referencedQuestionnaire.getVariables().getVariable().add(calculatedVariableType);
        referencedQuestionnaire.getVariables().getVariable().add(externalVariableType);

        //
        new UpdateReferencedVariablesScope().apply(questionnaire, referencedQuestionnaire);

        //
        referencedQuestionnaire.getVariables().getVariable().forEach(variableType ->
                assertEquals("main-loop-id", variableType.getScope()));
    }

}
