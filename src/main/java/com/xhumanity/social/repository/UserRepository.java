package com.xhumanity.social.repository;

import org.springframework.data.repository.CrudRepository;

import com.xhumanity.social.model.User;

public interface UserRepository extends CrudRepository<User, Integer> { 

}
