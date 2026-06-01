package fr.insee.pogues.configuration.auth;

public class AuthorityPrivileges {
    private AuthorityPrivileges() {
        throw new IllegalArgumentException("Constant class");
    }

    public static final String HAS_DESIGNER_PRIVILEGES = "hasRole('DESIGNER', 'DESIGNER_ALTERNATIVE')";
    public static final String HAS_USER_PRIVILEGES = "hasAnyRole('DESIGNER', 'DESIGNER_ALTERNATIVE', 'ADMIN', 'WEBCLIENT')";
    public static final String HAS_ADMIN_PRIVILEGES = "hasAnyRole('ADMIN')";
}