package com.xhumanity.social.dto.instagram;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class InsightDTO {

	private String mediaId;
	private String mediaUrl;
	private Integer likes;
	private Integer dislikes;
	private List<CommentDTO> comments;
}
