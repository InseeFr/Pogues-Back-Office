package fr.insee.pogues.configuration.rest;

public record OidcToken(String tokenValue, long tokenExpirationTime) {

    boolean isExpired(long marginMillis) {
        return System.currentTimeMillis() >= tokenExpirationTime - marginMillis;
    }
}