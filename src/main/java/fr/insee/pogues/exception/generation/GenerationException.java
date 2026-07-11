package fr.insee.pogues.exception.generation;

/**
 * Exception thrown if an error occurs during questionnaire de-referencing (composition feature).
 */
public class GenerationException extends Exception {

    public GenerationException(String message, Exception e) {
        super(message, e);
    }

}
