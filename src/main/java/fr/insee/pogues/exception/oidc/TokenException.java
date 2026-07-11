package fr.insee.pogues.exception.oidc;

import fr.insee.pogues.exception.PoguesException;

import static fr.insee.pogues.controller.error.ErrorCode.TOKEN_ERROR;

/** Exception that is thrown when token can not be retrieved with the service account */
public class TokenException extends PoguesException {

    public TokenException(String message) {
        super(403, message, null, TOKEN_ERROR);
    }

}
