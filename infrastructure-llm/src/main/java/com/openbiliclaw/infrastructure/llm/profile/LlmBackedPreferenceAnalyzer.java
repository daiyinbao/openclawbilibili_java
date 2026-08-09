/**
 * file: LlmBackedPreferenceAnalyzer.java
 * author: daiyinbao
 * date: 2026-08-09
 */
package com.openbiliclaw.infrastructure.llm.profile;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.openbiliclaw.domain.event.EventType;
import com.openbiliclaw.domain.event.UserEvent;
import com.openbiliclaw.domain.llm.LlmRequest;
import com.openbiliclaw.domain.llm.LlmService;
import com.openbiliclaw.domain.profile.InterestTag;
import com.openbiliclaw.domain.profile.PreferenceAnalyzer;
import com.openbiliclaw.domain.profile.PreferenceProfile;
import com.openbiliclaw.domain.profile.ProfileId;

public class LlmBackedPreferenceAnalyzer implements PreferenceAnalyzer{

      private static final ProfileId DEFAULT_PROFILE_ID = new ProfileId(1L);

      private final LlmService llmService;

      public LlmBackedPreferenceAnalyzer(LlmService llmService) {
          this.llmService = llmService;
      }

    @Override
    public PreferenceProfile analyze(List<UserEvent> events) {

          Instant now = Instant.now();

          if (events == null || events.isEmpty()) {
              return new PreferenceProfile(
                      DEFAULT_PROFILE_ID,
                      List.of(),
                      List.of(),
                      Map.of(
                              "depthPreference", 0.0,
                              "noveltyPreference", 0.0,
                              "intentStrength", 0.0,
                              "consumptionDepth", 0.0
                      ),
                      now
              );
          }

        
// 这一步先让真实的 LLM 调用路径成立。
          // 即使当前 FakeLlmProvider 不返回真正可解析的业务结果，
          // analyzer 也已经开始依赖统一的 LlmService，而不是硬编码 provider。
          LlmRequest request = new LlmRequest(
                  buildSystemPrompt(),
                  buildUserPrompt(events),
                  "fake-profile-model",
                  0.2,
                  512
          );

          var llmResponse = llmService.complete(request);

          // 当前阶段并不依赖 fake provider 的内容做真正业务决策，
          // 但我们保留这次调用结果，后续切换到真实 provider 时，
          // 这里会成为解析 structured output 的位置。
          String llmContent = llmResponse.content();

          int viewCount = 0;
          int searchCount = 0;
          int likeCount = 0;
          int favoriteCount = 0;

          Map<String, InterestTag> interestMap = new LinkedHashMap<>();
          Map<String, InterestTag> dislikedMap = new LinkedHashMap<>();

          for (UserEvent event : events) {
              if (event.eventType() == EventType.VIEW) {
                  viewCount++;
              } else if (event.eventType() == EventType.SEARCH) {
                  searchCount++;
              } else if (event.eventType() == EventType.LIKE) {
                  likeCount++;
              } else if (event.eventType() == EventType.FAVORITE) {
                  favoriteCount++;
              }

              String text = buildSearchableText(event);
              detectInterestTags(text, interestMap);
              detectDislikedTags(text, event.metadata(), dislikedMap);
          }

          if (interestMap.isEmpty()) {
              interestMap.put(
                      "general technical content",
                      new InterestTag("general technical content", "technology", 0.55)
              );
          }

          // 让 fake LLM 调用对结果有极轻微影响，证明链路已接入，
          // 但不让它主导业务输出，避免当前 fake provider 破坏结构。
          boolean llmCallSucceeded = llmContent != null && !llmContent.isBlank();

          double depthPreference = clamp01(0.40 + favoriteCount * 0.15 + likeCount * 0.08 + (llmCallSucceeded ? 0.02 : 0.0));
          double noveltyPreference = clamp01(0.25 + searchCount * 0.12);
          double intentStrength = clamp01(0.30 + favoriteCount * 0.20 + likeCount * 0.10);
          double consumptionDepth = clamp01(0.20 + viewCount * 0.05 + favoriteCount * 0.10);

          return new PreferenceProfile(
                  DEFAULT_PROFILE_ID,
                  List.copyOf(interestMap.values()),
                  List.copyOf(dislikedMap.values()),
                  Map.of(
                          "depthPreference", depthPreference,
                          "noveltyPreference", noveltyPreference,
                          "intentStrength", intentStrength,
                          "consumptionDepth", consumptionDepth
                  ),
                  now
          );
    }

    
    private String buildSystemPrompt() {
          return """
                  You are a preference analysis assistant.
                  Read user behavior events and infer stable preference structure.
                  Focus on interests, dislike signals, learning depth, and exploration tendency.
                  Return concise structured reasoning.
                  """;
      }


