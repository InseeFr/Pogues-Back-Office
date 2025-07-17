package fr.insee.pogues.service.modelcleaning.rules;

import fr.insee.pogues.model.ComponentType;
import fr.insee.pogues.model.FlowControlTypeEnum;
import fr.insee.pogues.model.QuestionType;
import fr.insee.pogues.model.QuestionTypeEnum;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.ResponseType;
import fr.insee.pogues.utils.model.cleaner.ModelCleaner;

import java.util.ArrayList;
import java.util.List;

/**
 * This cleaner ensures that each question (QCU/QCM) keeps only a single clarification question,
 * and removes related FlowControls that target removed clarifications (business rule).
 */
public class ClarificationCleaner implements ModelCleaner {

    @Override
    public void apply(Questionnaire questionnaire) {
        singleClarification(questionnaire);
    }

    private void singleClarification(ComponentType poguesComponent) {
        if (isSingleOrMultipleChoiceQuestion(poguesComponent)) {
            limitToSingleClarification((QuestionType) poguesComponent);
        }
    }

    /**
     * Checks whether the given component is a question of type SINGLE_CHOICE or MULTIPLE_CHOICE.
     *
     * @param component the component to check
     * @return true if it is a single or multiple choice question, false otherwise
     */
    private boolean isSingleOrMultipleChoiceQuestion(ComponentType component) {
        return component instanceof QuestionType question &&
                (QuestionTypeEnum.SINGLE_CHOICE.equals(question.getQuestionType())
                        || QuestionTypeEnum.MULTIPLE_CHOICE.equals(question.getQuestionType()));
    }

    /**
     * Limits the given question to only one clarification question.
     * Retains only the first clarification, removes FlowControls to removed ones,
     * and clears variable references in the removed clarifications.
     *
     * @param question the question to clean
     */
    static void limitToSingleClarification(QuestionType question) {
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
