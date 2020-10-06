package com.xhumanity.social.dto.instagram;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class AuthenticationDTO {
	private Integer userId;
	private String token;
	private Long validity;
}
