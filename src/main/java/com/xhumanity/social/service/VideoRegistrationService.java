package com.xhumanity.social.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xhumanity.social.dto.forum.PostDTO;
import com.xhumanity.social.model.CampaignVideo;
import com.xhumanity.social.model.TelegramUser;
import com.xhumanity.social.repository.CampaignVideoRepository;
import com.xhumanity.social.utils.ForumUtils;

@Service
public class VideoRegistrationService {
	private static final Logger logger = LogManager.getLogger(VideoRegistrationService.class);

	private static final int PROMO_TOPIC_ID = 11100297;
	private static final int WELCOME_XHUMANITY_CAMPAIGN_ID = 1;

	@Autowired
	private CampaignVideoRepository campaignVideoRepository;
	
	public String register(TelegramUser user, String url, String forumApiKey) throws Exception {
		String postLink = createPost(user, url, forumApiKey);
		campaignVideoRepository.save(CampaignVideo.builder()
				.campaignId(WELCOME_XHUMANITY_CAMPAIGN_ID)
				.source(CampaignVideo.SOURCE_YOUTUBE)
				.userId(user.getId())
				.link(url)
				.postUrl(postLink)
				.build());
		
		return postLink;
	}

	private String createPost(TelegramUser user, String url, String forumApiKey) {
		int topicId = PROMO_TOPIC_ID;
		final String subject = user.getFirstName() + "'s promotional video";
		final String message = "This is my video. Waiting for your reaction...\\n" + url;

		String postLink = "Error creating automated post";
		try {
			PostDTO post = ForumUtils.createPost(user.getForumUsername(), topicId, subject, message, forumApiKey);
			postLink = post.getURL();
		} catch (Exception e) {
			logger.error(e);
		}
		return postLink;
	}


}
