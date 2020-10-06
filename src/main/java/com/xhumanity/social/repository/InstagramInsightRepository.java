package com.xhumanity.social.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.xhumanity.social.model.InstagramInsight;

public interface InstagramInsightRepository  extends JpaRepository<InstagramInsight, Integer> {

	Optional<InstagramInsight> findByMediaUrl(@Param("mediaUrl") String mediaUrl);

}
