package com.LeetForce.DTO;

import com.LeetForce.Entity.Enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeetcodeResponseDTO {

    private UserData userData;
    private Long totalQuestions;

    // Key examples: "array", "dp", "graph", "string".
    private Map<String, Long> questionTypeCounts;

    private Map<Difficulty, Long> difficultyCounts;

    private Map<String, Long> topicCounts;

    // Raw entries mapped from activity_logs rows for LEETCODE platform.
    private List<QuestionActivity> activities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserData {
        private Long userId;
        private String username;
        private String email;
        private String lcUsername;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionActivity {
        private String problemSlug;

        // Optional classifier (can be derived from tags/topic while mapping ActivityLog).
        private String questionType;

        private Difficulty difficulty;
        private String topic;
        private LocalDateTime solvedAt;
    }
}

