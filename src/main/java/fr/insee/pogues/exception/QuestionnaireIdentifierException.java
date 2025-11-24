package fr.insee.pogues.exception;

/**
 * Exception to be thrown when a Pogues questionnaire has an invalid identifier.
 */
public class QuestionnaireIdentifierException extends Exception {

    /**
     * @param id Invalid questionnaire identifier value.
     */
    public QuestionnaireIdentifierException(String id) {
        super("Invalid questionnaire identifier: " + id);
    }

}
