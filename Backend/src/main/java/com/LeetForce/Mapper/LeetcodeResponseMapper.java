package com.LeetForce.Mapper;

import com.LeetForce.DTO.ResponseDto.LeetcodeResponseDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class LeetcodeResponseMapper {

	public LeetcodeResponseDto mapToLeetcodeResponseDto(String jsonResponse) {
		if (jsonResponse == null || jsonResponse.isBlank()) {
			throw new IllegalArgumentException("JSON response must not be blank");
		}

		try {
			Map<String, Object> data = parseJson(jsonResponse);
			Map<String, Object> matchedUser = asMap(getNestedValue(data, "data", "matchedUser"));

			if (matchedUser.isEmpty()) {
				throw new IllegalArgumentException("No user data found in response");
			}

			String username = asString(matchedUser.get("username"));
			Map<String, Long> difficultyCounts = extractDifficultyCounts(matchedUser);
			List<LeetcodeResponseDto.RecentQuestion> recentQuestions = extractRecentQuestions(matchedUser);

			return LeetcodeResponseDto.builder()
					.username(username)
					.easyQuestions(difficultyCounts.getOrDefault("EASY", 0L))
					.mediumQuestion(difficultyCounts.getOrDefault("MEDIUM", 0L))
					.hardQuestion(difficultyCounts.getOrDefault("HARD", 0L))
					.recentQuestion(recentQuestions)
					.totalActiveDays(0)
					.totalCompetition(0)
					.competitionRank(0)
					.build();
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to map JSON response to LeetcodeResponseDto", ex);
		}
	}

	private Map<String, Long> extractDifficultyCounts(Map<String, Object> matchedUser) {
		Map<String, Long> counts = new HashMap<>();
		counts.put("EASY", 0L);
		counts.put("MEDIUM", 0L);
		counts.put("HARD", 0L);


		Map<String, Object> submitStatsGlobal = asMap(matchedUser.get("submitStatsGlobal"));
		List<Map<String, Object>> acSubmissionNum = asMapList(submitStatsGlobal.get("acSubmissionNum"));

		for (Map<String, Object> item : acSubmissionNum) {
			String difficulty = asString(item.get("difficulty")).toUpperCase();
			long count = asLong(item.get("count"));
			if (counts.containsKey(difficulty)) {
				counts.put(difficulty, count);
			}
		}

		return counts;
	}

	private List<LeetcodeResponseDto.RecentQuestion> extractRecentQuestions(Map<String, Object> matchedUser) {
		List<LeetcodeResponseDto.RecentQuestion> recentQuestions = new ArrayList<>();
		List<Map<String, Object>> recentAcSubmissionList = asMapList(matchedUser.get("recentAcSubmissionList"));

		for (Map<String, Object> submission : recentAcSubmissionList) {
			String titleSlug = asString(submission.get("titleSlug"));
			String title = asString(submission.get("title"));
			String timestamp = asString(submission.get("timestamp"));

			if (titleSlug.isBlank()) {
				continue;
			}

			LeetcodeResponseDto.RecentQuestion recentQuestion = LeetcodeResponseDto.RecentQuestion.builder()
					.name(titleSlug)
					.title(title)
					.topic("")
					.difficulty("")
					.timestamp(epochToDateTime(timestamp))
					.build();

			recentQuestions.add(recentQuestion);
		}

		return recentQuestions;
	}

	private Map<String, Object> parseJson(String json) {
		Map<String, Object> result = new HashMap<>();
		String trimmed = json.trim();
		if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
			result = parseJsonObject(trimmed.substring(1, trimmed.length() - 1));
		}
		return result;
	}

	private Map<String, Object> parseJsonObject(String jsonContent) {
		Map<String, Object> result = new HashMap<>();
		int braceCount = 0;
		int bracketCount = 0;
		StringBuilder currentKey = new StringBuilder();
		StringBuilder currentValue = new StringBuilder();
		boolean inString = false;
		boolean readingKey = true;

		for (int i = 0; i < jsonContent.length(); i++) {
			char c = jsonContent.charAt(i);

			if (c == '"' && (i == 0 || jsonContent.charAt(i - 1) != '\\')) {
				inString = !inString;
			}

			if (!inString) {
				if (c == ':' && readingKey) {
					readingKey = false;
					continue;
				} else if (c == ',' && braceCount == 0 && bracketCount == 0) {
					String key = cleanKey(currentKey.toString());
					Object value = parseValue(currentValue.toString());
					result.put(key, value);
					currentKey.setLength(0);
					currentValue.setLength(0);
					readingKey = true;
					continue;
				} else if (c == '{') {
					braceCount++;
				} else if (c == '}') {
					braceCount--;
				} else if (c == '[') {
					bracketCount++;
				} else if (c == ']') {
					bracketCount--;
				}
			}

			if (readingKey) {
				currentKey.append(c);
			} else {
				currentValue.append(c);
			}
		}

		if (!currentKey.isEmpty()) {
			String key = cleanKey(currentKey.toString());
			Object value = parseValue(currentValue.toString());
			result.put(key, value);
		}

		return result;
	}

	private Object parseValue(String value) {
		String trimmed = value.trim();

		if (trimmed.isEmpty()) {
			return null;
		}

		if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
			return trimmed.substring(1, trimmed.length() - 1)
					.replace("\\\"", "\"")
					.replace("\\n", "\n")
					.replace("\\r", "\r");
		}

		if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
			return parseJsonObject(trimmed.substring(1, trimmed.length() - 1));
		}

		if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
			return parseJsonArray(trimmed.substring(1, trimmed.length() - 1));
		}

		if ("true".equalsIgnoreCase(trimmed)) {
			return true;
		}
		if ("false".equalsIgnoreCase(trimmed)) {
			return false;
		}
		if ("null".equalsIgnoreCase(trimmed)) {
			return null;
		}

		try {
			if (trimmed.contains(".")) {
				return Double.parseDouble(trimmed);
			}
			return Long.parseLong(trimmed);
		} catch (NumberFormatException ex) {
			return trimmed;
		}
	}

	private List<Object> parseJsonArray(String arrayContent) {
		List<Object> result = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		int braceCount = 0;
		int bracketCount = 0;
		boolean inString = false;

		for (int i = 0; i < arrayContent.length(); i++) {
			char c = arrayContent.charAt(i);

			if (c == '"' && (i == 0 || arrayContent.charAt(i - 1) != '\\')) {
				inString = !inString;
			}

			if (!inString) {
				if (c == '{') braceCount++;
				else if (c == '}') braceCount--;
				else if (c == '[') bracketCount++;
				else if (c == ']') bracketCount--;
				else if (c == ',' && braceCount == 0 && bracketCount == 0) {
					result.add(parseValue(current.toString()));
					current.setLength(0);
					continue;
				}
			}
			current.append(c);
		}

		if (!current.isEmpty()) {
			result.add(parseValue(current.toString()));
		}

		return result;
	}

	private String cleanKey(String key) {
		return key.trim().replaceAll("^\"|\"$", "");
	}

	private Object getNestedValue(Map<String, Object> map, String... keys) {
		Object current = map;
		for (String key : keys) {
			if (current instanceof Map<?, ?>) {
				current = ((Map<?, ?>) current).get(key);
			} else {
				return null;
			}
		}
		return current;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> asMap(Object value) {
		if (value instanceof Map<?, ?> map) {
			return (Map<String, Object>) map;
		}
		return new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> asMapList(Object value) {
		if (value instanceof List<?> list) {
			List<Map<String, Object>> result = new ArrayList<>();
			for (Object item : list) {
				if (item instanceof Map<?, ?> map) {
					result.add((Map<String, Object>) map);
				}
			}
			return result;
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
}