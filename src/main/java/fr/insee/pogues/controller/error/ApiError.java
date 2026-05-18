package fr.insee.pogues.controller.error;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public class ApiError {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    public ApiError(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiError apiError = (ApiError) o;
        return status == apiError.status && Objects.equals(error, apiError.error) && Objects.equals(message, apiError.message) && Objects.equals(timestamp, apiError.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, error, message, timestamp);
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "status=" + status +
                ", statusMessage='" + error + '\'' +
                ", message='" + message + '\'' +
                ", date=" + timestamp +
                '}';
    }
}
