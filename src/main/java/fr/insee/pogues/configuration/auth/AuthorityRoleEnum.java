package fr.insee.pogues.configuration.auth;

public enum AuthorityRoleEnum {
    ADMIN,
    DESIGNER,
    DESIGNER_ALTERNATIVE,
    WEBCLIENT;

    public static final String ROLE_PREFIX = "ROLE_";

    public String securityRole() {
        return ROLE_PREFIX + this.name();
    }

    public static AuthorityRoleEnum fromAuthority(String authority) {
        if(authority != null && authority.startsWith(ROLE_PREFIX)) {
            authority = authority.split(ROLE_PREFIX)[1];
        }
        return AuthorityRoleEnum.valueOf(authority);
    }
}