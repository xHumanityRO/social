package com.xhumanity.social.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xhumanity.social.model.User;
import com.xhumanity.social.repository.UserRepository;

@Controller
@RequestMapping
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping(path = "/all")
	public @ResponseBody Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}
}