package fr.insee.pogues.user.query;

import fr.insee.pogues.user.model.User;
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User Service Query for the LDAP implementation to assume the identity service
 * of Pogues UI in JSON
 * 
 * @author I6VWID
 *
 */
@Service
@Configuration
@PropertySource("classpath:${fr.insee.pogues.env:prod}/pogues-bo.properties")
public class UserServiceQueryLDAPImpl implements UserServiceQuery {

	final static Logger logger = Logger.getLogger(UserServiceQueryLDAPImpl.class);

	private DirContext context = null;

	private String ldapHost;
	private String ldapRootDn;
	private String ldapUserBaseDn;
	private String ldapUserCommonName;
	private String ldapUserGivenName;
	private String ldapUserSn;
	private String ldapUserFilter;
	private String ldapUnitesBaseDn;
	private String ldapPermissions;
	private String ldapPermissionFilter;
	private String ldapPermissionName;
	private String ldapPermissionDescription;
	private String ldapPermissionOther;
	private String ldapPermissionRegex;

	@Autowired
	private Environment env;

	@PostConstruct
	public void init() {
		ldapHost = env.getProperty("fr.insee.pogues.permission.ldap.hostname");
		ldapRootDn = env.getProperty("fr.insee.pogues.permission.ldap.root");
		ldapUserBaseDn = env.getProperty("fr.insee.pogues.permission.ldap.user.base");
		ldapUnitesBaseDn = env.getProperty("fr.insee.pogues.permission.ldap.unite.base");
		ldapUserCommonName = env.getProperty("fr.insee.pogues.permission.ldap.user.cn");
		ldapUserGivenName = env.getProperty("fr.insee.pogues.permission.ldap.user.givenName");
		ldapUserSn = env.getProperty("fr.insee.pogues.permission.ldap.user.sn");
		ldapUserFilter = env.getProperty("fr.insee.pogues.permission.ldap.user.filtre");
		ldapPermissionName = env.getProperty("fr.insee.pogues.permission.ldap.permission.name");
		ldapPermissionDescription = env.getProperty("fr.insee.pogues.permission.ldap.permission.description");
		ldapPermissionOther = env.getProperty("fr.insee.pogues.permission.ldap.permission.other");
		ldapPermissions = env.getProperty("fr.insee.pogues.permission.ldap.user.permission");
		ldapPermissionFilter = env.getProperty("fr.insee.pogues.permission.ldap.permission.filtre");
		ldapPermissionRegex = env.getProperty("fr.insee.pogues.permission.ldap.user.permission.regex");
		ldapUserBaseDn = String.format("%s,%s", ldapUserBaseDn, ldapRootDn);
		ldapUnitesBaseDn = String.format("%s,%s", ldapUnitesBaseDn, ldapRootDn);
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


	public void closeConnection() throws Exception {
		try {
			this.context.close();
		} catch (Exception e) {
			logger.error("Exception - Impossible to closeConnection the LDAP connection");
			throw e;
		}
	}


	public User getNameAndPermissionByID(String id) throws Exception {
		String name = null;
		String permission = null;
		String firstName = null;
		String lastName = null;
		try {
			this.initConnection();
			// Criteria specification for the permission search
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(new String[] {ldapUserCommonName, ldapPermissions, ldapUserGivenName, ldapUserSn});
			String filter = "(" + ldapUserFilter + id + ")";
			NamingEnumeration<SearchResult> results;
			results = context.search(ldapUserBaseDn, filter, controls);
			while (results.hasMore()) {
				SearchResult entree = results.next();
				name = entree.getAttributes().get(ldapUserCommonName).get().toString();
				permission = entree.getAttributes().get(ldapPermissions).get().toString();
				Matcher matcher = Pattern
						.compile(ldapPermissionRegex)
						.matcher(permission);
				if(matcher.find()){
					permission = matcher.group(1);
				}
				firstName = entree.getAttributes().get(ldapUserGivenName).get().toString();
				lastName = entree.getAttributes().get(ldapUserSn).get().toString();
			}
		}  catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		} finally {
			this.closeConnection();
		}
		return new User(id, name, firstName, lastName, permission);
	}


	public List<String> getPermissions() throws Exception{
		List<String> permissions = new ArrayList<String>();
		try {
			this.initConnection();
			// Criteria specification for the permission search
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(new String[] {
					ldapPermissionName,
					ldapPermissionDescription
			});
			NamingEnumeration<SearchResult> results;
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
			throw e;
		} finally {
			this.closeConnection();
		}
		return permissions;
	}

}
