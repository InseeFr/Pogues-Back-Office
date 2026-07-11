package fr.insee.pogues.controller.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiMessage {
    private int status;
    private String message;
    private String detail;
    private String errorCode;
}
