package fr.insee.pogues.service.validation;

import fr.insee.pogues.model.*;

import java.util.List;

/**
 * Validation step that checks that the questionnaire doesn't contain "code list" multiple choice question
 * with the mandatory property.
 */
public class MandatoryCodeListMCQCheck implements ValidationStep {

    /** Local variable to store the invalid question that might be found. */
    private QuestionType invalidQuestion;

    @Override
    public String errorMessage() {
        if (invalidQuestion == null)
            throw new IllegalStateException("No invalid question found.");
        return "Les question QCM de type \"Liste de codes\" ne peuvent pas être obligatoires (question '%s')."
                .formatted(invalidQuestion.getName());
    }

    @Override
    public boolean validate(Questionnaire questionnaire) {
        return !containsInvalidCase(questionnaire.getChild());
    }

    /** Returns true if a "code list" multiple choice question with the mandatory property is found in the
     * questionnaire. */
    private boolean containsInvalidCase(List<ComponentType> components) {
        for (ComponentType component : components) {
            if (component instanceof SequenceType sequenceType && containsInvalidCase(sequenceType.getChild()))
                return true;
            if (isMandatoryCodeListMCQ(component))
                return true;
        }
        return false;
    }

    private boolean isMandatoryCodeListMCQ(ComponentType component) {
        if (! (component instanceof QuestionType question))
            return false;
        if (isCodeListMCQ(question) && Boolean.TRUE.equals(question.isMandatory())) {
            invalidQuestion = question;
            return true;
        }
        return false;
    }

    private static boolean isCodeListMCQ(QuestionType question) {
        if (! QuestionTypeEnum.MULTIPLE_CHOICE.equals(question.getQuestionType()))
            return false;
        if (question.getResponse().isEmpty()) // safety check, should not happen
            throw new IllegalStateException("Question " + question + " has no response.");
        // A 'code list' multiple choice question has a code list reference in its responses
        return question.getResponse().getFirst().getCodeListReference() != null;
    }


}
