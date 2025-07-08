package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.model.DynamicIterationType;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.RoundaboutType;
import fr.insee.pogues.model.SequenceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateIterationBoundsTest {

    @Test
    void roundaboutOnReferencedQuestionnaire() throws DeReferencingException {
        // Given
        Questionnaire questionnaire = new Questionnaire();
        SequenceType sequenceType1 = new SequenceType();
        RoundaboutType roundaboutType = new RoundaboutType();
        DynamicIterationType iterationType = new DynamicIterationType();
        iterationType.getMemberReference().add("ref-id");
        iterationType.getMemberReference().add("ref-id");
        roundaboutType.setLoop(iterationType);
        SequenceType reference = new SequenceType();
        reference.setId("ref-id");
        questionnaire.getChild().add(sequenceType1);
        questionnaire.getChild().add(roundaboutType);
        questionnaire.getChild().add(reference);

        Questionnaire referenceQuestionnaire = new Questionnaire();
        referenceQuestionnaire.setId("ref-id");
        SequenceType firstSequence = new SequenceType();
        firstSequence.setId("ref-first-seq-id");
        SequenceType lastSequence = new SequenceType();
        lastSequence.setId("ref-last-seq-id");
        referenceQuestionnaire.getChild().add(firstSequence);
        referenceQuestionnaire.getChild().add(lastSequence);

        // When
        new UpdateIterationBounds().apply(questionnaire, referenceQuestionnaire);

        // Then
        assertEquals("ref-first-seq-id", iterationType.getMemberReference().getFirst());
        assertEquals("ref-last-seq-id", iterationType.getMemberReference().getLast());
    }

}
