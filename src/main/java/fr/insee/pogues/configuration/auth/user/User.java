package fr.insee.pogues.configuration.auth.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class User {

    @Getter
    private final String stamp;
    @Getter
    private final String name;
    @Getter
    private final String userId;

    public User(){
        this("default", "Guest", "guest");
    }
}
