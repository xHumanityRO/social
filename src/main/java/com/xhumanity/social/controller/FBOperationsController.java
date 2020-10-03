package com.xhumanity.social.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xhumanity.social.dto.CampaignVideoDTO;
import com.xhumanity.social.dto.facebook.FeedDTO;
import com.xhumanity.social.service.FacebookService;

@Controller
@RequestMapping("/")
public class FBOperationsController {

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

	@PostMapping("/feed/{forumUserId}/{postUrl}")
    public @ResponseBody CampaignVideoDTO feed(Model model, @PathVariable(value = "forumUserId") Integer forumUserId, 
    		@PathVariable(value = "postUrl") String postUrl) {
		return facebookService.registerPost(model, forumUserId, postUrl);
    }
}