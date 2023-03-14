package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.IllegalFlowControlException;
import fr.insee.pogues.model.ComponentType;
import fr.insee.pogues.model.FlowControlType;
import fr.insee.pogues.model.Questionnaire;

import java.util.List;

import static fr.insee.pogues.utils.PoguesModelUtils.getFlowControlBounds;
import static fr.insee.pogues.utils.PoguesModelUtils.getSequences;

/**
 * Methods to insert and update FlowControl (filters) objects when de-referencing a questionnaire.
 */
class FilterComposition {

    private FilterComposition() {}

    /** Replace filter bounds that are reference a questionnaire by its first or last sequence.
     * @param referencedQuestionnaire Referenced questionnaire.
     * @param flowControlType The FlowControl object to be updated.
     * @throws IllegalFlowControlException If the FlowControl 'IfTrue' property doesn't match the format "id-id".
     */
    static void updateFlowControlBounds(Questionnaire referencedQuestionnaire, FlowControlType flowControlType)
            throws IllegalFlowControlException {
        //
        String reference = referencedQuestionnaire.getId();
        //
        String[] flowControlBounds = getFlowControlBounds(flowControlType);
        // Replace questionnaire reference by its first/last sequence
        String beginMember = flowControlBounds[0];
        String endMember = flowControlBounds[1];
        if (beginMember.equals(reference)) {
            beginMember = referencedQuestionnaire.getChild().get(0).getId();
        }
        if (endMember.equals(reference)) {
            List<ComponentType> referenceSequences = getSequences(referencedQuestionnaire);
            endMember = referenceSequences.get(referenceSequences.size() - 1).getId();
        }
        flowControlType.setIfTrue(beginMember+"-"+endMember);
    }

}
