package fr.insee.pogues.configuration.swagger.role;

import fr.insee.pogues.configuration.auth.AuthorityPrivileges;
import fr.insee.pogues.configuration.auth.AuthorityRoleEnum;
import lombok.Getter;

import java.util.List;

@Getter
public enum RoleUIMapper {
    ADMIN(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES,
            AuthorityRoleEnum.ADMIN),
    USER(AuthorityPrivileges.HAS_USER_PRIVILEGES,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.DESIGNER,
            AuthorityRoleEnum.DESIGNER_ALTERNATIVE,
            AuthorityRoleEnum.WEBCLIENT),
    DESIGNER(AuthorityPrivileges.HAS_DESIGNER_PRIVILEGES,
            AuthorityRoleEnum.DESIGNER,
            AuthorityRoleEnum.DESIGNER_ALTERNATIVE);

    private final String roleExpression;

    private final List<AuthorityRoleEnum> roles;

    RoleUIMapper(String roleExpression, AuthorityRoleEnum... roles) {
        this.roleExpression = roleExpression;
        this.roles = List.of(roles);
    }
}