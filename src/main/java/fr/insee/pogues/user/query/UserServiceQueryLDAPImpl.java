package fr.insee.pogues.user.query;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.*;

/**
 * User Service Query for the LDAP implementation to assume the identity service
 * of Pogues UI in JSON
 * 
 * @author I6VWID
 *
 */
@Service
@Configuration
@PropertySource("classpath:pogues-bo.properties")
public class UserServiceQueryLDAPImpl implements UserServiceQuery {

	final static Logger logger = Logger.getLogger(UserServiceQueryLDAPImpl.class);

	private DirContext context = null;

	private String ldapHost;
	private String ldapUserBaseDn;
	private String ldapUserName;
	private String ldapUserFilter;
	private String ldapUnitesBaseDn;
	private String ldapPermissionsDn;
	private String ldapPermissionFilter;
	private String ldapPermissionName;
	private String ldapPermissionDescription;
	private String ldapPermissionOther;

	@Autowired
	private Environment env;

	@PostConstruct
	public void init() {
		ldapHost = env.getProperty("fr.insee.pogues.permission.ldap.hostname");
		ldapUserBaseDn = env.getProperty("fr.insee.pogues.permission.ldap.user.base");
		ldapUnitesBaseDn = env.getProperty("fr.insee.pogues.permission.ldap.unite.base");
		ldapUserName = env.getProperty("fr.insee.pogues.permission.ldap.user.name");
		ldapUserFilter = env.getProperty("fr.insee.pogues.permission.ldap.user.filtre");
		ldapPermissionName = env.getProperty("fr.insee.pogues.permission.ldap.permission.name");
		ldapPermissionDescription = env.getProperty("fr.insee.pogues.permission.ldap.permission.description");
		ldapPermissionOther = env.getProperty("fr.insee.pogues.permission.ldap.permission.other");
		ldapPermissionsDn = env.getProperty("fr.insee.pogues.permission.ldap.user.permission");
		ldapPermissionFilter = env.getProperty("fr.insee.pogues.permission.ldap.permission.filtre");
	}

	public void initConnection(){
		// Connexion à la racine de l'annuaire
		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environment.put(Context.PROVIDER_URL, ldapHost);
		environment.put(Context.SECURITY_AUTHENTICATION, "none");
		try {
			this.context = new InitialDirContext(environment);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}


	public void closeConnection() {
		try {
			this.context.close();
		} catch (NamingException e) {
			logger.error("NamingException - Impossible to closeConnection the LDAP connection");
			e.printStackTrace();
		}
	}


	public Map<String, String> getNameAndPermissionByID(String id) {
		this.initConnection();
		Map<String, String> attributes = new HashMap<>();
		attributes.put("id", id);
		String name = null;
		String permission = null;
		// Criteria specification for the permission search
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setReturningAttributes(new String[] {ldapUserName, ldapPermissionsDn });
		String filter = "(" + ldapUserFilter + id + ")";
		NamingEnumeration<SearchResult> results;
		try {
			results = context.search(ldapUserBaseDn, filter, controls);
			while (results.hasMore()) {
				SearchResult entree = results.next();
				name = entree.getAttributes().get(ldapUserName).get().toString();
				permission = entree.getAttributes().get(ldapPermissionsDn).get().toString();
				// TODO Fix it with a regex
				permission = permission.split(",")[0].split("=")[1];
			}
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			this.closeConnection();
		}
		attributes.put("name", name);
		attributes.put("permission", permission);

		return attributes;
	}


	public List<String> getPermissions() {
		this.initConnection();
		List<String> permissions = new ArrayList<String>();

		// Criteria specification for the permission search
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setReturningAttributes(new String[] {
				ldapPermissionName,
				ldapPermissionDescription
		});

		NamingEnumeration<SearchResult> results;
		try {
			results = this.context.search(ldapUnitesBaseDn, ldapPermissionFilter, controls);
			while (results.hasMore()) {
				SearchResult entree = results.next();
				String permission = entree.getAttributes().get(ldapPermissionName).get().toString();
				if (!permission.equals(ldapPermissionOther)) {
					permissions.add(permission);
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			this.closeConnection();
		}
		return permissions;
	}

}
