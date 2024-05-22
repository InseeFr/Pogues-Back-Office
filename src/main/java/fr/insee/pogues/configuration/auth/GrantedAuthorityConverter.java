package fr.insee.pogues.configuration.auth;

import fr.insee.pogues.configuration.properties.OidcProperties;
import fr.insee.pogues.configuration.properties.RoleProperties;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;
@AllArgsConstructor
public class GrantedAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final OidcProperties oidcProperties;
    private final RoleProperties roleProperties;

    /**
     *
     * @param map: Map that represent JWT token
     * @param keyPath : jsonPath to wanted value, ex: realm_access.roles
     * @return the value of keyPath inside Map
     * @param <T>
     */
    public <T> T getDeepPropsOfMapForRoles(Map<String, Object> map, String keyPath){
        Map subMap = (Map) map;
        String[] propertyPath = keyPath.toString().split("\\.");
        for (int i = 0; i < propertyPath.length -1; i++) {
            subMap = (Map) subMap.get(propertyPath[i]);
        }
        return (T) subMap.get(propertyPath[propertyPath.length -1]);

    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();

        List<String> roles = getDeepPropsOfMapForRoles(claims, oidcProperties.roleClaim());

        return roles.stream()
                .map(role -> {
                    if (role.equals(roleProperties.designer())) {
                        return new SimpleGrantedAuthority(AuthConstants.ROLE_PREFIX + AuthorityRoleEnum.DESIGNER);
                    }
                    if (role.equals(roleProperties.admin())) {
                        return new SimpleGrantedAuthority(AuthConstants.ROLE_PREFIX + AuthorityRoleEnum.ADMIN);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}