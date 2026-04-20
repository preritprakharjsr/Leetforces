package com.LeetForce.Service.LeetcodeServices;

import com.LeetForce.DTO.ResponseDto.LeetcodeResponseDto;
import com.LeetForce.Mapper.LeetcodeResponseMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class LeetcodeResponseService {

    private static final String LEETCODE_GRAPHQL_URL = "https://leetcode.com/graphql";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final LeetcodeResponseMapper leetcodeResponseMapper;

    public LeetcodeResponseService(LeetcodeResponseMapper leetcodeResponseMapper) {
        this.leetcodeResponseMapper = leetcodeResponseMapper;
    }

    public LeetcodeResponseDto fetchUserAndQuestionDetail(String lcUsername) {
        if (lcUsername == null || lcUsername.isBlank()) {
            throw new IllegalArgumentException("LeetCode username must not be blank");
        }

        String rawResponse = executeGraphqlQuery(Map.of("username", lcUsername));

        return leetcodeResponseMapper.mapToLeetcodeResponseDto(rawResponse);
    }

    private String executeGraphqlQuery(Map<String, Object> variables) {
        try {
            String jsonBody = buildRequestBody(LeetcodeGraphqlQueries.USER_STATS_AND_RECENT_QUERY, variables);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LEETCODE_GRAPHQL_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("LeetCode API returned status: " + response.statusCode());
            }

            return response.body();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Failed to fetch data from LeetCode GraphQL API", ex);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to fetch data from LeetCode GraphQL API", ex);
        }
    }

    private String buildRequestBody(String query, Map<String, Object> variables) {
        return "{\"query\":\"" + escapeJson(query) + "\",\"variables\":{" +
                variables.entrySet().stream()
                        .map(entry -> "\"" + escapeJson(entry.getKey()) + "\":\"" + escapeJson(asString(entry.getValue())) + "\"")
                        .reduce((left, right) -> left + "," + right)
                        .orElse("") +
                "}}";
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
