package com.xhumanity.social.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.xhumanity.social.service.FacebookService;

@RestController
@RequestMapping("/")
public class FBAuthController {
	private static final Logger logger = LogManager.getLogger(FBAuthController.class);

	@Autowired
	FacebookService facebookService;

	@GetMapping("/createFacebookAuthorization/{forumUserId}")
	public RedirectView createFacebookAuthorization(@PathVariable(value = "forumUserId") Integer forumUserId) {
		String fbAuthorizationUrl = facebookService.createFacebookAuthorizationURL(forumUserId);
		logger.info("authorization url: " + fbAuthorizationUrl);
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl(fbAuthorizationUrl);
		return redirectView;
	}

	@GetMapping("/facebook")
	public RedirectView createFacebookAccessToken(Model model, @RequestParam("state") Integer forumUserId,
			@RequestParam("code") String code) throws URISyntaxException, IOException {
		facebookService.createFacebookAccessToken(forumUserId, code);
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("https://webapp.xhumanity.org/social/posts/" + forumUserId);
		return redirectView;
	}

	@GetMapping("/getName")
	public String getNameResponse() {
		return facebookService.getName();
	}

	@GetMapping("/getPageLikes")
	public String getPageLikes() {
		return facebookService.getPageLikes();
	}

}