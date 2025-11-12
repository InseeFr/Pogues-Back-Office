package fr.insee.pogues.service.validation;

import fr.insee.pogues.model.Questionnaire;

public interface ValidationStep {

    /** Returns true if the questionnaire is valid for this step. */
    boolean validate(Questionnaire questionnaire);

    /** Error message to be sent if the validation fails. */
    String errorMessage();

}
