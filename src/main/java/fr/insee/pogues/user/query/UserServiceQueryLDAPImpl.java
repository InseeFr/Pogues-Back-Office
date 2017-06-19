package fr.insee.pogues.user.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;

/**
 * User Service Query for the LDAP implementation to assume the identity service
 * of Pogues UI in JSON
 * 
 * @author I6VWID
 *
 */
public class UserServiceQueryLDAPImpl implements UserServiceQuery {

	final static Logger logger = Logger.getLogger(UserServiceQueryLDAPImpl.class);

	private DirContext context = null;

	// TODO externalisation of the parameter
	private static String LDAP_HOST_NAME = "ldap://annuaire.insee.fr";

	/**
	 * Contructor for User Service Query LDAP implementation, init the
	 * connection.
	 * 
	 */
	public UserServiceQueryLDAPImpl() {
		// Connexion à la racine de l'annuaire
		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environment.put(Context.PROVIDER_URL, LDAP_HOST_NAME);
		environment.put(Context.SECURITY_AUTHENTICATION, "none");

		try {
			context = new InitialDirContext(environment);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * A method to close the connection to the LDAP.
	 * 
	 */
	public void close() {
		try {
			context.close();
		} catch (NamingException e) {
			logger.error("NamingException - Impossible to close the LDAP connection");
			e.printStackTrace();
		}
	}

	/**
	 * A method to get the name and the permission by user ID
	 * 
	 * @return the name and the permission in a map<String,String>
	 */
	public Map<String, String> getNameAndPermissionByID(String id) {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("id", id);
		String name = null;
		String permission = null;
		// Criteria specification for the permission search
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		// TODO externalisation of the parameter
		controls.setReturningAttributes(new String[] { "cn", "inseeUniteDN" });
		// TODO externalisation of the parameter
		String filter = "(uid=" + id + ")";

		NamingEnumeration<SearchResult> results;
		try {
			// TODO externalisation of the parameter
			results = context.search("ou=Personnes,o=insee,c=fr", filter, controls);
			while (results.hasMore()) {
				SearchResult entree = results.next();
				// TODO externalisation of the parameter
				name = entree.getAttributes().get("cn").get().toString();
				permission = entree.getAttributes().get("inseeUniteDN").get().toString();
				// TODO Fix it with a regex
				permission = permission.split(",")[0].split("=")[1];
			}
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		attributes.put("name", name);
		attributes.put("permission", permission);

		return attributes;
	}

	/**
	 * A method to get the Permissions List from the LDAP
	 * 
	 * @return the Permissions List List<String>
	 */
	public List<String> getPermissions() {

		List<String> permissions = new ArrayList<String>();

		// Criteria specification for the permission search
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		// TODO externalisation of the parameter
		controls.setReturningAttributes(new String[] { "ou", "description" });
		// TODO externalisation of the parameter
		String filter = "(objectClass=inseeUnite)";

		NamingEnumeration<SearchResult> results;
		try {
			// TODO externalisation of the parameter
			results = context.search("ou=Unités,o=insee,c=fr", filter, controls);
			while (results.hasMore()) {
				SearchResult entree = results.next();
				// TODO externalisation of the parameter
				String permission = entree.getAttributes().get("ou").get().toString();
				// TODO externalisation of the parameter
				if (!permission.equals("AUTRE")) {
					permissions.add("\"" + permission + "\"");
				}
			}
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return permissions;
	}

}
