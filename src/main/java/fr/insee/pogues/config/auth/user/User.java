package fr.insee.pogues.config.auth.user;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class User {
	
	private String stamp = "";
	private Collection<GrantedAuthority> authorities;
	
	public User() {
		super();
	}
	
	public User(String stamp) {
		this.stamp = stamp;
	}

	public String getStamp() {
		return stamp;
	}
	public void setStamp(String stamp) {
		this.stamp = stamp;
	}
	public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }
    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

}
