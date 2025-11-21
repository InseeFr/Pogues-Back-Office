package fr.insee.pogues.service.validation;

import fr.insee.pogues.model.Questionnaire;

public interface ValidationStep {

    /** Returns true if the questionnaire is valid for this step. */
    ValidationResult validate(Questionnaire questionnaire);

}
