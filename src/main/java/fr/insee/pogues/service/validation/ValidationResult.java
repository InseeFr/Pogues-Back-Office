package fr.insee.pogues.service.validation;

public record ValidationResult(boolean isValid, String errorMessage) {

    public ValidationResult {
        if (isValid && errorMessage != null)
            throw new IllegalArgumentException("A valid result doesn't have an error message.");
    }

    public static ValidationResult valid() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult invalid(String errorMessage) {
        return new ValidationResult(false, errorMessage);
    }

}
