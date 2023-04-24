package fr.insee.pogues.exception;

/**
 * Exception thrown if an error occurs during questionnaire de-referencing (composition feature).
 */
public class DeReferencingException extends RuntimeException {

    public DeReferencingException(String message, Exception e) {
        super(message, e);
    }

}
