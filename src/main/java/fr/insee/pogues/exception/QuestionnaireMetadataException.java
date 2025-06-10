package fr.insee.pogues.exception;

public class QuestionnaireMetadataException extends PoguesException {

    public QuestionnaireMetadataException(int status, String message, Throwable cause) {
        super(status, message, cause != null ? cause.getMessage() : null);
    }

    public QuestionnaireMetadataException(int status, String message, String details) {
        super(status, message, details);
    }

    public QuestionnaireMetadataException(int status, String message) {
        super(status, message, null);
    }

}
