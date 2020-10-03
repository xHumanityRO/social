package com.xhumanity.social.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xhumanity.social.dto.CampaignVideoDTO;
import com.xhumanity.social.dto.facebook.FeedDTO;
import com.xhumanity.social.service.FacebookService;

@Controller
@RequestMapping("/")
public class FBOperationsController {
	private static final Logger logger = LogManager.getLogger(FBOperationsController.class);

	@Autowired
	FacebookService facebookService;

	@GetMapping("/posts/{forumUserId}")
    public String posts(Model model, @PathVariable(value = "forumUserId") Integer forumUserId) {
		return facebookService.getPosts(model, forumUserId);
    }
	
	@GetMapping("/feed/{forumUserId}")
    public @ResponseBody FeedDTO feed(Model model, @PathVariable(value = "forumUserId") Integer forumUserId) {
		return facebookService.getFeed(model, forumUserId);
    }

	@PostMapping(path = "/feed", consumes = "application/json", produces = "application/json")
    public @ResponseBody CampaignVideoDTO feed(@RequestBody CampaignVideoDTO video) {
		logger.info(video);
		return facebookService.registerPost(video.getUserId(), video.getVideoUrl());
    }
}