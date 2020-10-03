package com.xhumanity.social.dto.facebook;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class FeedDTO {
	private String firstName;
	private String lastName;
	private List<PostDTO> posts = new ArrayList<>();
}
