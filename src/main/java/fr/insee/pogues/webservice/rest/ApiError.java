package fr.insee.pogues.webservice.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ApiError {
    @JsonProperty("timestamp")
    private Date date;
    private int status;
    @JsonProperty("error")
    private String statusMessage;
    private String message;

    public ApiError(int status, String statusMessage, String message) {
        this.status = status;
        this.statusMessage = statusMessage;
        this.message = message;
        this.date = Calendar.getInstance().getTime();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiError apiError = (ApiError) o;
        return status == apiError.status && Objects.equals(statusMessage, apiError.statusMessage) && Objects.equals(message, apiError.message) && Objects.equals(date, apiError.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, statusMessage, message, date);
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "status=" + status +
                ", statusMessage='" + statusMessage + '\'' +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}
