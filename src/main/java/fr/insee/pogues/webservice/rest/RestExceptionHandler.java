package fr.insee.pogues.webservice.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { PoguesException.class })
    public ResponseEntity<RestMessage> handlePoguesException(PoguesException pe) {
        RestMessage message = pe.toRestMessage();
        return new ResponseEntity<>(message, HttpStatusCode.valueOf(message.getStatus()));
    }
    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<ApiError> handleOtherException(Exception e) {
        String cause = e.getCause() != null ? e.getCause().getClass().getSimpleName() : e.getClass().getSimpleName();
        String causeMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
        ApiError apiErrorResponse = new ApiError(500, cause, causeMessage);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.valueOf(500));
    }
}