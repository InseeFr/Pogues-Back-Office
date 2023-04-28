package fr.insee.pogues.exception;

/**
 * Exception thrown if a Pogues object has an empty business name.
 */
public class EmptyNameException extends RuntimeException {

    public EmptyNameException(String message) {
        super(message);
    }

}
