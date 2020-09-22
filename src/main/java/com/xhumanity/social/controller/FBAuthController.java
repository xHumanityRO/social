package com.xhumanity.social.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xhumanity.social.service.FacebookService;

@RestController
@RequestMapping("/")
public class FBAuthController {

	@Autowired
	FacebookService facebookService;

	@GetMapping("/createFacebookAuthorization")
	public String createFacebookAuthorization() {
		return facebookService.createFacebookAuthorizationURL();
	}
	
	@GetMapping("/facebook")
	public void createFacebookAccessToken(@RequestParam("code") String code, @RequestParam("username") String username) throws URISyntaxException, IOException {
	    facebookService.createFacebookAccessToken(code, username);
	}
	
	@GetMapping("/getName")
	public String getNameResponse(){
	    return facebookService.getName();
	}
	
	@GetMapping("/getPageLikes")
	public String getPageLikes(){
	    return facebookService.getPageLikes();
	}
	
}