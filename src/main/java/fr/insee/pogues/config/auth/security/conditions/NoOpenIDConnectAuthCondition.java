package fr.insee.pogues.config.auth.security.conditions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class NoOpenIDConnectAuthCondition implements Condition{
	
	@Value("${fr.insee.pogues.authentication}")
    boolean authentification;

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		return !authentification;
	}

}
