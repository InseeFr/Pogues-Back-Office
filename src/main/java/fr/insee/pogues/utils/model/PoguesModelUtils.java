package fr.insee.pogues.utils.model;

import fr.insee.pogues.exception.IllegalFlowControlException;
import fr.insee.pogues.exception.IllegalIterationException;
import fr.insee.pogues.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/** Helper class to factorize methods on Pogues-Model objects.
 * Some parts of the model should be revised to make this class obsolete. */
@Slf4j
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
                .toList();
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
     * If the list contains only one reference, it means that begin and end members are the same.
     * A 'MemberReference' property with one element is accepted for now, yet a warning is shown in the log in that case
     * (Pogues UI will be updated so that this case should not exist).
     * @param iterationType An Iteration object.
     * @return A List of strings of size 2 containing begin/end member references of the iteration.
     * (The result will always be of size 2 even if begin and end members are equal.)
     * @throws IllegalIterationException If The 'MemberReference' property is not of size 1 or 2.
     */
    public static List<String> getIterationBounds(IterationType iterationType) throws IllegalIterationException {
        int size = iterationType.getMemberReference().size();
        if (!(size == 2 || size == 1)) {
            throw new IllegalIterationException(String.format(
                    "'MemberReference' of iteration object '%s' contains %s references (should contain 1 or 2).",
                    iterationType.getId(), size));
        }
        if (size == 1) {
            log.warn("'MemberReference' property with 1 element is deprecated (iteration object '{}').",
                    iterationType.getId());
            iterationType.getMemberReference().add(iterationType.getMemberReference().getFirst());
        }
        return iterationType.getMemberReference();
    }

    /**
     * Returns true if the given iteration corresponds to a linked loop.
     * @param iterationType A Pogues iteration (loop) object.
     * @return True if the given iteration corresponds to a linked loop.
     * @throws IllegalIterationException If the iteration object given is not a DynamicIterationType.
     */
    public static boolean isLinkedLoop(IterationType iterationType) throws IllegalIterationException {
        checkIterationInstance(iterationType);
        return ((DynamicIterationType) iterationType).getIterableReference() != null;
    }

    /**
     * The iteration reference of the given iteration.
     * If the iteration is a "main" loop, this is null.
     * If it is a linked loop, this is the identifier of the corresponding "main" loop.
     * @param iterationType A Pogues iteration (loop) object.
     * @return The iteration reference of the given iteration.
     * @throws IllegalIterationException If the iteration object given is not a DynamicIterationType.
     */
    public static String getLinkedLoopReference(IterationType iterationType) throws IllegalIterationException {
        checkIterationInstance(iterationType);
        return ((DynamicIterationType) iterationType).getIterableReference();
    }

    /**
     * Safety check due to a flaw in Pogues-Model: abstract class IterationType has only one inheritor that is
     * DynamicIterationType. Only the DynamicIterationType class has the "iteration reference" property...
     * Therefore, an iteration object has to be cast to get this property.
     * This method throws an exception if the cast fails, and explains why.
     * @param iterationType A Pogues iteration object.
     * @throws IllegalIterationException If the iteration object given is not a DynamicIterationType.
     */
    private static void checkIterationInstance(IterationType iterationType) throws IllegalIterationException {
        if (! (iterationType instanceof DynamicIterationType)) // (
            throw new IllegalIterationException(String.format(
                    "Pogues iteration with id=%s and name=%s is not is of type %s. " +
                            "Only DynamicIterationType is supported.",
                    iterationType.getId(), iterationType.getName(), iterationType.getClass().getSimpleName()));
    }

}
