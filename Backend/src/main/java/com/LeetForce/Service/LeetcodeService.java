package com.LeetForce.Service;

import com.LeetForce.DTO.LeetcodeResponseDTO;
import com.LeetForce.mapper.LeetcodeResponseMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class LeetcodeService {

    private static final String USER_QUERY = """
            query userData($username: String!) {
              matchedUser(username: $username) {
                username
                profile {
                  realName
                }
                submitStatsGlobal {
                  acSubmissionNum {
                    difficulty
                    count
                  }
                }
                tagProblemCounts {
                  fundamental {
                    tagName
                    problemsSolved
                  }
                  intermediate {
                    tagName
                    problemsSolved
                  }
                  advanced {
                    tagName
                    problemsSolved
                  }
                }
                recentAcSubmissionList {
                  titleSlug
                  timestamp
                }
              }
            }
            """;

    private WebClient webClient;
    private LeetcodeResponseMapper leetcodeResponseMapper;

    public LeetcodeResponseDTO fetchData(String lcUsername) {
        Map<String, Object> variables = Map.of("username", lcUsername);
        Map<String, Object> requestBody = Map.of("query", USER_QUERY, "variables", variables);

        Map<String, Object> response = webClient.post()
                .uri("/graphql")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null) {
            throw new IllegalStateException("Empty response received from LeetCode API");
        }

        Map<String, Object> data = asMap(response.get("data"));
        Map<String, Object> matchedUser = asMap(data.get("matchedUser"));

        if (matchedUser.isEmpty()) {
            throw new IllegalArgumentException("LeetCode user not found: " + lcUsername);
        }

        return leetcodeResponseMapper.toLeetcodeResponse(matchedUser, lcUsername);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }
}
