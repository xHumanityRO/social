package com.xhumanity.social.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "jforum_users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id")
	private Integer id;

	@Column(name = "username")
	private String username;
	
	@Column(name = "user_email")
	private String email;
	
	@Column(name = "user_active")
	private Boolean active;
	
	@Column(name = "deleted")
	private Boolean deleted;

	@Column(name = "fb_access_token")
	private String fbAccessToken;

	@Column(name = "fb_refresh_token")
	private String fbRefreshToken;
	
	@Column(name = "fb_token_expire")
	private Long fbTokenExpire;
	  
	@Column(name = "logging")
	private String logging;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getFbAccessToken() {
		return fbAccessToken;
	}

	public void setFbAccessToken(String fbAccessToken) {
		this.fbAccessToken = fbAccessToken;
	}

	public String getFbRefreshToken() {
		return fbRefreshToken;
	}

	public void setFbRefreshToken(String fbRefreshToken) {
		this.fbRefreshToken = fbRefreshToken;
	}

	public Long getFbTokenExpire() {
		return fbTokenExpire;
	}

	public void setFbTokenExpire(Long fbTokenExpire) {
		this.fbTokenExpire = fbTokenExpire;
	}

	public String getLogging() {
		return logging;
	}

	public void setLogging(String logging) {
		this.logging = logging;
	}

}