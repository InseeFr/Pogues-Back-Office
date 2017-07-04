package fr.insee.pogues.webservice.rest;

/**
 * Created by acordier on 04/07/17.
 */
public class PoguesException extends Exception {

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
}
