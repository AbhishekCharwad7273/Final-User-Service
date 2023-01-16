package com.UserService.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.UserService.Entity.Subscription;

public interface SubscribeRepo extends JpaRepository<Subscription,Long> {

}
