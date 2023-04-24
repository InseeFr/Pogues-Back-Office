package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.exception.IllegalIterationException;
import fr.insee.pogues.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static fr.insee.pogues.utils.PoguesModelUtils.getIterationBounds;

@Slf4j
class UpdateReferencedVariablesScope implements CompositionStep {

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
     * If referenced questionnaire is in a loop in referencing questionnaire,
     * variables in referenced questionnaire that have null scope have to be updated.
     * Warning: This must be done BEFORE replacing referenced questionnaire by its content.
     * Otherwise, we would have to scan every iteration and determine the variables in their scope,
     * which would be much more complex.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     * @throws IllegalIterationException If the 'MemberReference' property is not of size 2
     * in one on the iteration object that has been scanned by the method.
     */
    static void updateReferencedVariablesScope(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws IllegalIterationException {
        for (IterationType iterationType : questionnaire.getIterations().getIteration()) {
            String scope = updateReferenceIfInBounds(questionnaire, referencedQuestionnaire, iterationType);
            if (scope != null) {
                log.info("Scope of root variables from referenced questionnaire '{}' set to iteration scope '{}'",
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
                    updateVariablesScope(referencedQuestionnaire, iterationType.getId());
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
     * Update the scope of variables that have a null scope in given questionnaire with iteration id given
     * (variables that have a non-null scope are unchanged, see model's documentation).
     * Only calculated and external variables are updated
     * (collected variables in Pogues' model should not have a scope, see model's documentation).
     * @param referencedQuestionnaire Questionnaire object.
     * @param iterationId Identifier of the iteration that will be the scope of null-scope variables.
     */
    private static void updateVariablesScope(Questionnaire referencedQuestionnaire, String iterationId) {
        referencedQuestionnaire.getVariables().getVariable().stream()
                .filter(variableType -> variableType instanceof CalculatedVariableType
                        || variableType instanceof ExternalVariableType)
                .forEach(variableType -> {
                    if (variableType.getScope() == null) {
                        variableType.setScope(iterationId);
                    }
                });
    }

}
