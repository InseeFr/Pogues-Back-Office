package fr.insee.pogues.exception;

/**
 * Exception thrown if an error occurs during questionnaire de-referencing (composition feature).
 */
public class EnoException extends Exception {

    public EnoException(String message, Exception e) {
        super(message, e);
    }

}
