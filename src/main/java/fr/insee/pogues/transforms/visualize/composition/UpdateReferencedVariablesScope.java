package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.exception.IllegalIterationException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.utils.model.PoguesModelUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Stream;

import static fr.insee.pogues.utils.model.PoguesModelUtils.getIterationBounds;

/**
 * Implementation of CompositionStep to update variable scopes when de-referencing a questionnaire.
 */
@Slf4j
class UpdateReferencedVariablesScope implements CompositionStep {

    /**
     * If the referenced questionnaire is in an iteration (loop) in referencing questionnaire,
     * variables in referenced questionnaire that have null scope have to be updated.
     * Warning: This must be done BEFORE replacing referenced questionnaire by its content.
     * (Otherwise, we would have to scan every iteration and determine the variables in their scope,
     * which would be much more complex.)
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     * @throws DeReferencingException if an error occurs during variable scopes update.
     */
    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws DeReferencingException {
        try {
            updateReferencedVariablesScope(questionnaire, referencedQuestionnaire);
        } catch (IllegalIterationException e) {
            String message = String.format(
                    "Error when updating referenced variables scope in questionnaire '%s' with reference '%s'",
                    questionnaire.getId(), referencedQuestionnaire.getId());
            throw new DeReferencingException(message, e);
        }
    }

    /**
     * Gather iterations defined in the questionnaire (e.g. in loops and roundabouts).
     * @param questionnaire A Pogues questionnaire object.
     * @return List of iteration objects.
     */
    static List<IterationType> gatherQuestionnaireIterations(Questionnaire questionnaire) {
        // Roundabouts
        // Note: a questionnaire reference cannot be inserted as a subsequence.
        // Thus, there is no need to fetch roundabouts in "depth 2".
        Stream<IterationType> roundaboutIterations = questionnaire.getChild().stream()
                .filter(RoundaboutType.class::isInstance).map(RoundaboutType.class::cast)
                .map(RoundaboutType::getLoop);
        // Loops
        if (questionnaire.getIterations() == null)
            return roundaboutIterations.toList();
        Stream<IterationType> iterations = questionnaire.getIterations().getIteration().stream();
        return Stream.concat(iterations, roundaboutIterations).toList();
    }

    /**
     * Iterate on iterations of the referencing questionnaire.
     * For each iteration: update variables scope if the referenced questionnaire is in the scope of the iteration.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     * @throws IllegalIterationException If the 'MemberReference' property is not of size 2
     * in one on the iteration object that has been scanned by the method.
     */
    static void updateReferencedVariablesScope(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws IllegalIterationException {
        for (IterationType iterationType : gatherQuestionnaireIterations(questionnaire)) {
            String scope = updateReferenceIfInBounds(questionnaire, referencedQuestionnaire, iterationType);
            if (scope != null) {
                log.debug("Scope of root variables from referenced questionnaire '{}' set to iteration scope '{}'",
                        referencedQuestionnaire.getId(), scope);
                break;
            }
        }
    }

    /**
     * Scan the referencing questionnaire to determine if the referenced questionnaire is in the scope of the
     * loop (Iteration) given. If so, update the scope of the referenced questionnaire's variables.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     * @param iterationType An iteration (loop) object.
     * @return null if the referenced questionnaire is not in the scope of the iteration given.
     * Otherwise, the identifier of the iteration, that has the referenced questionnaire in its scope.
     * @throws IllegalIterationException If the 'MemberReference' property is not of size 2
     * in one on the iteration object that has been scanned by the method.
     */
    private static String updateReferenceIfInBounds(Questionnaire questionnaire,
                                                    Questionnaire referencedQuestionnaire,
                                                    IterationType iterationType) throws IllegalIterationException {
        List<String> iterationBounds = getIterationBounds(iterationType);
        String beginMember = iterationBounds.get(0);
        String endMember = iterationBounds.get(1);
        //
        String result = null;
        boolean inScope = false;
        // Iterate on first level (sequences / questionnaire references)
        for (ComponentType component : questionnaire.getChild()) {
            // Declare following components as in the iteration's scope
            if (beginMember.equals(component.getId())) {
                inScope = true;
            }
            // If the referenced questionnaire is found...
            if (referencedQuestionnaire.getId().equals(component.getId())) {
                // If it is in iteration's scope, then update its variables
                if (inScope) {
                    result = iterationType.getId();
                    updateVariablesScope(referencedQuestionnaire, iterationType);
                }
                // We have found what we wanted for this iteration object, break
                break;
            }
            // If end of the iteration's scope is reached, break
            if (endMember.equals(component.getId())) {
                break;
            }
        }
        return result;
    }

    /**
     * Update the scope of variables that have a null scope in given questionnaire with iteration id given.
     * Note: variables that have a non-null scope are unchanged, since "loops of loops" are prohibited:
     * if a referenced questionnaire is in a loop in the referencing questionnaire, it is supposed that
     * the referenced questionnaire doesn't define new "main"/"standalone" loops.
     * @param referencedQuestionnaire Questionnaire object.
     * @param iterationType Iteration that will define the scope of null-scope variables.
     * @throws IllegalIterationException if the iteration object is not a DynamicIterationType.
     */
    private static void updateVariablesScope(Questionnaire referencedQuestionnaire, IterationType iterationType)
            throws IllegalIterationException {
        for (VariableType variableType : referencedQuestionnaire.getVariables().getVariable()) {
            if (variableType.getScope() == null) {
                // Rule: if it is a "main"/"standalone" loop, the scope is the loop id
                if (! PoguesModelUtils.isLinkedLoop(iterationType))
                    variableType.setScope(iterationType.getId());
                // if it is a linked loop, the scope is the id of the corresponding "main" loop
                else
                    variableType.setScope(PoguesModelUtils.getLinkedLoopReference(iterationType));
            }
        }
    }

}
