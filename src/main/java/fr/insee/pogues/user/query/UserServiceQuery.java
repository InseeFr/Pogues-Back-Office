package fr.insee.pogues.user.query;

import java.util.List;
import java.util.Map;

/**
 * User Service Query interface to assume the identity service of Pogues UI in JSON
 * 
 * @author I6VWID
 * 
 */
public interface UserServiceQuery {

	public Map<String, String> getNameAndPermissionByID(String id);
	
	public List<String> getPermissions();
	
	public void close(); 
	
}
