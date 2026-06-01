package fr.insee.pogues.controller.error;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode
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
