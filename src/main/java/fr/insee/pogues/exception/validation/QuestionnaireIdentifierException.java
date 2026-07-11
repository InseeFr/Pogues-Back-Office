package fr.insee.pogues.exception.validation;

/**
 * Exception to be thrown when a Pogues questionnaire has an invalid identifier.
 */
public class QuestionnaireIdentifierException extends RuntimeException {

    /**
     * @param id Invalid questionnaire identifier value.
     */
    public QuestionnaireIdentifierException(String id) {
        super("Invalid questionnaire identifier: " + id);
    }

}
