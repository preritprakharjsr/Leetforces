package com.LeetForce.Service;

import com.LeetForce.Entity.ProblemEntity;
import com.LeetForce.Repository.ProblemRecommendationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ProblemRecommendationService {

    private final ProblemRecommendationRepository problemRecommendationRepository;

    public ProblemRecommendationService(ProblemRecommendationRepository problemRecommendationRepository) {
        this.problemRecommendationRepository = problemRecommendationRepository;
    }

    public List<ProblemEntity> fetchTop10HighestPriorityProblems() {
        return problemRecommendationRepository.findTop10ByOrderByPriorityDesc();
    }

    public List<ProblemEntity> fetchProblems() {
        return problemRecommendationRepository.findTop10ByOrderByPriorityDesc();
    }
}
