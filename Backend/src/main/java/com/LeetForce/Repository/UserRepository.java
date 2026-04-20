package com.LeetForce.Repository;

import com.LeetForce.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	boolean existsByUsername(String username);

	boolean existsByUsernameAndIdNot(String username, Long id);
}

