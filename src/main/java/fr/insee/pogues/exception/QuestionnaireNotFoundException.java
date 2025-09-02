package fr.insee.pogues.exception;

import lombok.Getter;

import static fr.insee.pogues.controller.error.ErrorCode.QUESTIONNAIRE_NOT_FOUND;

/** Exception that is thrown when one tries to access a questionnaire which does not exist. */
@Getter
public class QuestionnaireNotFoundException extends PoguesException {

    public QuestionnaireNotFoundException(String message) {
        super(404, message, null, QUESTIONNAIRE_NOT_FOUND);
    }

}
