package fr.insee.pogues.service.validation;

import fr.insee.pogues.model.*;

import java.util.List;
import java.util.Optional;

/**
 * Validation step that checks that the questionnaire doesn't contain "code list" multiple choice question
 * with the mandatory property (it is only allowed for "boolean" multiple choice question).
 */
public class MandatoryCodeListMCQCheck implements ValidationStep {

    @Override
    public ValidationResult validate(Questionnaire questionnaire) {
        Optional<QuestionType> invalidQuestion = lookForInvalidCase(questionnaire.getChild());

        if (invalidQuestion.isEmpty())
            return ValidationResult.valid();

        String errorMessage = String.format(
                "Les question QCM de type \"Liste de codes\" ne peuvent pas être obligatoires (question '%s').",
                invalidQuestion.get().getName());
        return ValidationResult.invalid(errorMessage);
    }

    /** Returns true if a "code list" multiple choice question with the mandatory property is found in the
     * questionnaire. */
    private Optional<QuestionType> lookForInvalidCase(List<ComponentType> components) {
        for (ComponentType component : components) {
            if (component instanceof SequenceType sequenceType)
                return lookForInvalidCase(sequenceType.getChild());
            if ((component instanceof QuestionType question) && isMandatoryCodeListMCQ(question))
                return Optional.of(question);
        }
        return Optional.empty();
    }

    private boolean isMandatoryCodeListMCQ(QuestionType question) {
        return isCodeListMCQ(question) && Boolean.TRUE.equals(question.isMandatory());
    }

    /** Checks if the question is a multiple choice question, and if its response type is 'code list'. */
    private static boolean isCodeListMCQ(QuestionType question) {
        if (! QuestionTypeEnum.MULTIPLE_CHOICE.equals(question.getQuestionType()))
            return false;
        if (question.getResponse().isEmpty()) // safety check, should not happen
            throw new IllegalStateException("Question " + question + " has no response.");
        // A 'code list' multiple choice question has a code list reference in its responses
        return question.getResponse().getFirst().getCodeListReference() != null;
    }

}
