package com.xhumanity.social.dto.forum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class PostDTO {
	
	private Integer postId;
	private String message;
	@JsonProperty("URL")
	private String URL;
	private Integer likeCount;
	private Integer dislikeCount;

}
