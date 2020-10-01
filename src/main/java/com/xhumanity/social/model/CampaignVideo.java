package com.xhumanity.social.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "campaign_video")
public class CampaignVideo {

	public static final String SOURCE_YOUTUBE = "youtube";
	public static final String SOURCE_FACEBOOK = "facebook";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "user_id")
	private Integer userId;
	
	@Column(name = "campaign_id")
	private Integer campaignId;
	
	@Column(name = "source")
	private String source;
	
	@Column(name = "link")
	private String link;
	
	@Column(name = "entity_id")
	private String entityId;
	
	@Column(name = "post_url")
	private String postUrl;
}
