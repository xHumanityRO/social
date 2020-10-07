package com.xhumanity.social.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.xhumanity.social.model.CampaignVideo;

public interface CampaignVideoRepository extends JpaRepository<CampaignVideo, Integer> {
	
	Optional<CampaignVideo> findAllByCampaignIdAndSource(@Param("campaign_id") final String campaignId, @Param("source") final String source);

	Optional<CampaignVideo> findByLink(@Param("link") String link);

	List<CampaignVideo> findAllBySource(@Param("source") String source);

	List<CampaignVideo> findAllBySourceAndUserId(String source, Integer userId);

}
