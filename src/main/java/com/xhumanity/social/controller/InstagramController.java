package com.xhumanity.social.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xhumanity.social.dto.instagram.AuthenticationDTO;
import com.xhumanity.social.dto.instagram.InsightDTO;
import com.xhumanity.social.dto.instagram.InstagramMediaDTO;
import com.xhumanity.social.model.CampaignVideo;
import com.xhumanity.social.model.TelegramUser;
import com.xhumanity.social.repository.CampaignVideoRepository;
import com.xhumanity.social.repository.TelegramUserRepository;
import com.xhumanity.social.service.InstagramService;
import com.xhumanity.social.service.VideoRegistrationService;

@Controller
@RequestMapping("/")
public class InstagramController {

	private static final Logger logger = LogManager.getLogger(InstagramController.class);

	@Value("${xhumanity.social.apiKey}")
	private String xhumanityApiKey;
	@Value("${forum.api.key}")
	private String forumApiKey;

	@Autowired
	private VideoRegistrationService videoRegistrationService;
	@Autowired
	private InstagramService instagramService;
	@Autowired
	private TelegramUserRepository telegramUserRepository;
	@Autowired
	private CampaignVideoRepository campaignVideoRepository;

	@PostMapping(path = "/instagram", consumes = "application/json", produces = "application/json")
	public @ResponseBody AuthenticationDTO auth(@RequestHeader("xhs-apikey") String apiKey,
			@RequestBody AuthenticationDTO auth) throws IllegalAccessException, MissingServletRequestParameterException {
		if (!xhumanityApiKey.equals(apiKey)) {
			throw new IllegalAccessException();
		}
		if (auth == null || auth.getUserId() == null || "".equals(auth.getUserId())) {
			throw new MissingServletRequestParameterException("userId", "String");
		}
		if (auth.getToken() == null || "".equals(auth.getToken())) {
			throw new MissingServletRequestParameterException("token", "String");
		}
		
		Optional<TelegramUser> telegramUser = telegramUserRepository.findByForumUserId(auth.getForumUserId());
		if (telegramUser.isPresent()) {
			TelegramUser user = telegramUser.get();
			user.setInstaUserId(auth.getUserId());
			user.setInstaAccessToken(auth.getToken());
			telegramUserRepository.save(user);
		} else {
			logger.info("user not found: " + auth.getForumUserId());
			throw new EntityNotFoundException("User not found");
		}
		return auth;
	}

	@PostMapping(path = "/instagram/media", consumes = "application/json", produces = "application/json")
	public @ResponseBody InstagramMediaDTO registration(@RequestHeader("xhs-apikey") String apiKey,
			@RequestBody InstagramMediaDTO media) throws IllegalAccessException, MissingServletRequestParameterException {

		String mediaUrl = media.getMediaUrl();

		if (!xhumanityApiKey.equals(apiKey)) {
			throw new IllegalAccessException();
		}
		if (media == null || media.getForumUserId() == null) {
			throw new MissingServletRequestParameterException("forumUserId", "String");
		}
		if (mediaUrl == null || "".equals(mediaUrl)) {
			logger.info("mediaUrl is empty");
			throw new MissingServletRequestParameterException("mediaUrl", "String");
		}
		if (campaignVideoRepository.findByLink(media.getMediaUrl()).isPresent()) {
			throw new IllegalArgumentException("Media already registered");
		}
		
		Optional<TelegramUser> telegramUser = telegramUserRepository.findByForumUserId(media.getForumUserId());
		if (telegramUser.isPresent()) {
			try {
				videoRegistrationService.register(telegramUser.get(), mediaUrl, media.getMediaId(), forumApiKey,
						CampaignVideo.SOURCE_INSTAGRAM);
			} catch (Exception e) {
				logger.error(e);
				throw new InternalError();
			}
		} else {
			logger.info("user not found: " + media.getForumUserId());
			throw new EntityNotFoundException("User not found");
		}
		return media;
	}

	@PostMapping(path = "/instagram/insights", consumes = "application/json", produces = "application/json")
	public @ResponseBody InsightDTO insights(@RequestHeader("xhs-apikey") String apiKey,
			@RequestBody InsightDTO insight) throws IllegalAccessException, MissingServletRequestParameterException {
		if (!xhumanityApiKey.equals(apiKey)) {
			throw new IllegalAccessException();
		}

		if (insight.getMediaUrl() == null || "".equals(insight.getMediaUrl())) {
			logger.info("mediaUrl is empty");
			throw new MissingServletRequestParameterException("mediaUrl", "String");
		}
		Optional<CampaignVideo> media = campaignVideoRepository.findByLink(insight.getMediaUrl());
		if (!media.isPresent()) {
			throw new IllegalArgumentException();
		}

		if (media.isPresent()) {
			logger.info("insight=" + insight);
			instagramService.saveInsights(insight);
		} else {
			logger.info("media url not found: " + insight.getMediaUrl());
			throw new IllegalArgumentException("Media url not found");
		}
		return insight;
	}

	@GetMapping("/instagram/media")
	public @ResponseBody List<InstagramMediaDTO> allMedia(@RequestHeader("xhs-apikey") String apiKey)
			throws IllegalAccessException {
		if (!xhumanityApiKey.equals(apiKey)) {
			throw new IllegalAccessException();
		}

		List<InstagramMediaDTO> media = new ArrayList<>();
		List<CampaignVideo> mediaList = campaignVideoRepository.findAllBySource(CampaignVideo.SOURCE_INSTAGRAM);
		for (CampaignVideo campaignVideo : mediaList) {
			Optional<TelegramUser> telegramUser = telegramUserRepository.findById(campaignVideo.getUserId());
			Integer forumUserId = null;
			String instaUserId = null;
			if (telegramUser.isPresent()) {
				instaUserId = telegramUser.get().getInstaUserId();
				forumUserId = telegramUser.get().getForumUserId();
			}
			media.add(InstagramMediaDTO.builder().userId(instaUserId).forumUserId(forumUserId)
					.mediaUrl(campaignVideo.getLink()).mediaId(campaignVideo.getEntityId()).build());
		}
		return media;
	}

	@GetMapping("/instagram/media/{forumUserId}")
	public @ResponseBody List<InstagramMediaDTO> userMedia(@RequestHeader("xhs-apikey") final String apiKey,
			@PathVariable("forumUserId") final Integer forumUserId) throws IllegalAccessException {
		if (!xhumanityApiKey.equals(apiKey)) {
			throw new IllegalAccessException();
		}
		if (forumUserId == null) {
			logger.info("forum user ID not specified");
			throw new IllegalArgumentException("forumUserId not specified");
		}

		List<InstagramMediaDTO> media = new ArrayList<>();
		Optional<TelegramUser> telegramUser = telegramUserRepository.findByForumUserId(forumUserId);
		if (telegramUser.isPresent()) {
			List<CampaignVideo> mediaList = campaignVideoRepository
					.findAllBySourceAndUserId(CampaignVideo.SOURCE_INSTAGRAM, telegramUser.get().getId());
			for (CampaignVideo campaignVideo : mediaList) {
				String instaUserId = telegramUser.get().getInstaUserId();
				media.add(InstagramMediaDTO.builder().userId(instaUserId).forumUserId(forumUserId)
						.mediaUrl(campaignVideo.getLink()).mediaId(campaignVideo.getEntityId()).build());
			}
		} else {
			logger.info("user not found: " + forumUserId);
			throw new EntityNotFoundException("User not found");
		}

		return media;
	}
}
