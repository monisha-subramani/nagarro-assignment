package com.users.dao;

import java.util.Optional;

import org.jboss.logging.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.users.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{

	//Optional<User> findByToken(String token);
	
	@Query(value = "SELECT * FROM USER u where u.token = '54a7b190-17be-45b5-9746-08f586eeb89d'",nativeQuery = true)
	User findByToken(String token);
	
	
	 //@Query(value = "SELECT * FROM USER where user = 'user'", nativeQuery = true)

}
