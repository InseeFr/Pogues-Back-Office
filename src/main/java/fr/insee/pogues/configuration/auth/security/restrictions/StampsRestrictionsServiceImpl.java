package fr.insee.pogues.configuration.auth.security.restrictions;

import fr.insee.pogues.configuration.auth.UserProvider;
import fr.insee.pogues.configuration.auth.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StampsRestrictionsServiceImpl implements StampsRestrictionsService{

	@Autowired
	UserProvider userProvider;
	
	@Override
	public User getUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = userProvider.getUser(authentication);
		log.info("Current user has stamp {}", currentUser.getStamp());
		return currentUser;
	}
	
	@Override
	public boolean isQuestionnaireOwner(String stamp) {
		User user = getUser();
		log.info("Check if user is the questionnaire's owner");
		return user.getStamp().equals(stamp);
	}
}
