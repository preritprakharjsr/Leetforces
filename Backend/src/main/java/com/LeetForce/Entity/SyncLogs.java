package com.LeetForce.Entity;

import com.LeetForce.Entity.Enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "sync_logs" ,
        indexes = {
        @Index(name = "idx_sync_logs_user_id", columnList = "user_id"),
                })
public class SyncLogs {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Builder.Default
    private LocalDateTime syncAt = LocalDateTime.now();
}
