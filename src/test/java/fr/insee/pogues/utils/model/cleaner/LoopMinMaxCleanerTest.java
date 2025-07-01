package fr.insee.pogues.utils.model.cleaner;

import fr.insee.pogues.model.DynamicIterationType;
import fr.insee.pogues.model.ExpressionType;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoopMinMaxCleanerTest {

    @Test
    void oldProperties() {
        // Given
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setIterations(new Questionnaire.Iterations());
        DynamicIterationType loop = new DynamicIterationType();
        loop.setDeprecatedMinimum(new ExpressionType());
        loop.getDeprecatedMinimum().setValue("1");
        loop.setDeprecatedMaximum(new ExpressionType());
        loop.getDeprecatedMaximum().setValue("5");
        questionnaire.getIterations().getIteration().add(loop);

        // When
        new LoopMinMaxCleaner().apply(questionnaire);

        // Then
        assertEquals("1", loop.getMinimum().getValue());
        assertEquals("5", loop.getMaximum().getValue());
        assertNull(loop.getSize());
        assertEquals("1", loop.getDeprecatedMinimum().getValue());
        assertEquals("5", loop.getDeprecatedMaximum().getValue());
    }

    @Test
    void newProperties_minMaxCase() {
        // Given
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setIterations(new Questionnaire.Iterations());
        DynamicIterationType loop = new DynamicIterationType();
        loop.setMinimum(new ExpressionType());
        loop.getMinimum().setValue("1");
        loop.setMaximum(new ExpressionType());
        loop.getMaximum().setValue("5");
        questionnaire.getIterations().getIteration().add(loop);

        // When
        new LoopMinMaxCleaner().apply(questionnaire);

        // Then
        assertEquals("1", loop.getMinimum().getValue());
        assertEquals("5", loop.getMaximum().getValue());
        assertNull(loop.getSize());
        assertEquals("1", loop.getDeprecatedMinimum().getValue());
        assertEquals("5", loop.getDeprecatedMaximum().getValue());
    }

    @Test
    void newProperties_sizeCase() {
        // Given
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setIterations(new Questionnaire.Iterations());
        DynamicIterationType loop = new DynamicIterationType();
        loop.setSize(new ExpressionType());
        loop.getSize().setValue("5");
        questionnaire.getIterations().getIteration().add(loop);

        // When
        new LoopMinMaxCleaner().apply(questionnaire);

        // Then
        assertNull(loop.getMinimum());
        assertNull(loop.getMaximum());
        assertEquals("5", loop.getSize().getValue());
        assertEquals("5", loop.getDeprecatedMinimum().getValue());
        assertEquals("5", loop.getDeprecatedMaximum().getValue());
    }

}
