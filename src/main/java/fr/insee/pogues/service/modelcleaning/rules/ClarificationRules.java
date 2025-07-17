package fr.insee.pogues.service.modelcleaning.rules;

import fr.insee.pogues.model.FlowControlTypeEnum;
import fr.insee.pogues.model.QuestionType;
import fr.insee.pogues.model.ResponseType;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains domain rules related to clarification questions.
 */
public final class ClarificationRules {

    private ClarificationRules() {
    }

    /**
     * Limits the given question to only one clarification question.
     * If the question has more than one clarification question, this method retains only the first
     * clarification and removes all others. It also updates the flow controls of the question to
     * remove any references to the removed clarifications. Finally, it clears the collected variable
     * references in the responses of the removed clarification questions to avoid dangling references.
     *
     * @param question the QuestionType object to be cleaned, expected to have a list of clarification questions.
     */
    public static void limitToSingleClarification(QuestionType question) {
        List<QuestionType> clarifications = question.getClarificationQuestion();

        if (clarifications == null || clarifications.size() <= 1) {
            return;
        }
        QuestionType firstClarification = clarifications.getFirst();
        List<QuestionType> toRemove = new ArrayList<>(clarifications.subList(1, clarifications.size()));
        clarifications.clear();
        clarifications.add(firstClarification);
        question.getFlowControl().removeIf(fc ->
                FlowControlTypeEnum.CLARIFICATION.equals(fc.getFlowControlType()) &&
                        !firstClarification.getId().equals(fc.getIfTrue())
        );
        for (QuestionType clarification : toRemove) {
            for (ResponseType response : clarification.getResponse()) {
                response.setCollectedVariableReference(null);
            }
        }
    }
}
