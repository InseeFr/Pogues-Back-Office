package fr.insee.pogues.client.interceptor;

public record OidcToken(String tokenValue, long tokenExpirationTime) {

    boolean isExpired(long marginMillis) {
        return System.currentTimeMillis() >= tokenExpirationTime - marginMillis;
    }
}