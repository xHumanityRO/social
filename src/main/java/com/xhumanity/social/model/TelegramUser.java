package com.xhumanity.social.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "telegram_users")
public class TelegramUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "chat_id")
	private Long chatId;
	
	@Column(name = "user_id")
	private long userId;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;

	@Column(name = "user_password")
	private String password;
	
	@Column(name = "fb_access_token")
	private String fbAccessToken;

	@Column(name = "user_lastvisit")
	private Date lastVisit;
	
	@Column(name = "user_regdate")
	private Date registrationDate;
	
	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "forum_user_id")
	private Integer forumUserId;
	
	@Column(name = "forum_username")
	private String forumUsername;
	
	@Column(name = "forum_email")
	private String forumEmail;
}
