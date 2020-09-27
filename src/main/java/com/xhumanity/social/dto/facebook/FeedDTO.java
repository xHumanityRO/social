package com.xhumanity.social.dto.facebook;

import java.util.ArrayList;
import java.util.List;

public class FeedDTO {
	private String firstName;
	private String lastName;
	private List<PostDTO> posts = new ArrayList<>();

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<PostDTO> getPosts() {
		return posts;
	}

	public void setPosts(List<PostDTO> posts) {
		this.posts = posts;
	}

}
