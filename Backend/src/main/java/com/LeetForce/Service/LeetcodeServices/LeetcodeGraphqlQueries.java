package com.LeetForce.Service.LeetcodeServices;

/**
 * Central place for LeetCode GraphQL query strings used by LeetcodeResponseService.
 */
public final class LeetcodeGraphqlQueries {

    private LeetcodeGraphqlQueries() {
    }

    // Maps username, easy/medium/hard solved counts, and recent solved questions.
    public static final String USER_STATS_AND_RECENT_QUERY = """
            query userStatsAndRecent($username: String!) {
              matchedUser(username: $username) {
                username
                submitStatsGlobal {
                  acSubmissionNum {
                    difficulty
                    count
                  }
                }
                recentAcSubmissionList {
                  title
                  titleSlug
                  timestamp
                }
              }
            }
            """;

    // Maps totalActiveDays, totalCompetition, and competitionRank.
    public static final String USER_ACTIVITY_AND_CONTEST_QUERY = """
            query userActivityAndContest($username: String!) {
              userProfileCalendar(username: $username) {
                totalActiveDays
              }
              userContestRanking(username: $username) {
                attendedContestsCount
                globalRanking
              }
            }
            """;

    // Maps per-question details for RecentQuestion fields.
    public static final String QUESTION_DETAILS_QUERY = """
            query questionDetails($titleSlug: String!) {
              question(titleSlug: $titleSlug) {
                title
                difficulty
                topicTags {
                  name
                }
              }
            }
            """;
}

