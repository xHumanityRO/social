package com.xhumanity.social.dto.instagram;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ReputationHistoryDTO {
	
	private Integer forumUserId;
	private String forumUsername;
	private List<ReputationHistoryItemDTO> data;
}
