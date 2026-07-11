package fr.insee.pogues.service.validation.steps;

import fr.insee.pogues.exception.validation.QuestionnaireIdentifierException;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.service.validation.ValidationResult;
import fr.insee.pogues.service.validation.ValidationStep;

public class IdentifierCheck implements ValidationStep {


    public static final String QUESTIONNAIRE_ID_PATTERN ="[a-zA-Z0-9]*";

    @Override
    public ValidationResult validate(Questionnaire questionnaire, String poguesId) {

        if (!poguesId.matches(QUESTIONNAIRE_ID_PATTERN))
            throw new QuestionnaireIdentifierException(poguesId);

        if(poguesId.equals(questionnaire.getId()))
            return ValidationResult.valid();
        String errorMessage = String.format(
                "L'identifiant du questionnaire '%s' ne peut pas être modifié avec la valeur '%s'",
                poguesId, questionnaire.getId());
        return ValidationResult.invalid(errorMessage);
    }

    @Override
    public ValidationResult validate(Questionnaire questionnaire) {
        // not use here
        return null;
    }
}
