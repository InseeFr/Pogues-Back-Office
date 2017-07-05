package fr.insee.pogues.user.service;

import fr.insee.pogues.user.query.UserServiceQuery;
import fr.insee.pogues.utils.json.JSONFunctions;
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

	private UserServiceQuery userServiceQuery;
	

	public String getUserID(HttpServletRequest request) {
		String id = request.getUserPrincipal().getName();
		String json = "{\"id\":\""+id+"\"}";
		return json;
	}
	

	public String getNameAndPermission(HttpServletRequest request) {
		
		String id = request.getUserPrincipal().getName();
		Map<String, String> attributes = this.userServiceQuery.getNameAndPermissionByID(id);
		this.userServiceQuery.close();
		return JSONFunctions.getJSON(attributes);

	}

	public List<String> getPermissions() throws Exception {
		try {
			return this.userServiceQuery.getPermissions();
		} catch(Exception e) {
			throw e;
		} finally {
			this.userServiceQuery.close();
		}
	}

	@Autowired
	public void setUserServiceQuery(UserServiceQuery userServiceQuery) {
		this.userServiceQuery = userServiceQuery;
	}
	
}
