package com.LeetForce.DTO.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeetcodeResponseDto {

    private String username;
    private Long easyQuestions;
    private Long mediumQuestion;
    private Long hardQuestion;
    private List<RecentQuestion> recentQuestion;
    private int totalActiveDays;
    private int totalCompetition;
    private int competitionRank;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentQuestion {
        private String name;
        private String title;
        private String topic;
        private String difficulty;
        private LocalDateTime timestamp;
    }
}
