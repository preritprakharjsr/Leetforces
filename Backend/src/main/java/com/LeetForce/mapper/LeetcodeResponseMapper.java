package com.LeetForce.mapper;

import com.LeetForce.DTO.LeetcodeResponseDTO;
import com.LeetForce.Entity.Enums.Difficulty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class LeetcodeResponseMapper {

    private static final String QUESTION_META_QUERY = """
            query questionMeta($titleSlug: String!) {
              question(titleSlug: $titleSlug) {
                difficulty
                topicTags {
                  name
                }
              }
            }
            """;

    private final WebClient webClient;

    public LeetcodeResponseMapper(WebClient webClient) {
        this.webClient = webClient;
    }

    public LeetcodeResponseDTO toLeetcodeResponse(Map<String, Object> matchedUser, String lcUsername) {
        LeetcodeResponseDTO.UserData userData = mapUserData(matchedUser, lcUsername);

        Map<Difficulty, Long> difficultyCounts = mapDifficultyCounts(matchedUser);
        long totalQuestions = mapTotalQuestions(matchedUser, difficultyCounts);

        Map<String, Object> tagProblemCounts = asMap(matchedUser.get("tagProblemCounts"));
        Map<String, Long> questionTypeCounts = mapQuestionTypeCounts(tagProblemCounts);
        Map<String, Long> topicCounts = mapTopicCounts(tagProblemCounts);
        Map<String, String> topicToType = buildTopicToTypeMap(tagProblemCounts);

        List<LeetcodeResponseDTO.QuestionActivity> activities = mapActivities(matchedUser, topicToType);

        return LeetcodeResponseDTO.builder()
                .userData(userData)
                .totalQuestions(totalQuestions)
                .questionTypeCounts(questionTypeCounts)
                .difficultyCounts(difficultyCounts)
                .topicCounts(topicCounts)
                .activities(activities)
                .build();
    }

    private LeetcodeResponseDTO.UserData mapUserData(Map<String, Object> matchedUser, String lcUsername) {
        Map<String, Object> profile = asMap(matchedUser.get("profile"));
        String realName = asString(profile.get("realName"));
        String username = !realName.isBlank() ? realName : asString(matchedUser.get("username"));

        return LeetcodeResponseDTO.UserData.builder()
                .userId(null)
                .username(username)
                .email(null)
                .lcUsername(lcUsername)
                .build();
    }

    private Map<Difficulty, Long> mapDifficultyCounts(Map<String, Object> matchedUser) {
        Map<Difficulty, Long> counts = new LinkedHashMap<>();
        counts.put(Difficulty.EASY, 0L);
        counts.put(Difficulty.MEDIUM, 0L);
        counts.put(Difficulty.HARD, 0L);

        Map<String, Object> submitStatsGlobal = asMap(matchedUser.get("submitStatsGlobal"));
        List<Map<String, Object>> acSubmissionNum = asMapList(submitStatsGlobal.get("acSubmissionNum"));

        for (Map<String, Object> item : acSubmissionNum) {
            String difficulty = asString(item.get("difficulty")).toUpperCase();
            long count = asLong(item.get("count"));
            switch (difficulty) {
                case "EASY" -> counts.put(Difficulty.EASY, count);
                case "MEDIUM" -> counts.put(Difficulty.MEDIUM, count);
                case "HARD" -> counts.put(Difficulty.HARD, count);
                default -> {
                }
            }
        }
        return counts;
    }

    private long mapTotalQuestions(Map<String, Object> matchedUser, Map<Difficulty, Long> difficultyCounts) {
        Map<String, Object> submitStatsGlobal = asMap(matchedUser.get("submitStatsGlobal"));
        List<Map<String, Object>> acSubmissionNum = asMapList(submitStatsGlobal.get("acSubmissionNum"));

        for (Map<String, Object> item : acSubmissionNum) {
            if ("ALL".equalsIgnoreCase(asString(item.get("difficulty")))) {
                return asLong(item.get("count"));
            }
        }

        return difficultyCounts.values().stream().mapToLong(Long::longValue).sum();
    }

    private Map<String, Long> mapQuestionTypeCounts(Map<String, Object> tagProblemCounts) {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("fundamental", sumProblems(asMapList(tagProblemCounts.get("fundamental"))));
        result.put("intermediate", sumProblems(asMapList(tagProblemCounts.get("intermediate"))));
        result.put("advanced", sumProblems(asMapList(tagProblemCounts.get("advanced"))));
        return result;
    }

    private Map<String, Long> mapTopicCounts(Map<String, Object> tagProblemCounts) {
        Map<String, Long> topics = new LinkedHashMap<>();
        mergeTopics(topics, asMapList(tagProblemCounts.get("fundamental")));
        mergeTopics(topics, asMapList(tagProblemCounts.get("intermediate")));
        mergeTopics(topics, asMapList(tagProblemCounts.get("advanced")));
        return topics;
    }

    private void mergeTopics(Map<String, Long> target, List<Map<String, Object>> source) {
        for (Map<String, Object> item : source) {
            String tagName = asString(item.get("tagName"));
            long solved = asLong(item.get("problemsSolved"));
            if (!tagName.isBlank()) {
                target.merge(tagName, solved, Long::sum);
            }
        }
    }

    private Map<String, String> buildTopicToTypeMap(Map<String, Object> tagProblemCounts) {
        Map<String, String> topicToType = new HashMap<>();
        addTopicType(topicToType, asMapList(tagProblemCounts.get("fundamental")), "fundamental");
        addTopicType(topicToType, asMapList(tagProblemCounts.get("intermediate")), "intermediate");
        addTopicType(topicToType, asMapList(tagProblemCounts.get("advanced")), "advanced");
        return topicToType;
    }

    private void addTopicType(Map<String, String> map, List<Map<String, Object>> tags, String type) {
        for (Map<String, Object> tag : tags) {
            String tagName = asString(tag.get("tagName"));
            if (!tagName.isBlank()) {
                map.putIfAbsent(tagName.toLowerCase(), type);
            }
        }
    }

    private List<LeetcodeResponseDTO.QuestionActivity> mapActivities(Map<String, Object> matchedUser, Map<String, String> topicToType) {
        List<Map<String, Object>> recentSubmissions = asMapList(matchedUser.get("recentAcSubmissionList"));
        List<LeetcodeResponseDTO.QuestionActivity> activities = new ArrayList<>();

        for (Map<String, Object> submission : recentSubmissions) {
            String slug = asString(submission.get("titleSlug"));
            if (slug.isBlank()) {
                continue;
            }

            QuestionMeta meta = fetchQuestionMeta(slug, topicToType);

            activities.add(LeetcodeResponseDTO.QuestionActivity.builder()
                    .problemSlug(slug)
                    .questionType(meta.questionType())
                    .difficulty(meta.difficulty())
                    .topic(meta.topic())
                    .solvedAt(epochToDateTime(asString(submission.get("timestamp"))))
                    .build());
        }

        return activities;
    }

    private QuestionMeta fetchQuestionMeta(String slug, Map<String, String> topicToType) {
        Map<String, Object> variables = Map.of("titleSlug", slug);
        Map<String, Object> requestBody = Map.of("query", QUESTION_META_QUERY, "variables", variables);

        Map<String, Object> response = webClient.post()
                .uri("/graphql")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Map<String, Object> data = asMap(response == null ? null : response.get("data"));
        Map<String, Object> question = asMap(data.get("question"));

        Difficulty difficulty = mapDifficultyValue(asString(question.get("difficulty")));
        List<Map<String, Object>> topicTags = asMapList(question.get("topicTags"));
        String topic = topicTags.isEmpty() ? "" : asString(topicTags.get(0).get("name"));

        String type = "general";
        if (!topic.isBlank()) {
            type = topicToType.getOrDefault(topic.toLowerCase(), "general");
        }

        return new QuestionMeta(difficulty, topic, type);
    }

    private Difficulty mapDifficultyValue(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return switch (raw.toUpperCase()) {
            case "EASY" -> Difficulty.EASY;
            case "MEDIUM" -> Difficulty.MEDIUM;
            case "HARD" -> Difficulty.HARD;
            default -> null;
        };
    }

    private long sumProblems(List<Map<String, Object>> tags) {
        long sum = 0L;
        for (Map<String, Object> tag : tags) {
            sum += asLong(tag.get("problemsSolved"));
        }
        return sum;
    }

    private LocalDateTime epochToDateTime(String epochSeconds) {
        if (epochSeconds == null || epochSeconds.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(epochSeconds)), ZoneOffset.UTC);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> asMapList(Object value) {
        if (value instanceof List<?> list) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .filter(item -> item instanceof Map)
                    .map(item -> (Map<String, Object>) item)
                    .toList();
        }
        return List.of();
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(asString(value));
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    private record QuestionMeta(Difficulty difficulty, String topic, String questionType) {
    }
}

