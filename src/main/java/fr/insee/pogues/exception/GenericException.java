package fr.insee.pogues.exception;

import fr.insee.pogues.controller.error.ApiMessage;
import lombok.Getter;

@Getter
public abstract class GenericException extends RuntimeException {

    private final String detail;

    protected GenericException(String message, String detail){
        super(message);
        this.detail = detail;
    }

    public ApiMessage toApiMessage() {
        return new ApiMessage(500, this.getMessage(), detail, null);
    }
}
