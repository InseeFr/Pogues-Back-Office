package fr.insee.pogues.user.service;

import fr.insee.pogues.user.model.User;
import fr.insee.pogues.user.query.UserServiceQuery;
import fr.insee.pogues.webservice.rest.PoguesException;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

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

	public JSONObject getUserID(HttpServletRequest request) throws Exception {
		try {
			Principal principal = request.getUserPrincipal();
			if (null == principal) {
				throw new PoguesException(403, "Not authenticated", "No user principal found, are you authenticated ?");
			}
			JSONObject json = new JSONObject();
			json.put("id", principal.getName());
			return json;
		} catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	

	public User getNameAndPermission(HttpServletRequest request) throws Exception {
		try {
			String id = request.getUserPrincipal().getName();
			return userServiceQuery.getNameAndPermissionByID(id);
		} catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}


	public List<String> getPermissions() throws Exception {
		try {
			return userServiceQuery.getPermissions();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
