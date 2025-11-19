package fr.insee.pogues.exception;

/**
 * Exception to be thrown when a Pogues object has an invalid identifier.
 */
public class PoguesIdentifierException extends Exception {

    /**
     * @param id Invalid identifier value.
     */
    public PoguesIdentifierException(String id) {
        super("Identifier %s is invalid.".formatted(id));
    }

}
