package fr.insee.pogues.exception;

import lombok.Getter;

import static fr.insee.pogues.webservice.error.ErrorCode.VARIABLE_INVALID_MODEL;

/** Exception that is thrown when one tries to upsert a variable which has an invalid model. */
@Getter
public class VariableInvalidModelException extends PoguesException {

    public VariableInvalidModelException(String message, String details) {
       super(400, message, details, VARIABLE_INVALID_MODEL);
    }

}
