package fr.insee.pogues.exception;

import lombok.Getter;

import static fr.insee.pogues.controller.error.ErrorCode.VARIABLE_NOT_FOUND;

/** Exception that is thrown when one tries to access a variable which does not exist. */
@Getter
public class VariableNotFoundException extends PoguesException {

    public VariableNotFoundException(String message) {
       super(404, message, null, VARIABLE_NOT_FOUND);
    }

}
