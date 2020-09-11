package com.xhumanity.social.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

import com.xhumanity.social.repository.UserRepository;
import com.xhumanity.social.utils.FacebookUtils;

@Service
public class FacebookService {

	@Value("${spring.social.facebook.appId}")
	String facebookAppId;
	@Value("${spring.social.facebook.appSecret}")
	String facebookSecret;

	@Autowired
	private UserRepository userRepository;

	private String accessToken;

	public String createFacebookAuthorizationURL() {
		FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
		OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
		OAuth2Parameters params = new OAuth2Parameters();
		params.setRedirectUri("https://localhost:8443/social/facebook");
		params.setScope("public_profile,email,user_birthday,user_posts");
		return oauthOperations.buildAuthorizeUrl(params);
	}

	public void createFacebookAccessToken(String code) throws URISyntaxException, IOException {
		FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
		AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code,
				"https://localhost:8443/social/facebook", null);
		accessToken = accessGrant.getAccessToken();
		
	    String result = getLongLivedTokenResponse();
	    
		Optional<com.xhumanity.social.model.User> forumUser = userRepository.findById(27);
		forumUser.ifPresent(u -> {
			u.setFbAccessToken(accessToken);
			u.setFbRefreshToken(accessGrant.getRefreshToken());
			u.setFbTokenExpire(accessGrant.getExpireTime());
			u.setLogging(result);
			userRepository.save(u);
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
		String pageInfo = "" + page.getId() + ", " + page.getName() + ", " + page.getCategory() + ", " + page.getLikes();
		return pageInfo;
	}

	private String getLongLivedTokenResponse() throws MalformedURLException, URISyntaxException, IOException {
		URL url = new URL(FacebookUtils.getLongLivedTokenURL(facebookAppId, facebookSecret, accessToken));
	    URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(),
	            url.getQuery(), null);
	    String result = FacebookUtils.readURL(uri.toURL());
		return result;
	}

}