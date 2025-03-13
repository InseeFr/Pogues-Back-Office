package fr.insee.pogues.webservice.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiMessage {
    private int status;
    private String message;
    private String details;
}
