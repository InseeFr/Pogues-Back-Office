package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.exception.IllegalIterationException;
import fr.insee.pogues.model.ComponentType;
import fr.insee.pogues.model.IterationType;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static fr.insee.pogues.utils.PoguesModelUtils.getIterationBounds;
import static fr.insee.pogues.utils.PoguesModelUtils.getSequences;

/**
 * Implementation of CompositionStep to update Iteration (loops) objects when de-referencing a questionnaire.
 */
@Slf4j
class UpdateIterationBounds implements CompositionStep {

    /**
     * Update iterations of the referencing questionnaire: if a start/end member of an iteration is a referenced
     * questionnaire, replace the reference id by the right element's id from the referenced questionnaire.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     * @throws DeReferencingException if an error occurs during iterations update.
     */
    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws DeReferencingException {
        if (questionnaire.getIterations() != null) {
            try {
                for (IterationType iterationType : questionnaire.getIterations().getIteration()) {
                    updateIterationBounds(referencedQuestionnaire, iterationType);
                }
                log.debug("Iterations' bounds updated in '{}' when de-referencing '{}'",
                        questionnaire.getId(), referencedQuestionnaire.getId());
            } catch (IllegalIterationException e) {
                String message = String.format(
                        "Error when updating iteration bounds in questionnaire '%s' with reference '%s'",
                        questionnaire.getId(), referencedQuestionnaire.getId());
                throw new DeReferencingException(message, e);
            }
        }
    }

    /** Replace loop bounds that reference a questionnaire by its first or last sequence.
     * @param referencedQuestionnaire Referenced questionnaire.
     * @param iterationType The Iteration object to be updated.
     * @throws IllegalIterationException if the 'MemberReference' property in the iteration is invalid.
     */
    static void updateIterationBounds(Questionnaire referencedQuestionnaire, IterationType iterationType)
            throws IllegalIterationException {
        //
        String reference = referencedQuestionnaire.getId();
        //
        List<String> iterationBounds = getIterationBounds(iterationType);
        // Replace questionnaire reference by its first/last sequence
        String beginMember = iterationBounds.get(0);
        String endMember = iterationBounds.get(1);
        if (beginMember.equals(reference)) {
            iterationBounds.set(0, referencedQuestionnaire.getChild().get(0).getId());
        }
        if (endMember.equals(reference)) {
            List<ComponentType> referenceSequences = getSequences(referencedQuestionnaire);
            iterationBounds.set(1, referenceSequences.get(referenceSequences.size() - 1).getId());
        }
    }

}
