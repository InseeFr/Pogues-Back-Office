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
	/**
	 * A method to get the Permissions List from the LDAP
	 *
	 * @return the Permissions List List<String>
	 */
	Map<String, String> getNameAndPermissionByID(String id);
	/**
	 * A method to get the name and the permission by user ID
	 *
	 * @return the name and the permission in a map<String,String>
	 */
	List<String> getPermissions();
	
}
