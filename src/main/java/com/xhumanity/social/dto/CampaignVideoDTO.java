package com.xhumanity.social.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampaignVideoDTO {
	private Integer userId;
	private String videoUrl;
	@JsonIgnore
	private String postUrl;
}
