package fr.insee.pogues.exception;

public class PoguesDeserializationException extends Exception {

    public PoguesDeserializationException(String message, Exception e) {
        super(message, e);
    }

    public PoguesDeserializationException(String message) {
        super(message);
    }

}
