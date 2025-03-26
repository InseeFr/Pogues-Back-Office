package fr.insee.pogues.exception;

import fr.insee.pogues.webservice.error.ApiMessage;
import lombok.Getter;

@Getter
public class PoguesException extends GenericException {

    private final int status;
    private final String details;

    /**
     *
     * @param status
     * @param message
     * @param details
     */
    public PoguesException(int status, String message, String details) {
        super(message, details);
        this.status = status;
        this.details = details;
    }

    @Override
    public ApiMessage toApiMessage(){
        return new ApiMessage(this.status, this.getMessage(), this.details, null);
    }
}
