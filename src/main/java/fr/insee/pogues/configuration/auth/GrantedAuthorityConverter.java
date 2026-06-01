package fr.insee.pogues.configuration.auth;

import fr.insee.pogues.configuration.properties.OidcProperties;
import fr.insee.pogues.configuration.properties.RoleProperties;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class GrantedAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    public static final String REALM_ACCESS_ROLE = "roles";
    public static final String REALM_ACCESS = "realm_access";

    private final Map<String, List<SimpleGrantedAuthority>> roles;
    private final OidcProperties oidcProperties;

    /**
     *
     * @param map: Map that represent JWT token
     * @param keyPath : jsonPath to wanted value, ex: realm_access.roles
     * @return the value of keyPath inside Map
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public <T> T getDeepPropsOfMapForRoles(Map<String, Object> map, String keyPath) {
        Map<String, Object> subMap = map;
        String[] propertyPath = keyPath.split("\\.");
        for (int i = 0; i < propertyPath.length - 1; i++) {
            subMap = (Map<String, Object>) subMap.get(propertyPath[i]);
        }
        return (T) subMap.get(propertyPath[propertyPath.length - 1]);
    }

    public GrantedAuthorityConverter(OidcProperties oidcProperties, RoleProperties roleProperties) {
        this.roles = new HashMap<>();
        this.oidcProperties = oidcProperties;
        initRole(roleProperties.admin(), AuthorityRoleEnum.ADMIN);
        initRole(roleProperties.designer(), AuthorityRoleEnum.DESIGNER);
        initRole(roleProperties.designerAlternative(), AuthorityRoleEnum.DESIGNER_ALTERNATIVE);
        initRole(roleProperties.webclient(), AuthorityRoleEnum.WEBCLIENT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        List<String> userRoles = getUserRoles(jwt);

        return userRoles.stream()
                .filter(this.roles::containsKey)
                .map(this.roles::get)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void initRole(String configRole, AuthorityRoleEnum authorityRole)  {
        // config role is not set
        if(configRole == null || configRole.isBlank()) {
            return;
        }

        this.roles.compute(configRole, (key, grantedAuthorities) -> {
            if(grantedAuthorities == null) {
                grantedAuthorities = new ArrayList<>();
            }
            grantedAuthorities.add(new SimpleGrantedAuthority(authorityRole.securityRole()));
            return grantedAuthorities;
        });
    }

    @SuppressWarnings("unchecked")
    private List<String> getUserRoles(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();

        if(oidcProperties.roleClaim().isEmpty()) {
            Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS);
            return (List<String>) realmAccess.get(REALM_ACCESS_ROLE);
        }
        return (List<String>) getDeepPropsOfMapForRoles(claims, oidcProperties.roleClaim());
    }
}