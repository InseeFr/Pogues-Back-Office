package fr.insee.pogues.user.query;

import fr.insee.pogues.user.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
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
public class UserServiceQueryLDAPImpl implements UserServiceQuery {

	final static Logger logger = LogManager.getLogger(UserServiceQueryLDAPImpl.class);

	private DirContext context = null;

	@Value("${fr.insee.pogues.permission.ldap.hostname}")
	private String ldapHost;

	@Value("${fr.insee.pogues.permission.ldap.root}")
	private String ldapRootDn;

	@Value("${fr.insee.pogues.permission.ldap.user.base}")
	private String ldapUserBaseDn;

	@Value("${fr.insee.pogues.permission.ldap.user.cn}")
	private String ldapUserCommonName;

	@Value("${fr.insee.pogues.permission.ldap.user.givenName}")
	private String ldapUserGivenName;

	@Value("${fr.insee.pogues.permission.ldap.user.sn}")
	private String ldapUserSn;

	@Value("${fr.insee.pogues.permission.ldap.user.filtre}")
	private String ldapUserFilter;

	@Value("${fr.insee.pogues.permission.ldap.unite.base}")
	private String ldapUnitesBaseDn;

	@Value("${fr.insee.pogues.permission.ldap.user.permission}")
	private String ldapPermissions;

	@Value("${fr.insee.pogues.permission.ldap.permission.filtre}")
	private String ldapPermissionFilter;

	@Value("${fr.insee.pogues.permission.ldap.permission.name}")
	private String ldapPermissionName;

	@Value("${fr.insee.pogues.permission.ldap.permission.description}")
	private String ldapPermissionDescription;

	@Value("${fr.insee.pogues.permission.ldap.permission.other}")
	private String ldapPermissionOther;

	@Value("${fr.insee.pogues.permission.ldap.user.permission.regex}")
	private String ldapPermissionRegex;

	@PostConstruct
	public void init() {
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
