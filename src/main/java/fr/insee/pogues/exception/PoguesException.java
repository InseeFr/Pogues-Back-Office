package fr.insee.pogues.exception;

import fr.insee.pogues.webservice.rest.RestMessage;

import java.io.IOException;

public class PoguesException extends IOException {

    private int status;
    private String details;

    /**
     *
     * @param status
     * @param message
     * @param details
     */
    public PoguesException(int status, String message, String details) {
        super(message);
        this.status = status;
        this.details = details;
    }

    public RestMessage toRestMessage(){
        return new RestMessage(this.status, this.getMessage(), this.details);
    }
    
    public int getStatus() {
		return status;
	}

	public String getDetails() {
		return details;
	}
}
