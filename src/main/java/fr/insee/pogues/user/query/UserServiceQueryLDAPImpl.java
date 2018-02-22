package fr.insee.pogues.user.query;

import fr.insee.pogues.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceQueryLDAPImpl implements UserServiceQuery {

	private DirContext context = null;

	@Value("${fr.insee.pogues.authentication}")
	private Boolean authentication;

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

	private void initConnection() throws Exception {
		// Connexion à la racine de l'annuaire
		Hashtable<String, String> environment = new Hashtable<>();
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environment.put(Context.PROVIDER_URL, ldapHost);
		environment.put(Context.SECURITY_AUTHENTICATION, "none");
		this.context = new InitialDirContext(environment);
	}

	private void closeConnection() throws Exception {
		this.context.close();
	}

	public User getNameAndPermissionByID(String id) throws Exception {

		String name = null;
		String permission = null;
		String firstName = null;
		String lastName = null;
		User user = null;
		if (!authentication) {
			id = "Guest";
			name = "Guest";
			permission = "TEST";
			firstName = "Guest";
			lastName = "";
			user = new User(id, name, firstName, lastName, permission);
		} else {

			try {
				this.initConnection();
				// Criteria specification for the permission search
				SearchControls controls = new SearchControls();
				controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
				controls.setReturningAttributes(
						new String[] { ldapUserCommonName, ldapPermissions, ldapUserGivenName, ldapUserSn });
				String filter = "(" + ldapUserFilter + id + ")";
				NamingEnumeration<SearchResult> results;
				results = context.search(ldapUserBaseDn, filter, controls);
				while (results.hasMore()) {
					SearchResult entree = results.next();
					name = entree.getAttributes().get(ldapUserCommonName).get().toString();
					permission = entree.getAttributes().get(ldapPermissions).get().toString();
					Matcher matcher = Pattern.compile(ldapPermissionRegex).matcher(permission);
					if (matcher.find()) {
						permission = matcher.group(1);
					}
					firstName = entree.getAttributes().get(ldapUserGivenName).get().toString();
					lastName = entree.getAttributes().get(ldapUserSn).get().toString();
					user = new User(id, name, firstName, lastName, permission);
				}
			} finally {
				this.closeConnection();
			}
		}
		return user;

	}

	public List<String> getPermissions() throws Exception {
		List<String> permissions = new ArrayList<>();
		try {
			this.initConnection();
			// Criteria specification for the permission search
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(new String[] { ldapPermissionName, ldapPermissionDescription });
			NamingEnumeration<SearchResult> results;
			results = this.context.search(ldapUnitesBaseDn, ldapPermissionFilter, controls);
			while (results.hasMore()) {
				SearchResult entree = results.next();
				String permission = entree.getAttributes().get(ldapPermissionName).get().toString();
				if (!permission.equals(ldapPermissionOther)) {
					permissions.add(permission);
				}
			}
		} finally {
			this.closeConnection();
		}
		return permissions;
	}
}
