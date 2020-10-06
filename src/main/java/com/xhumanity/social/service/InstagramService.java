package com.xhumanity.social.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xhumanity.social.dto.instagram.CommentDTO;
import com.xhumanity.social.dto.instagram.InsightDTO;
import com.xhumanity.social.model.InstagramInsight;
import com.xhumanity.social.model.InstagramInsightComment;
import com.xhumanity.social.repository.InstagramInsightCommentRepository;
import com.xhumanity.social.repository.InstagramInsightRepository;

@Service
@Transactional
public class InstagramService {
	private static final Logger logger = LogManager.getLogger(InstagramService.class);

	@Autowired
	private InstagramInsightRepository insightRepository;
	@Autowired
	private InstagramInsightCommentRepository insightCommentRepository;

	public void saveInsights(InsightDTO insightDto) throws InternalError {
		try {
			Optional<InstagramInsight> insightOpt = insightRepository.findByMediaUrl(insightDto.getMediaUrl());
			InstagramInsight insight = insightOpt.orElse(InstagramInsight.builder().build());
			insight.setMediaUrl(insightDto.getMediaUrl());
			insight.setLikes(insightDto.getLikes());
			insight.setDislikes(insightDto.getDislikes());
			
			insight = insightRepository.save(insight);

			insightCommentRepository.deleteAllByInsightId(insight.getId());

			for (CommentDTO comment : insightDto.getComments()) {
				insightCommentRepository.save(InstagramInsightComment.builder().insightId(insight.getId())
						.commentId(comment.getCommentId()).message(comment.getMessage()).build());
			}

		} catch (Exception e) {
			logger.error(e);
			throw new InternalError();
		}
	}

}
