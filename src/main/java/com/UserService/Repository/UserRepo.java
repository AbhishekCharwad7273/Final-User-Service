package com.UserService.Repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.UserService.Entity.User;

public interface UserRepo extends JpaRepository<User,Long> {
	
	  @Query("select u from User u where upper(u.name) like upper(concat('%', ?1, '%'))")
	    List<User> findByNameContainsIgnoreCaseAllIgnoreCase(String name);
	    Optional<User> findByEmailIdAndPassword(String emailId, String password);
	    Optional<User> findByEmailId(String emailId);

}
