package com.LeetForce.Entity;

import com.LeetForce.Entity.Enums.Roles;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles role = Roles.USER;

    @Column(length = 50)
    private String lcUsername;

    @Column(length = 50)
    private String cfHandle;

    @Column(length = 255)
    private String lcProfileUrl;

    @Column(length = 255)
    private String cfProfileUrl;

    @Column(length = 500)
    private String selfDescription;

    @Builder.Default
    private int xp = 0;

    @Builder.Default
    private int level = 1;

    @Builder.Default
    private int eloRating = 1200;

    @Builder.Default
    private int streak = 0;

    private LocalDate lastSolvedDate;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastSyncedAt;

    // Optional: auto-set createdAt if not set
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}