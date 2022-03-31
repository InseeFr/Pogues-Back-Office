package fr.insee.pogues.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

@Component
public class PropertiesLog {

    private static final Logger log = Logger.getLogger(PropertiesLog.class.getName());
    private final Environment environment;

    public PropertiesLog(Environment environment) {
        Objects.requireNonNull(environment);
        this.environment=environment;

        log.info("===============================================================================================");
        log.info("                                Valeurs des properties                                         ");
        log.info("===============================================================================================");

        ((AbstractEnvironment) environment).getPropertySources().stream()
                .map(propertySource -> {
                    if (propertySource instanceof EnumerablePropertySource){
                        return ((EnumerablePropertySource<?>)propertySource).getPropertyNames();
                    }else{
                        log.warning(propertySource+ " n'est pas EnumerablePropertySource : impossible à lister");
                        return new String[] {};
                    }
                }
                )
                .flatMap(Arrays::stream)
                .distinct()
                .forEach(key->log.info(key+" = "+printValueWithoutPassword(key)));
    }
    
	private String printValueWithoutPassword(String key) {
		if (StringUtils.containsAny(key, "password", "pwd", "keycloak.key", "jeton", "secret")) {
			return "******";
		}
		return environment.getProperty(key);
	}

}
