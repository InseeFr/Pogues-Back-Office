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

    /** Returns true if a "code list" multiple choice question with the mandatory property is found. */
    private boolean containsInvalidCase(List<ComponentType> components) {
        for (ComponentType component :  components) {
            boolean invalidCaseFound = false;
            if (component instanceof SequenceType sequenceType)
                invalidCaseFound = containsInvalidCase(sequenceType.getChild());
            if (!invalidCaseFound && isMandatoryCodeListMCQ(component))
                return true;
        }
        return false;
    }

    private boolean isMandatoryCodeListMCQ(ComponentType component) {
        if (! (component instanceof QuestionType question))
            return false;
        if (QuestionTypeEnum.MULTIPLE_CHOICE.equals(question.getQuestionType()) && question.isMandatory()) {
            invalidQuestion = question;
            return true;
        }
        return false;
    }

}
