package com.xhumanity.social.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xhumanity.social.service.FacebookService;

@Controller
@RequestMapping("/")
public class FBOperationsController {

	@Autowired
	FacebookService facebookService;

	@GetMapping("/feed")
    public String feed(Model model) {
		return facebookService.getFeed(model);
    }
}