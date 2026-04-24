package com.LeetForce.Repository;

import com.LeetForce.Entity.ProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemRecommendationRepository extends JpaRepository<ProblemEntity, Long> {

	List<ProblemEntity> findTop10ByOrderByPriorityDesc();
}

