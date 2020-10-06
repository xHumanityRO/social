package com.xhumanity.social.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.xhumanity.social.model.InstagramInsightComment;

public interface InstagramInsightCommentRepository  extends JpaRepository<InstagramInsightComment, Integer> {

	Optional<InstagramInsightComment> findByInsightId(@Param("insightId") Integer insightId);
	
	void deleteAllByInsightId(Integer id);
}
