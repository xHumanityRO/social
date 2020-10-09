package com.xhumanity.social.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xhumanity.social.model.InstagramInsight;
import com.xhumanity.social.projection.ReputationProjection;

public interface InstagramInsightRepository  extends JpaRepository<InstagramInsight, Integer> {

	Optional<InstagramInsight> findByMediaUrl(@Param("mediaUrl") String mediaUrl);

	@Query(value = "select DATE(creation_time) as day, sum(a.likes - a.dislikes + (\n" + 
			"			select count(*) from instagram_insight_comment b where b.insight_id = a.id)) as reputation \n" + 
			"	from instagram_insight a \n" + 
			"	  inner join campaign_video c on a.media_url = c.link\n" + 
			"	  inner join telegram_users t on c.user_id = t.id AND t.forum_user_id = ?1\n" + 
			"	where a.id in (select max(ii.id) from instagram_insight ii group by media_url, DATE(creation_time))\n" + 
			"	group by DATE(creation_time)\n" + 
			"	order by DATE(creation_time)", nativeQuery = true)
    List<ReputationProjection> getReputation(Integer forumUserId);

}
