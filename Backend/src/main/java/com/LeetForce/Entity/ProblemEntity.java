package com.LeetForce.Entity;

import com.LeetForce.Entity.Enums.Difficulty;
import com.LeetForce.Entity.Enums.RecomendationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "problem_recommendations")
public class ProblemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String problemSlug;

    private String title;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private String topic;

    private LocalDateTime solvedAt;

    @Column(nullable = false)
    private int priority = 1;

    @Enumerated(EnumType.STRING)
    private RecomendationStatus recomendationStatus;

    @PrePersist
    @PreUpdate
    protected void refreshPriority() {
        long daysSinceLastSolved = 0L;
        if (solvedAt != null) {
            daysSinceLastSolved = Math.max(ChronoUnit.DAYS.between(solvedAt.toLocalDate(), LocalDate.now()), 0L);
        }

        double difficultyValue = mapDifficultyValue(difficulty);
        double calculatedPriority = (2.0 * daysSinceLastSolved) + (1.5 * difficultyValue);
        priority = (int) Math.max(Math.round(calculatedPriority), 1);
    }

    private double mapDifficultyValue(Difficulty difficulty) {
        if (difficulty == null) {
            return 0.0;
        }

        return switch (difficulty) {
            case EASY -> 1.0;
            case MEDIUM -> 2.0;
            case HARD -> 3.0;
        };
    }
}
