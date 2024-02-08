package fr.insee.pogues.webservice.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { PoguesException.class })
    public ResponseEntity<ApiError> handlePoguesException(PoguesException pe) {
        ApiError apiErrorResponse = new ApiError(pe.getStatus(), pe.getMessage(), pe.getDetails());
        return new ResponseEntity<>(apiErrorResponse, org.springframework.http.HttpStatus.valueOf(pe.getStatus()));
    }
    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<ApiError> handleOtherException(Exception e) {
        ApiError apiErrorResponse = new ApiError(500,e.getCause().getClass().getSimpleName(), e.getCause().getMessage());
        return new ResponseEntity<>(apiErrorResponse, org.springframework.http.HttpStatus.valueOf(500));
    }
}