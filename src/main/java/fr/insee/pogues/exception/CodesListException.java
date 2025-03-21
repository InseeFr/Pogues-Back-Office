package fr.insee.pogues.exception;

import fr.insee.pogues.webservice.error.ApiMessage;
import fr.insee.pogues.webservice.error.CodesListMessage;
import fr.insee.pogues.webservice.error.ErrorCode;
import lombok.Getter;

import java.util.List;

@Getter
public class CodesListException extends PoguesException {

    private final List<String> relatedQuestionNames;
    private final ErrorCode errorCode;

    public CodesListException(int status, ErrorCode errorCode, String message, String details, List<String> relatedQuestionNames) {
       super(status, message, details);
       this.errorCode = errorCode;
       this.relatedQuestionNames = relatedQuestionNames;
    }

    @Override
    public ApiMessage toApiMessage(){
        return new CodesListMessage(this.getStatus(), this.getMessage(),this.getDetails(), relatedQuestionNames, errorCode);
    }

}
