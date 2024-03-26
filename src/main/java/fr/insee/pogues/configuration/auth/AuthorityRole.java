package fr.insee.pogues.configuration.auth;

public class AuthorityRole {
    private AuthorityRole() {
        throw new IllegalArgumentException("Constant class");
    }

    public static final String HAS_ROLE_DESIGNER = "hasRole('DESIGNER')";
    public static final String HAS_ANY_ROLE = "hasAnyRole('DESIGNER', 'ADMIN')";
    public static final String HAS_ADMIN_PRIVILEGES = "hasAnyRole('ADMIN')";
}
