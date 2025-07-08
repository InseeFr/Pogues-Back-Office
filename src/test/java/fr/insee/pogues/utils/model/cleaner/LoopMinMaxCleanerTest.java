package fr.insee.pogues.utils.model.cleaner;

import fr.insee.pogues.model.DynamicIterationType;
import fr.insee.pogues.model.ExpressionType;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

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
        loop.setIsFixedLength(false);
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
        loop.setIsFixedLength(true);
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

    @Test
    void oldAndNewProperties_minMaxCase() {
        // Given
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setIterations(new Questionnaire.Iterations());
        DynamicIterationType loop = new DynamicIterationType();
        loop.setDeprecatedMinimum(new ExpressionType());
        loop.getDeprecatedMinimum().setValue("1");
        loop.setDeprecatedMaximum(new ExpressionType());
        loop.getDeprecatedMaximum().setValue("5");
        loop.setMinimum(new ExpressionType());
        loop.getMinimum().setValue("1");
        loop.setMaximum(new ExpressionType());
        loop.getMaximum().setValue("5");
        loop.setIsFixedLength(false);
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
    void oldAndNewProperties_sizeCase() {
        // Given
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setIterations(new Questionnaire.Iterations());
        DynamicIterationType loop = new DynamicIterationType();
        loop.setDeprecatedMinimum(new ExpressionType());
        loop.getDeprecatedMinimum().setValue("5");
        loop.setDeprecatedMaximum(new ExpressionType());
        loop.getDeprecatedMaximum().setValue("5");
        loop.setSize(new ExpressionType());
        loop.getSize().setValue("5");
        loop.setIsFixedLength(true);
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

    @ParameterizedTest
    @NullSource
    @ValueSource(booleans = {false})
    void allProps_withoutFixedLength(Boolean fixedLengthProp) {
        // Given
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setIterations(new Questionnaire.Iterations());
        DynamicIterationType loop = new DynamicIterationType();
        loop.setDeprecatedMinimum(new ExpressionType());
        loop.getDeprecatedMinimum().setValue("1");
        loop.setDeprecatedMaximum(new ExpressionType());
        loop.getDeprecatedMaximum().setValue("5");
        loop.setMinimum(new ExpressionType());
        loop.getMinimum().setValue("1");
        loop.setMaximum(new ExpressionType());
        loop.getMaximum().setValue("5");
        loop.setSize(new ExpressionType());
        loop.getSize().setValue("5");
        loop.setIsFixedLength(fixedLengthProp);
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
    void allProps_withFixedLength() {
        // Given
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setIterations(new Questionnaire.Iterations());
        DynamicIterationType loop = new DynamicIterationType();
        loop.setDeprecatedMinimum(new ExpressionType());
        loop.getDeprecatedMinimum().setValue("5");
        loop.setDeprecatedMaximum(new ExpressionType());
        loop.getDeprecatedMaximum().setValue("5");
        loop.setMinimum(new ExpressionType());
        loop.getMinimum().setValue("5");
        loop.setMaximum(new ExpressionType());
        loop.getMaximum().setValue("5");
        loop.setSize(new ExpressionType());
        loop.getSize().setValue("5");
        loop.setIsFixedLength(true);
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
