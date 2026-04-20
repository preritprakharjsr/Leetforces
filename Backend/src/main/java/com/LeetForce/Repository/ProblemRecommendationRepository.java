package com.LeetForce.Repository;

import com.LeetForce.Entity.ProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRecommendationRepository extends JpaRepository<ProblemEntity, Long> {
}

