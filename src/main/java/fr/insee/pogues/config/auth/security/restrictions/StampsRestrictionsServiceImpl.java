package fr.insee.pogues.config.auth.security.restrictions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fr.insee.pogues.config.auth.UserProvider;
import fr.insee.pogues.config.auth.user.User;

@Service
public class StampsRestrictionsServiceImpl implements StampsRestrictionsService{
	
	static final Logger logger = LogManager.getLogger(StampsRestrictionsServiceImpl.class);
	
	@Autowired
	UserProvider userProvider;
	
	@Override
	public User getUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = userProvider.getUser(authentication);
		logger.info("Current user has stamp {}", currentUser.getStamp());
		return currentUser;
	}
	
	@Override
	public boolean isQuestionnaireOwner(String stamp) {
		User user = getUser();
		logger.info("Check if user is the questionnaire's owner");
		return user.getStamp().equals(stamp);
	}
}
