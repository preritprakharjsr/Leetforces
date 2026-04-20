package com.LeetForce.Entity;

import com.LeetForce.Entity.Enums.Difficulty;
import com.LeetForce.Entity.Enums.RecomendationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    private RecomendationStatus recomendationStatus;
}
