package fr.insee.pogues.exception;

import lombok.Getter;

import static fr.insee.pogues.controller.error.ErrorCode.VERSION_NOT_FOUND;

/** Exception that is thrown when one tries to access a version which does not exist. */
@Getter
public class VersionNotFoundException extends PoguesException {

    public VersionNotFoundException(String message) {
        super(404, message, null, VERSION_NOT_FOUND);
    }

}
