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
        singleClarification(questionnaire, questionnaire);
    }

    private void singleClarification(ComponentType poguesComponent, Questionnaire questionnaire) {
        if (isSingleOrMultipleChoiceQuestion(poguesComponent)) {
            limitToSingleClarification((QuestionType) poguesComponent, questionnaire);
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
     * @param questionnaire the questionnaire containing the variables and flow controls related to the question
     */
    static void limitToSingleClarification(QuestionType question, Questionnaire questionnaire) {
        List<QuestionType> clarifications = question.getClarificationQuestion();
        if (clarifications == null || clarifications.size() <= 1) return;

        QuestionType firstClarification = clarifications.getFirst();
        List<QuestionType> toRemove = new ArrayList<>(clarifications.subList(1, clarifications.size()));

        retainOnlyFirstClarification(question, firstClarification);
        removeClarificationFlowControls(question, firstClarification);
        List<String> removedVariableIds = clearClarificationResponses(toRemove);
        removeVariablesFromQuestionnaire(questionnaire, removedVariableIds);
    }

    /**
     * Keeps only the first clarification in the question.
     */
    private static void retainOnlyFirstClarification(QuestionType question, QuestionType firstClarification) {
        List<QuestionType> clarifications = question.getClarificationQuestion();
        clarifications.clear();
        clarifications.add(firstClarification);
    }

    /**
     * Removes flow controls pointing to clarifications other than the retained one.
     */
    private static void removeClarificationFlowControls(QuestionType question, QuestionType retainedClarification) {
        question.getFlowControl().removeIf(flowControl ->
                FlowControlTypeEnum.CLARIFICATION.equals(flowControl.getFlowControlType()) &&
                        !retainedClarification.getId().equals(flowControl.getIfTrue())
        );
    }

    /**
     * Clears the collected variable references from removed clarifications and returns the removed variable IDs.
     */
    private static List<String> clearClarificationResponses(List<QuestionType> toRemove) {
        List<String> removedVariableIds = new ArrayList<>();
        for (QuestionType clarification : toRemove) {
            for (ResponseType response : clarification.getResponse()) {
                String varRef = response.getCollectedVariableReference();
                if (varRef != null) {
                    removedVariableIds.add(varRef);
                    response.setCollectedVariableReference(null);
                }
            }
        }
        return removedVariableIds;
    }

    /**
     * Removes the given variable IDs from the questionnaire’s variable list.
     */
    private static void removeVariablesFromQuestionnaire(Questionnaire questionnaire, List<String> removedVariableIds) {
        if (questionnaire.getVariables() != null && questionnaire.getVariables().getVariable() != null) {
            questionnaire.getVariables().getVariable().removeIf(variable ->
                    removedVariableIds.contains(variable.getId())
            );
        }
    }
}
