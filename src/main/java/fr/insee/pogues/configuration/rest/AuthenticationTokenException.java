package fr.insee.pogues.configuration.rest;

public class AuthenticationTokenException extends RuntimeException {
    public AuthenticationTokenException(String message) {
        super(message);
    }
}