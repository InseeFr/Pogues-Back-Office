package fr.insee.pogues.user.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.insee.pogues.user.query.UserServiceQuery;
import fr.insee.pogues.user.query.UserServiceQueryLDAPImpl;
import fr.insee.pogues.utils.json.JSONFunctions;

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
	
	
}
