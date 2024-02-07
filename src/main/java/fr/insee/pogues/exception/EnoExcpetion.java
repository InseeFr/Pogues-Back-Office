package fr.insee.pogues.exception;

/**
 * Exception thrown if an error occurs during questionnaire de-referencing (composition feature).
 */
public class EnoExcpetion extends Exception {

    public EnoExcpetion(String message, Exception e) {
        super(message, e);
    }

}
