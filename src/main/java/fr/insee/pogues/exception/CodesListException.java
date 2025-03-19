package fr.insee.pogues.exception;

import fr.insee.pogues.webservice.error.ApiMessage;
import fr.insee.pogues.webservice.error.CodesListMessage;
import lombok.Getter;

import java.util.List;

@Getter
public class CodesListException extends PoguesException {

    private final List<String> questionIds;

    public CodesListException(int status, String message, String details, List<String> questionIds) {
       super(status, message, details);
       this.questionIds = questionIds;
    }

    @Override
    public ApiMessage toApiMessage(){
        return new CodesListMessage(this.getStatus(), this.getMessage(),this.getDetails(), questionIds);
    }

}
