package fr.insee.pogues.utils.model.cleaner;

import fr.insee.pogues.model.DynamicIterationType;
import fr.insee.pogues.model.ExpressionType;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoopMinMaxCleanerTest {

    @Test
    void loopMinMaxCleaningTest() {
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
        assertNull(loop.getDeprecatedMinimum());
        assertNull(loop.getDeprecatedMaximum());
    }

}
