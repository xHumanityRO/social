package com.xhumanity.social.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.xhumanity.social.dto.instagram.AuthenticationDTO;
import com.xhumanity.social.dto.instagram.CommentDTO;
import com.xhumanity.social.dto.instagram.InsightDTO;
import com.xhumanity.social.dto.instagram.InstagramMediaDTO;
import com.xhumanity.social.model.CampaignVideo;
import com.xhumanity.social.model.InstagramInsight;
import com.xhumanity.social.model.InstagramInsightComment;
import com.xhumanity.social.model.TelegramUser;
import com.xhumanity.social.repository.CampaignVideoRepository;
import com.xhumanity.social.repository.InstagramInsightCommentRepository;
import com.xhumanity.social.repository.InstagramInsightRepository;
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

	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Exception occured while processing...")
	@ExceptionHandler
	void onInternalError(final InternalError ex) {

	}

	@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Invalid authorization key")
	@ExceptionHandler
	void onIllegalAccessException(final IllegalAccessException ex) {

	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Some parameters are invalid")
	@ExceptionHandler
	void onIllegalArgumentException(final IllegalArgumentException ex) {

	}

	@PostMapping(path = "/instagram", consumes = "application/json", produces = "application/json")
	public @ResponseBody AuthenticationDTO auth(@RequestHeader("xhs-apikey") String apiKey,
			@RequestBody AuthenticationDTO auth) throws IllegalAccessException {
		if (!xhumanityApiKey.equals(apiKey)) {
			throw new IllegalAccessException();
		}
		if (auth == null || auth.getUserId() == null || auth.getToken() == null || "".equals(auth.getToken())) {
			throw new InternalError();
		}
		Optional<TelegramUser> telegramUser = telegramUserRepository.findByForumUserId(auth.getUserId());
		if (telegramUser.isPresent()) {
			TelegramUser user = telegramUser.get();
			user.setInstaAccessToken(auth.getToken());
			telegramUserRepository.save(user);
		} else {
			logger.info("user not found: " + auth.getUserId());
			throw new IllegalArgumentException();
		}
		return auth;
	}

	@PostMapping(path = "/instagram/media", consumes = "application/json", produces = "application/json")
	public @ResponseBody InstagramMediaDTO posts(@RequestHeader("xhs-apikey") String apiKey,
			@RequestBody InstagramMediaDTO media) throws IllegalAccessException {
		if (!xhumanityApiKey.equals(apiKey)) {
			throw new IllegalAccessException();
		}

		if (campaignVideoRepository.findByLink(media.getMediaUrl()).isPresent()) {
			throw new InternalError();
		}
		Optional<TelegramUser> telegramUser = telegramUserRepository.findByForumUserId(media.getUserId());
		String mediaUrl = media.getMediaUrl();
		if (telegramUser.isPresent() && mediaUrl != null && !"".equals(mediaUrl)) {
			try {
				videoRegistrationService.register(telegramUser.get(), mediaUrl, media.getMediaId(), forumApiKey,
						CampaignVideo.SOURCE_INSTAGRAM);
			} catch (Exception e) {
				logger.error(e);
				throw new InternalError();
			}
		} else {
			logger.info("user not found: " + media.getUserId());
			throw new IllegalArgumentException();
		}
		return media;
	}

	@PostMapping(path = "/instagram/insights", consumes = "application/json", produces = "application/json")
	public @ResponseBody InsightDTO insights(@RequestHeader("xhs-apikey") String apiKey,
			@RequestBody InsightDTO insight) throws IllegalAccessException {
		if (!xhumanityApiKey.equals(apiKey)) {
			throw new IllegalAccessException();
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
			throw new IllegalArgumentException();
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
			media.add(InstagramMediaDTO.builder().userId(campaignVideo.getUserId()).mediaUrl(campaignVideo.getLink())
					.mediaId(campaignVideo.getEntityId()).build());
		}
		return media;
	}
}
