package fr.insee.pogues.user.service;

import fr.insee.pogues.user.query.UserServiceQuery;
import fr.insee.pogues.user.query.UserServiceQueryLDAPImpl;
import fr.insee.pogues.utils.json.JSONFunctions;
import org.json.simple.JSONArray;

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
public class UserService {
	
	private HttpServletRequest request;
	private UserServiceQuery serviceQuery;
	
	/**
	 * Contructor for User Service
	 * 
	 */
	public UserService(HttpServletRequest request){
		this.request=request;
		serviceQuery = new UserServiceQueryLDAPImpl();
	}
	
	/**
	 * A method to get the user id of the connected user
	 * 
	 * @return the user
	 */
	public String getUserID() {
		
		String id = request.getUserPrincipal().getName();
		String json = "{\"id\":\""+id+"\"}";
		
		return json;

	}
	
	/**
	 * A method to get the user attributes of the connected user
	 * 
	 * @return the user
	 */
	public String getNameAndPermission() {
		
		String id = request.getUserPrincipal().getName();
		Map<String, String> attributes = serviceQuery.getNameAndPermissionByID(id);
		serviceQuery.close();
		return JSONFunctions.getJSON(attributes);

	}

	public String getPermissions() throws Exception {
		try {
			List<String> permissions = this.serviceQuery.getPermissions();
			return JSONFunctions.getJSONArray(permissions);
		} catch(Exception e) {
			throw e;
		} finally {
			this.serviceQuery.close();
		}
	}
	
	
}
