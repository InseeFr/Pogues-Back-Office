package fr.insee.pogues.utils;

import fr.insee.pogues.exception.IllegalFlowControlException;
import fr.insee.pogues.exception.IllegalIterationException;
import fr.insee.pogues.model.*;

import java.util.List;
import java.util.stream.Collectors;

/** Helper class to factorize methods on Pogues-Model objects.
 * Some parts of the model should be revised to make this class obsolete. */
public class PoguesModelUtils {

    /** Name of the artificial end sequence added by the front (to manage some GoTo cases). */
    public static final String FAKE_LAST_ELEMENT_ID = "idendquest";

    private PoguesModelUtils() {}

    /**
     * Return the list of components of depth 1 (that are sequences and/or questionnaire references)
     * of the given questionnaire, without the fake last sequence component (filtered using its specific id).
     * @param questionnaire A Questionnaire object.
     * @return A list of sequences / questionnaire references, in the order defined in the questionnaire.
     */
    public static List<ComponentType> getSequences(Questionnaire questionnaire) {
        return questionnaire.getChild().stream()
                .filter(componentType -> !FAKE_LAST_ELEMENT_ID.equals(componentType.getId()))
                .collect(Collectors.toList());
    }

    /**
     * The 'IfTrue' property defines begin/end member references (separated with '-') of the filter.
     * @param flowControlType A FlowControl object.
     * @return A String array of size 2 containing begin/end member references of the filter.
     * @throws IllegalFlowControlException If the FlowControl 'IfTrue' property doesn't match the format "id-id".
     */
    public static String[] getFlowControlBounds(FlowControlType flowControlType) throws IllegalFlowControlException {
        if (flowControlType.getIfTrue() == null) {
            throw new IllegalFlowControlException(String.format(
                    "'IfTrue' property is null in FlowControl '%s'",
                    flowControlType.getId()));
        }
        String[] flowControlBounds = flowControlType.getIfTrue().split("-");
        if (flowControlBounds.length != 2) {
            throw new IllegalFlowControlException(String.format(
                    "'IfTrue' value '%s' is not compliant with Pogues-Model specification in FlowControl '%s'",
                    flowControlType.getIfTrue(), flowControlType.getId()));
        }
        return flowControlBounds;
    }

    /**
     * The 'MemberReference' property is a list containing begin/end member references of the loop.
     * @param iterationType An Iteration object.
     * @return A List of strings of size 2 containing begin/end member references of the iteration.
     * @throws IllegalIterationException If The 'MemberReference' property is not of size 2.
     */
    public static List<String> getIterationBounds(IterationType iterationType) throws IllegalIterationException {
        int size = iterationType.getMemberReference().size();
        if (size != 2) {
            throw new IllegalIterationException(String.format(
                    "'MemberReference' of iteration object '%s' contains %s references (should contain 2).",
                    iterationType.getId(), size));
        }
        return iterationType.getMemberReference();
    }

}
