package fr.insee.pogues.exception;

public class QuestionnaireMetadataRuntimeException extends RuntimeException {

    public QuestionnaireMetadataRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuestionnaireMetadataRuntimeException(String message) {
        super(message);
    }
}
