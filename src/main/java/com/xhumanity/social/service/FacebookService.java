package com.xhumanity.social.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xhumanity.social.dto.CampaignVideoDTO;
import com.xhumanity.social.dto.facebook.FeedDTO;
import com.xhumanity.social.dto.facebook.InsightDTO;
import com.xhumanity.social.dto.facebook.PostDTO;
import com.xhumanity.social.dto.forum.TopicDTO;
import com.xhumanity.social.model.CampaignVideo;
import com.xhumanity.social.model.TelegramUser;
import com.xhumanity.social.repository.TelegramUserRepository;
import com.xhumanity.social.utils.FacebookUtils;

@Service
public class FacebookService {
	private static final Logger logger = LogManager.getLogger(FacebookService.class);

	@Value("${spring.social.facebook.appId}")
	private String facebookAppId;
	@Value("${spring.social.facebook.appSecret}")
	private String facebookSecret;
	@Value("${my.chatId}")
	private Long myChatId;
	@Value("${forum.api.key}")
	private String forumApiKey;
	
	@Autowired
	private TelegramUserRepository telegramUserRepository;
	@Autowired
	private VideoRegistrationService videoRegistrationService;

	private String accessToken;

	public String createFacebookAuthorizationURL(Integer forumUserId) {
		FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
		OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
		OAuth2Parameters params = new OAuth2Parameters();
		params.setRedirectUri("https://webapp.xhumanity.org/social/facebook");
		params.setScope("public_profile,email,user_posts,read_insights");
		params.setState(String.valueOf(forumUserId));
		return oauthOperations.buildAuthorizeUrl(params);
	}

	public void createFacebookAccessToken(Integer forumUserId, String code) throws URISyntaxException, IOException {
		FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
		AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code,
				"https://webapp.xhumanity.org/social/facebook", null);
		accessToken = accessGrant.getAccessToken();

		//Optional<TelegramUser> telegramUser = telegramUserRepository.findByChatId(myChatId);
		Optional<TelegramUser> telegramUser = telegramUserRepository.findByForumUserId(forumUserId);
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

	public String getPosts(Model model, Integer forumUserId) {
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

	public String getInsights(Model model, Integer forumUserId) {
		Optional<TelegramUser> telegramUser = telegramUserRepository.findByForumUserId(forumUserId);
		telegramUser.ifPresent(u -> {
			Facebook facebook = new FacebookTemplate(u.getFbAccessToken());

			User userProfile = facebook.fetchObject(FacebookUtils.LOGGED_USER_ID, User.class,
					FacebookUtils.USER_FIELD_ID, FacebookUtils.USER_FIELD_EMAIL, FacebookUtils.USER_FIELD_FIRST_NAME,
					FacebookUtils.USER_FIELD_LAST_NAME);
			model.addAttribute("userProfile", userProfile);
			
			RestTemplate restTemplate = new RestTemplate();
		     
			try {
				final String baseUrl = "https://graph.facebook.com/v2.8/3418160981537358_3510908308929291?fields=shares,reactions,comments.summary(true)&access_token=" + u.getFbAccessToken();
				URI uri = new URI(baseUrl);
				 
				ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
				InsightDTO insight = new ObjectMapper().readValue(result.getBody(), InsightDTO.class);
				
				model.addAttribute("likes", insight.getReactions().getData().size());
				model.addAttribute("dislikes", "0");
				model.addAttribute("comments", insight.getComments().getData().size());
				model.addAttribute("picture", "https://scontent-otp1-1.xx.fbcdn.net/v/t1.0-0/s130x130/120848431_3510907702262685_7970974661646936809_n.jpg?_nc_cat=106&_nc_sid=8024bb&_nc_ohc=f-kYozOIRrcAX8Zxzwf&_nc_ht=scontent-otp1-1.xx&tp=7&oh=e1c56e110629fbf054730eb1d6d7cf87&oe=5F9DE34B");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return "insights";
	}

	public FeedDTO getFeed(Model model, Integer forumUserId) {
		Optional<TelegramUser> telegramUser = telegramUserRepository.findByForumUserId(forumUserId);
		FeedDTO feed = new FeedDTO();
		telegramUser.ifPresent(u -> {
			Facebook facebook = new FacebookTemplate(u.getFbAccessToken());

			User userProfile = facebook.fetchObject(FacebookUtils.LOGGED_USER_ID, User.class,
					FacebookUtils.USER_FIELD_ID, FacebookUtils.USER_FIELD_EMAIL, FacebookUtils.USER_FIELD_FIRST_NAME,
					FacebookUtils.USER_FIELD_LAST_NAME);
			feed.setFirstName(userProfile.getFirstName());
			feed.setLastName(userProfile.getLastName());
			feed.setUserId(forumUserId);
			PagedList<Post> userFeed = facebook.feedOperations().getFeed();
			userFeed.forEach(p -> {
				PostDTO post = new PostDTO();
				post.setId(p.getId());
				post.setMessage(p.getMessage());
				post.setPicture(p.getPicture());
				post.setLink(p.getLink());
				feed.getPosts().add(post);
			});
//			PagedList usersLikeThisPost = facebook.likeOperations().getLikes("3418160981537358_2389353731084760");
//			Integer likes = usersLikeThisPost.size();
//			logger.info("likes count: " + likes);
//			PagedList<Comment> comments = facebook.commentOperations().getComments("3418160981537358_2389353731084760");
//			for (Comment comment : comments) {
//				logger.info("comment: " + comment.getMessage());
//			}
			//https://graph.facebook.com/v2.8/3418160981537358_3510908308929291?fields=shares,reactions,comments.summary(true)&access_token=XXXYYY
			model.addAttribute("userFeed", userFeed);
		});
		return feed;
	}

	public CampaignVideoDTO registerPost(Integer forumUserId, String videoUrl) {
		CampaignVideoDTO campaignVideoDTO = CampaignVideoDTO.builder().build();
		Optional<TelegramUser> telegramUser = telegramUserRepository.findByForumUserId(forumUserId);
		if (telegramUser.isPresent() && videoUrl != null && !"".equals(videoUrl)) {
			try {
				campaignVideoDTO = videoRegistrationService.register(telegramUser.get(), videoUrl, "", forumApiKey, CampaignVideo.SOURCE_FACEBOOK);
			} catch (Exception e) {
				logger.error(e);
				throw new InternalError();
			}
		} else {
			throw new IllegalArgumentException();
		}
		
		return campaignVideoDTO;
	}
}