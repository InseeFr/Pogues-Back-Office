package fr.insee.pogues.exception;

/** Thrown when a referenced questionnaire is null when doing questionnaire de-referencing. */
public class NullReferenceException extends Exception {

    public NullReferenceException(String message) {
        super(message);
    }

}
