package fr.insee.pogues.user.model;

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private String name;
    private String permission;
    private String firstName;
    private String lastName;

    public User() {}

    /// [marker0]
    public User(String id, String name, String firstName, String lastName, String permission) {
        this.id = id;
        this.name = name;
        this.permission = permission;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    /// [marker1]

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
