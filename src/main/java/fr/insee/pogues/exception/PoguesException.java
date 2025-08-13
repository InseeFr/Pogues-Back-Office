package fr.insee.pogues.exception;

import fr.insee.pogues.controller.error.ApiMessage;
import fr.insee.pogues.controller.error.ErrorCode;
import lombok.Getter;

/**
 * Exception that is thrown when there is an error in API requests. Its use is usually discouraged as it is better to
 * use a more specific exception.
 */
@Getter
public class PoguesException extends GenericException {

    private final int status;
    /** More information about the error which should help debugging, for example the payload of the object. */
    private final String details;
    /** An error code known to Pogues UI which allow it to display a translated and readable message. */
    private final ErrorCode errorCode;

    /**
     * Create a Pogues exception to be sent back by the API.
     * @param status Status code of the error
     * @param message A human-readable message about what failed
     * @param details A developer readable message about what caused the failure (for example, the payload)
     */
    public PoguesException(int status, String message, String details) {
        super(message, details);
        this.status = status;
        this.details = details;
        this.errorCode = null;
    }

    /**
     * Create a Pogues exception to be sent back by the API.
     * @param status Status code of the error
     * @param message A human-readable message about what failed
     * @param details A developer-readable message about what caused the failure (for example, the payload)
     * @param errorCode A Pogues UI readable message which will allow it to display a translated and readable message
     */
    public PoguesException(int status, String message, String details, ErrorCode errorCode) {
        super(message, details);
        this.status = status;
        this.details = details;
        this.errorCode = errorCode;
    }

    @Override
    public ApiMessage toApiMessage(){
        return new ApiMessage(
                this.getStatus(),
                this.getMessage(),
                this.getDetails(),
                this.getErrorCode() != null ? this.getErrorCode().label : null
        );
    }
}
