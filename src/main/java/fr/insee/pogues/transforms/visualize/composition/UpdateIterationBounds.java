package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.exception.IllegalIterationException;
import fr.insee.pogues.model.ComponentType;
import fr.insee.pogues.model.IterationType;
import fr.insee.pogues.model.Questionnaire;

import java.util.List;

import static fr.insee.pogues.utils.PoguesModelUtils.getIterationBounds;
import static fr.insee.pogues.utils.PoguesModelUtils.getSequences;

public class UpdateIterationBounds implements CompositionStep {

    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws DeReferencingException {
        try {
            for (IterationType iterationType : questionnaire.getIterations().getIteration()) {
                updateIterationBounds(referencedQuestionnaire, iterationType);
            }
        } catch (IllegalIterationException e) {
            String message = String.format(
                    "Error when updating iteration bounds in questionnaire '%s' with reference '%s'",
                    questionnaire.getId(), referencedQuestionnaire.getId());
            throw new DeReferencingException(message, e);
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
