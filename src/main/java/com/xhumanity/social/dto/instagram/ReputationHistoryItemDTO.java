package com.xhumanity.social.dto.instagram;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ReputationHistoryItemDTO {
	
	private Long timestamp;
	private Integer reputation;
}
