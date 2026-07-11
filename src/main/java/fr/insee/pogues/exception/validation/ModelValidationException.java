package fr.insee.pogues.exception.validation;

import lombok.Getter;

import java.util.List;

@Getter
public class ModelValidationException extends RuntimeException {

    private final List<String> details;

    public ModelValidationException(String message, List<String> details) {
        super(message);
        this.details = details;
    }

}
