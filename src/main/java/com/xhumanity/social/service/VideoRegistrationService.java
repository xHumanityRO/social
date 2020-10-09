package com.xhumanity.social.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xhumanity.social.dto.CampaignVideoDTO;
import com.xhumanity.social.dto.forum.TopicDTO;
import com.xhumanity.social.model.CampaignVideo;
import com.xhumanity.social.model.TelegramUser;
import com.xhumanity.social.repository.CampaignVideoRepository;
import com.xhumanity.social.utils.ForumUtils;

@Service
public class VideoRegistrationService {
	private static final Logger logger = LogManager.getLogger(VideoRegistrationService.class);

	private static final int PROMO_TOPIC_ID = 11100297;
	public static final int WELCOME_XHUMANITY_CAMPAIGN_ID = 827617;
	private static final int YOUTUBE_CAMPAIGN_ID = 1;

	@Autowired
	private CampaignVideoRepository campaignVideoRepository;
	
	public CampaignVideoDTO register(TelegramUser user, String videoUrl, String mediaId, String forumApiKey, String source) throws Exception {
		String postLink = createPost(user, videoUrl, forumApiKey);
		campaignVideoRepository.save(CampaignVideo.builder()
				.campaignId(YOUTUBE_CAMPAIGN_ID)
				.source(source)
				.userId(user.getId())
				.link(videoUrl)
				.entityId(mediaId)
				.postUrl(postLink)
				.build());
		
		return CampaignVideoDTO.builder().postUrl(postLink).videoUrl(videoUrl).userId(user.getForumUserId()).build();
	}

	private String createPost(TelegramUser user, String url, String forumApiKey) {
		final String subject = user.getFirstName() + "'s promotional video";
		final String message = "This is my video. Waiting for your reaction...\\n" + url;

		String postLink = "Error creating automated post";
		try {
			TopicDTO topic = ForumUtils.createTopic(user.getForumUsername(), WELCOME_XHUMANITY_CAMPAIGN_ID, subject, message, forumApiKey);
			postLink = topic.getURL();
		} catch (Exception e) {
			logger.error(e);
		}
		return postLink;
	}


}
