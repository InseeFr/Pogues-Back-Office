package fr.insee.pogues.exception;

import lombok.Getter;

import static fr.insee.pogues.controller.error.ErrorCode.QUESTIONNAIRE_ROUNDABOUT_NOT_FOUND;

/**
 * Exception that is thrown when one tries to use a feature only available to questionnaire which has a roundabout but
 * the questionnaire has none.
 */
@Getter
public class QuestionnaireRoundaboutNotFoundException extends PoguesException {

    public QuestionnaireRoundaboutNotFoundException(String message) {
        super(422, message, null, QUESTIONNAIRE_ROUNDABOUT_NOT_FOUND);
    }

}
