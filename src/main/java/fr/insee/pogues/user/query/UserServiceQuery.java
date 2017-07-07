package fr.insee.pogues.user.query;

import fr.insee.pogues.user.model.User;

import java.util.List;

/**
 * User Service Query interface to assume the identity service of Pogues UI in JSON
 * 
 * @author I6VWID
 * 
 */
public interface UserServiceQuery {
	/**
	 * A method to get the Permissions List from the LDAP
	 * @param id Should be a known user id
	 * @return
	 * @throws Exception
	 */
	User getNameAndPermissionByID(String id) throws Exception;

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	List<String> getPermissions() throws Exception;
	
}
