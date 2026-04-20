package com.LeetForce.Service;

import com.LeetForce.Entity.ProblemEntity;
import com.LeetForce.Entity.Enums.Difficulty;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ProblemRecommendationService {

    public int getProblemGapInDays(ProblemEntity problemEntity) {
        if (problemEntity == null) {
            throw new IllegalArgumentException("Problem recommendation must not be null");
        }

        LocalDateTime solvedAt = problemEntity.getSolvedAt();
        if (solvedAt == null) {
            return 0;
        }

        long gap = ChronoUnit.DAYS.between(solvedAt.toLocalDate(), LocalDate.now());
        return (int) Math.max(gap, 0);
    }

    public int getProblemDifficulty(ProblemEntity problemEntity) {
        return problemEntity.getDifficulty() == Difficulty.EASY ? 1 :
                problemEntity.getDifficulty() == Difficulty.MEDIUM ? 2 : 3;
    }

}
