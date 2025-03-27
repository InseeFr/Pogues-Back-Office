package fr.insee.pogues.exception;

import fr.insee.pogues.webservice.error.ApiMessage;
import lombok.Getter;

@Getter
public abstract class GenericException extends Exception {

    private final String details;

    protected GenericException(String message, String details){
        super(message);
        this.details = details;
    }

    public ApiMessage toApiMessage() {
        return new ApiMessage(500, this.getMessage(), details, null);
    }
}
