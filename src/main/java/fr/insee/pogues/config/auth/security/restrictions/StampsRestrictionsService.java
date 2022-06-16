package fr.insee.pogues.config.auth.security.restrictions;

import fr.insee.pogues.config.auth.user.User;

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


