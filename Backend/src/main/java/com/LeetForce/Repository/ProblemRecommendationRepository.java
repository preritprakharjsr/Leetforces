package com.LeetForce.Repository;

import com.LeetForce.Entity.ProblemRecommendationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRecommendationRepository extends JpaRepository<ProblemRecommendationEntity, Long> {
}

