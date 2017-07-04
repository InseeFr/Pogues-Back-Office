package fr.insee.pogues.persistence.query;

/**
 * Created by acordier on 04/07/17.
 */
public class NonUniqueResultException extends Exception {
    public NonUniqueResultException() {
        super();
    }

    public NonUniqueResultException(String message) {
        super(message);
    }

    public NonUniqueResultException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonUniqueResultException(Throwable cause) {
        super(cause);
    }

    protected NonUniqueResultException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
