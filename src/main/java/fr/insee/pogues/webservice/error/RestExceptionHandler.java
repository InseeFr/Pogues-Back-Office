package fr.insee.pogues.webservice.error;

import fr.insee.pogues.exception.GenericException;
import fr.insee.pogues.webservice.rest.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { GenericException.class, })
    public ResponseEntity<ApiMessage> handleGenericException(GenericException exception) {
        log.error(exception.getMessage(), exception);
        ApiMessage message = exception.toApiMessage();
        return new ResponseEntity<>(message, HttpStatusCode.valueOf(message.getStatus()));
    }
    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<ApiError> handleOtherException(Exception e) {
        log.error(e.getMessage(), e);
        String cause = e.getCause() != null ? e.getCause().getClass().getSimpleName() : e.getClass().getSimpleName();
        String causeMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
        ApiError apiErrorResponse = new ApiError(500, cause, causeMessage);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.valueOf(500));
    }
}