    private String buildUserPrompt(List<UserEvent> events) {
          StringBuilder builder = new StringBuilder();
          builder.append("Analyze the following user events:\n");

          for (UserEvent event : events) {
              builder.append("- type=").append(event.eventType());
              builder.append(", title=").append(event.title());
              builder.append(", url=").append(event.url());
              builder.append(", platform=").append(event.sourcePlatform());
              builder.append(", metadata=").append(event.metadata());
              builder.append('\n');
          }

          builder.append("\nSummarize likely preferences and signals.");
          return builder.toString();
      }

      private String buildSearchableText(UserEvent event) {
          StringBuilder builder = new StringBuilder();

          if (event.title() != null) {
              builder.append(event.title()).append(' ');
          }

          if (event.metadata() != null) {
              for (Map.Entry<String, String> entry : event.metadata().entrySet()) {
                  if (entry.getValue() != null) {
                      builder.append(entry.getValue()).append(' ');
                  }
              }
          }

          return builder.toString().toLowerCase(Locale.ROOT);
      }

      private void detectInterestTags(String text, Map<String, InterestTag> interestMap) {
          if (containsAny(text, "architecture", "system design", "microservice", "distributed")) {
              interestMap.put(
                      "architecture",
                      new InterestTag("architecture", "technology", 0.90)
              );
          }

          if (containsAny(text, "system", "design", "engineering")) {
              interestMap.put(
                      "system design",
                      new InterestTag("system design", "technology", 0.85)
              );
          }

          if (containsAny(text, "java", "spring", "spring boot")) {
              interestMap.put(
                      "java backend",
                      new InterestTag("java backend", "technology", 0.88)
              );
          }

          if (containsAny(text, "database", "sql", "sqlite", "mysql", "postgres")) {
              interestMap.put(
                      "data storage",
                      new InterestTag("data storage", "technology", 0.78)
              );
          }

          if (containsAny(text, "api", "rest", "http", "interface")) {
              interestMap.put(
                      "service interface design",
                      new InterestTag("service interface design", "technology", 0.72)
              );
          }
      }

      private void detectDislikedTags(
              String text,
              Map<String, String> metadata,
              Map<String, InterestTag> dislikedMap
      ) {
          if (containsAny(text, "clickbait", "shorts", "营销", "震惊")) {
              dislikedMap.put(
                      "shallow attention content",
                      new InterestTag("shallow attention content", "avoidance", 0.75)
              );
          }

          if (metadata == null) {
              return;
          }

          String feedback = metadata.get("feedback");
          if (feedback != null && feedback.equalsIgnoreCase("dislike")) {
              dislikedMap.put(
                      "explicitly disliked content pattern",
                      new InterestTag("explicitly disliked content pattern", "avoidance", 0.90)
              );
          }
      }

      private boolean containsAny(String text, String... keywords) {
          for (String keyword : keywords) {
              if (text.contains(keyword.toLowerCase(Locale.ROOT))) {
                  return true;
              }
          }
          return false;
      }

      private double clamp01(double value) {
          return Math.max(0.0, Math.min(1.0, value));
      }
}
