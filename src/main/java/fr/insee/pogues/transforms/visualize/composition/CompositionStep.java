package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.model.Questionnaire;

/**
 * Interface for processing step when de-referencing a questionnaire.
 */
public interface CompositionStep {

    /**
     * Update questionnaire content with referenced questionnaire given.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     */
    void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire) throws DeReferencingException;

}
