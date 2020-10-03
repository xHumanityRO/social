package com.xhumanity.social.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Page;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.xhumanity.social.dto.facebook.FeedDTO;
import com.xhumanity.social.dto.facebook.PostDTO;
import com.xhumanity.social.model.TelegramUser;
import com.xhumanity.social.repository.TelegramUserRepository;
import com.xhumanity.social.utils.FacebookUtils;

@Service
public class FacebookService {

	@Value("${spring.social.facebook.appId}")
	private String facebookAppId;
	@Value("${spring.social.facebook.appSecret}")
	private String facebookSecret;
	@Value("${my.chatId}")
	private Long myChatId;

	@Autowired
	private TelegramUserRepository telegramUserRepository;

	private String accessToken;

	public String createFacebookAuthorizationURL() {
		FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
		OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
		OAuth2Parameters params = new OAuth2Parameters();
		params.setRedirectUri("https://webapp.xhumanity.org:8443/social/facebook");
		params.setScope("public_profile,email,user_birthday,user_posts");
		return oauthOperations.buildAuthorizeUrl(params);
	}

	public void createFacebookAccessToken(String code) throws URISyntaxException, IOException {
		FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
		AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code,
				"https://webapp.xhumanity.org:8443/social/facebook", null);
		accessToken = accessGrant.getAccessToken();

		Optional<TelegramUser> telegramUser = telegramUserRepository.findByChatId(myChatId);
		telegramUser.ifPresent(u -> {
			u.setFbAccessToken(accessToken);
			telegramUserRepository.save(u);
		});

	}

	public String getName() {
		Facebook facebook = new FacebookTemplate(accessToken);
		String[] fields = { "id", "name" };
		return facebook.fetchObject("me", String.class, fields);
	}

	public String getFeed(Model model) {
		Facebook facebook = new FacebookTemplate(accessToken);

		User userProfile = facebook.fetchObject(FacebookUtils.LOGGED_USER_ID, User.class, FacebookUtils.USER_FIELD_ID,
				FacebookUtils.USER_FIELD_EMAIL, FacebookUtils.USER_FIELD_FIRST_NAME,
				FacebookUtils.USER_FIELD_LAST_NAME);
		model.addAttribute("userProfile", userProfile);
		PagedList<Post> userFeed = facebook.feedOperations().getFeed();
		model.addAttribute("userFeed", userFeed);
		return "feed";
	}

	public String getPageLikes() {
		Facebook facebook = new FacebookTemplate(accessToken);
		Page page = facebook.pageOperations().getPage("114870950352141");
		String pageInfo = "" + page.getId() + ", " + page.getName() + ", " + page.getCategory() + ", "
				+ page.getLikes();
		return pageInfo;
	}

	public String getPosts(Model model, String forumUserId) {
		Optional<TelegramUser> telegramUser = telegramUserRepository.findByForumUserId(forumUserId);
		telegramUser.ifPresent(u -> {
			Facebook facebook = new FacebookTemplate(u.getFbAccessToken());

			User userProfile = facebook.fetchObject(FacebookUtils.LOGGED_USER_ID, User.class,
					FacebookUtils.USER_FIELD_ID, FacebookUtils.USER_FIELD_EMAIL, FacebookUtils.USER_FIELD_FIRST_NAME,
					FacebookUtils.USER_FIELD_LAST_NAME);
			model.addAttribute("userProfile", userProfile);
			PagedList<Post> userFeed = facebook.feedOperations().getFeed();
			model.addAttribute("userFeed", userFeed);
		});
		return "feed";
	}

	public FeedDTO getFeed(Model model, String forumUserId) {
		Optional<TelegramUser> telegramUser = telegramUserRepository.findByForumUserId(forumUserId);
		FeedDTO feed = new FeedDTO();
		telegramUser.ifPresent(u -> {
			Facebook facebook = new FacebookTemplate(u.getFbAccessToken());

			User userProfile = facebook.fetchObject(FacebookUtils.LOGGED_USER_ID, User.class,
					FacebookUtils.USER_FIELD_ID, FacebookUtils.USER_FIELD_EMAIL, FacebookUtils.USER_FIELD_FIRST_NAME,
					FacebookUtils.USER_FIELD_LAST_NAME);
			feed.setFirstName(userProfile.getFirstName());
			feed.setLastName(userProfile.getLastName());
			PagedList<Post> userFeed = facebook.feedOperations().getFeed();
			userFeed.forEach(p -> {
				PostDTO post = new PostDTO();
				post.setId(p.getId());
				post.setMessage(p.getMessage());
				post.setPicture(p.getPicture());
				feed.getPosts().add(post);
			});
			model.addAttribute("userFeed", userFeed);
		});
		return feed;
	}
}