package fr.insee.pogues.configuration.auth.security.restrictions;

import fr.insee.pogues.configuration.auth.user.User;

public interface StampsRestrictionsService {
	
	/*
	 * USER
	 */
	
	User getUser();
	
	/*
	 * QUESTIONNAIRE
	 */
	
	boolean isQuestionnaireOwner(String stamp);

}


