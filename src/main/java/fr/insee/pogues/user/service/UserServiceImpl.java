package fr.insee.pogues.user.service;

import fr.insee.pogues.user.query.UserServiceQuery;
import fr.insee.pogues.utils.json.JSONFunctions;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * User Service to assume the identity service of Pogues UI in JSON
 * 
 * @author I6VWID
 * 
 *
 */
@Service
public class UserServiceImpl implements UserService {

	private static Logger logger = Logger.getLogger(UserServiceImpl.class);

	@Autowired
	private UserServiceQuery userServiceQuery;

	public String getUserID(HttpServletRequest request) {
		String id = request.getUserPrincipal().getName();
		String json = "{\"id\":\""+id+"\"}";
		return json;
	}
	

	public String getNameAndPermission(HttpServletRequest request) {
		try {
			String id = request.getUserPrincipal().getName();
			Map<String, String> attributes = this.userServiceQuery.getNameAndPermissionByID(id);
			return JSONFunctions.getJSON(attributes);
		} catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}


	}

	public List<String> getPermissions() throws Exception {
		try {
			return this.userServiceQuery.getPermissions();
		} catch(Exception e) {
			throw e;
		}
	}

}
