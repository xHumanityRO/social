package com.xhumanity.social.dto.instagram;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class CommentDTO {

	private Long commentId;
	private String message;
}
