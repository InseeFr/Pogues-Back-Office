package fr.insee.pogues.config.auth.security.conditions;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class NoOpenIDConnectAuthCondition implements Condition {

	final static Logger logger = LogManager.getLogger(NoOpenIDConnectAuthCondition.class);

	private static final String WEBAPPS = "%s/webapps/%s";
	private static final String CATALINA_BASE = "catalina.base";
	private Properties env;

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		try {
			env = getEnvironmentProperties();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return env.getProperty("fr.insee.pogues.authentication").equals("NONE");
	}

	private Properties getEnvironmentProperties() throws IOException {
		Properties props = new Properties();
		String env = System.getProperty("fr.insee.pogues.env");
		if (null == env) {
			env = "dev";
		}
		String propsPath = String.format("env/%s/pogues-bo.properties", env);
		props.load(getClass().getClassLoader().getResourceAsStream(propsPath));
		loadPropertiesIfExist(props, "pogues-bo.properties");
		loadPropertiesIfExist(props, "rmspogfo.properties");
		loadPropertiesIfExist(props, "rmespogfo.properties");
		return props;
	}

	private void loadPropertiesIfExist(Properties props, String fileName) throws IOException {
		File f = new File(String.format(WEBAPPS, System.getProperty(CATALINA_BASE), fileName));
		if (f.exists() && !f.isDirectory()) {
			try (FileReader r = new FileReader(f)) {
				props.load(r);
				r.close();
			}
		}
	}

}
