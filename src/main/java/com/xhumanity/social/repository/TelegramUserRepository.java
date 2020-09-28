package com.xhumanity.social.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xhumanity.social.model.TelegramUser;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
	
	Optional<TelegramUser> findByUsername(@Param("username") final String username);
	
	Optional<TelegramUser> findByChatId(@Param("chatId") final Long chatId);
	
	@Query("SELECT u FROM TelegramUser u WHERE u.chatId = :chatId AND u.forumUserId is not null")
	Optional<TelegramUser> findByChatIdAndForumuserIdNotNull(@Param("chatId") long chatId);
	
	Optional<TelegramUser> findByForumEmail(@Param("forumEmail") String forumEmail);
}

