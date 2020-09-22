package com.xhumanity.social.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.xhumanity.social.model.ForumUser;

public interface UserRepository extends CrudRepository<ForumUser, Integer> {
	
	Optional<ForumUser> findByUsername(@Param("username") final String username);
}

