package fr.insee.pogues.webservice.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Created by acordier on 04/07/17.
 */
@Slf4j
@RestControllerAdvice
public class PoguesExceptionMapper {

    @ExceptionHandler({PoguesException.class})
    public ResponseEntity<RestMessage> handleServiceException(
            PoguesException ex) {
        RestMessage message = ex.toRestMessage();

        return new ResponseEntity<>(message, HttpStatusCode.valueOf(message.getStatus()));
    }
}
