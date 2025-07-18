package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.exception.IllegalFlowControlException;
import fr.insee.pogues.model.ComponentType;
import fr.insee.pogues.model.FlowControlType;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static fr.insee.pogues.utils.model.PoguesModelUtils.getFlowControlBounds;
import static fr.insee.pogues.utils.model.PoguesModelUtils.getSequences;

/**
 * Implementation of CompositionStep to update FlowControl (filters) objects when de-referencing a questionnaire.
 */
@Slf4j
class UpdateFlowControlBounds implements CompositionStep {

    /**
     * Update flow controls of the referencing questionnaire: if a start/end member of a flow control is a referenced
     * questionnaire, replace the reference id by the right element's id from the referenced questionnaire.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     * @throws DeReferencingException if an error occurs during flow controls update.
     */
    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws DeReferencingException {
        try {
            for (FlowControlType flowControlType : questionnaire.getFlowControl()) {
                updateFlowControlBounds(referencedQuestionnaire, flowControlType);
            }
            log.debug("Flow controls' bounds updated in '{}' when de-referencing '{}'",
                    questionnaire.getId(), referencedQuestionnaire.getId());
        } catch (IllegalFlowControlException e) {
            String message = String.format(
                    "Error when updating flow control bounds in questionnaire '%s' with reference '%s'",
                    questionnaire.getId(), referencedQuestionnaire.getId());
            throw new DeReferencingException(message, e);
        }

    }

    /** Replace filter bounds that are reference a questionnaire by its first or last sequence.
     * @param referencedQuestionnaire Referenced questionnaire.
     * @param flowControlType The FlowControl object to be updated.
     * @throws IllegalFlowControlException If the FlowControl 'IfTrue' property doesn't match the format "id-id".
     */
    private static void updateFlowControlBounds(Questionnaire referencedQuestionnaire, FlowControlType flowControlType)
            throws IllegalFlowControlException {
        //
        String reference = referencedQuestionnaire.getId();
        //
        String[] flowControlBounds = getFlowControlBounds(flowControlType);
        // Replace questionnaire reference by its first/last sequence
        String beginMember = flowControlBounds[0];
        String endMember = flowControlBounds[1];
        if (beginMember.equals(reference)) {
            beginMember = referencedQuestionnaire.getChild().getFirst().getId();
        }
        if (endMember.equals(reference)) {
            List<ComponentType> referenceSequences = getSequences(referencedQuestionnaire);
            endMember = referenceSequences.getLast().getId();
        }
        flowControlType.setIfTrue(beginMember+"-"+endMember);
    }

}
