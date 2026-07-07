package fr.insee.pogues.service.modelcleaning.cleaners;

import fr.insee.pogues.model.*;
import fr.insee.pogues.service.modelcleaning.ModelCleaner;

import java.util.ArrayList;
import java.util.List;

/**
 * This cleaner ensures that each question (QCU/QCM) keeps only a single clarification question,
 * and removes related FlowControls that target removed clarifications, according to business rules.
 * As part of this process, any variables linked to the removed clarification questions are also deleted.
 */
public class ClarificationCleaner implements ModelCleaner {

    @Override
    public void apply(Questionnaire questionnaire) {
        List<String> removedVariableIds = new ArrayList<>();
        singleClarification(questionnaire, removedVariableIds);
        dropdownClarification(questionnaire, removedVariableIds);
        removeVariablesFromQuestionnaire(questionnaire, removedVariableIds);
    }

    private void singleClarification(ComponentType component, List<String> removedVariableIds) {
        if (component instanceof SequenceType sequence) {
            sequence.getChild().forEach(child -> singleClarification(child, removedVariableIds));
        }
        if (isSingleOrMultipleChoiceQuestion(component)) {
            assert component instanceof QuestionType;
            removedVariableIds.addAll(limitToSingleClarification((QuestionType) component));
        }
    }

    private void dropdownClarification(ComponentType component, List<String> removedVariableIds) {
        if (component instanceof SequenceType sequence) {
            sequence.getChild().forEach(child -> dropdownClarification(child, removedVariableIds));
        }
        if (isDropdownSingleChoiceQuestion(component)) {
            assert component instanceof QuestionType;
            removedVariableIds.addAll(removeAllClarifications((QuestionType) component));
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
     * Checks whether the given component is a SINGLE_CHOICE question rendered as a dropdown.
     * 
     * @param component the component to check
     * @return true if it is a single choice question with a dropdown response, false otherwise
     */
    private boolean isDropdownSingleChoiceQuestion(ComponentType component) {
        return component instanceof QuestionType question &&
                QuestionTypeEnum.SINGLE_CHOICE.equals(question.getQuestionType()) &&
                VisualizationHintEnum.DROPDOWN.equals(question.getResponse().getFirst().getDatatype().getVisualizationHint());
    }

    /**
     * Limits the given question to only one clarification question.
     * Retains only the first clarification, removes FlowControls to removed ones,
     * and clears variable references in the removed clarifications.
     * It returns the list of variable IDs that were associated with the removed clarification
     * questions, so they can be deleted from the questionnaire at a later stage.
     *
     * @param question the question to clean
     * @return a list of variable IDs to be removed from the questionnaire
     */
    private static List<String> limitToSingleClarification(QuestionType question) {
        List<QuestionType> clarifications = question.getClarificationQuestion();
        if (clarifications == null || clarifications.size() <= 1) {
            return List.of();
        }

        QuestionType firstClarification = clarifications.getFirst();
        List<QuestionType> toRemove = new ArrayList<>(clarifications.subList(1, clarifications.size()));

        retainOnlyFirstClarification(question, firstClarification);
        removeClarificationFlowControls(question, firstClarification);
        return clearClarificationResponses(toRemove);
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
     * Removes all clarification questions from the given question, removes the corresponding FlowControls,
     * and clears variable references in the removed clarifications.
     * The IDs of the removed variables are added to {@code removedVariableIds} so they can be
     * deleted from the questionnaire at a later stage.
     *
     * @param question           the question to clean
     * @param removedVariableIds the list to accumulate variable IDs to be removed
     */
    private static List<String> removeAllClarifications(QuestionType question) {
        List<QuestionType> clarifications = question.getClarificationQuestion();
        if (clarifications == null || clarifications.isEmpty()) return List.of();
        List<String> removedVariableIds = clearClarificationResponses(new ArrayList<>(clarifications));
        question.getFlowControl().removeIf(flowControl -> FlowControlTypeEnum.CLARIFICATION.equals(flowControl.getFlowControlType()));
        clarifications.clear();
        return removedVariableIds;
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
