package com.xhumanity.social.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xhumanity.social.model.FeedDTO;
import com.xhumanity.social.service.FacebookService;

@Controller
@RequestMapping("/")
public class FBOperationsController {

	@Autowired
	FacebookService facebookService;

	@GetMapping("/posts/{username}")
    public String posts(Model model, @PathVariable(value = "username") String username) {
		return facebookService.getPosts(model, username);
    }
	
	@GetMapping("/feed/{username}")
    public @ResponseBody FeedDTO feed(Model model, @PathVariable(value = "username") String username) {
		return facebookService.getFeed(model, username);
    }
}