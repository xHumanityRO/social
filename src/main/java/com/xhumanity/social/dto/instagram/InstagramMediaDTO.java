package com.xhumanity.social.dto.instagram;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class InstagramMediaDTO {
	private Integer userId;
	private String mediaId;
	private String mediaUrl;
	@JsonIgnore
	private String postUrl;
}
