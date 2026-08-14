/**
 * file: LlmBackedSoulProfileBuilder.java
 * author: daiyinbao
 * date: 2026-08-14
 */
package com.openbiliclaw.infrastructure.llm.profile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.openbiliclaw.domain.event.UserEvent;
import com.openbiliclaw.domain.llm.LlmRequest;
import com.openbiliclaw.domain.llm.LlmService;
import com.openbiliclaw.domain.profile.InterestTag;
import com.openbiliclaw.domain.profile.PreferenceProfile;
import com.openbiliclaw.domain.profile.SoulProfile;
import com.openbiliclaw.domain.profile.SoulProfileBuilder;

public class LlmBackedSoulProfileBuilder implements SoulProfileBuilder{
    
      private final LlmService llmService;

      public LlmBackedSoulProfileBuilder(LlmService llmService) {
          this.llmService = llmService;
      }

   
      @Override
      public SoulProfile build(PreferenceProfile preferenceProfile, List<UserEvent> recentEvents) {
          Instant now = Instant.now();

          if (recentEvents == null || recentEvents.isEmpty()) {
              return new SoulProfile(
                      preferenceProfile.id(),
                      "No events yet. Profile has not been established.",
                      List.of("unknown"),
                      List.of("needs more signals"),
                      preferenceProfile.interests(),
                      preferenceProfile.dislikedTopics(),
                      now
              );
          }

          LlmRequest request = new LlmRequest(
                  buildSystemPrompt(),
                  buildUserPrompt(preferenceProfile, recentEvents),
                  "fake-profile-model",
                  0.2,
                  768
          );

          var llmResponse = llmService.complete(request);
          String llmContent = llmResponse.content();
          boolean llmCallSucceeded = llmContent != null && !llmContent.isBlank();

          String portrait = buildPortrait(preferenceProfile, llmCallSucceeded);
          List<String> coreTraits = buildCoreTraits(preferenceProfile.styleSignals(), llmCallSucceeded);
          List<String> deepNeeds = buildDeepNeeds(preferenceProfile.styleSignals(), llmCallSucceeded);

          return new SoulProfile(
                  preferenceProfile.id(),
                  portrait,
                  coreTraits,
                  deepNeeds,
                  preferenceProfile.interests(),
                  preferenceProfile.dislikedTopics(),
                  now
          );
      }

      private String buildSystemPrompt() {
          return """
                  You are a profile synthesis assistant.
                  Given a user's structured preference profile and recent events,
                  summarize the user's likely content preference style, traits, and deep needs.
                  Keep the reasoning compact and structured.
                  """;
      }

      private String buildUserPrompt(PreferenceProfile preferenceProfile, List<UserEvent> recentEvents) {
          StringBuilder builder = new StringBuilder();
          builder.append("Preference profile:\n");
          builder.append("- interests: ").append(preferenceProfile.interests()).append('\n');
          builder.append("- dislikedTopics: ").append(preferenceProfile.dislikedTopics()).append('\n');
          builder.append("- styleSignals: ").append(preferenceProfile.styleSignals()).append('\n');

          builder.append("\nRecent events:\n");
          for (UserEvent event : recentEvents) {
              builder.append("- type=").append(event.eventType());
              builder.append(", title=").append(event.title());
              builder.append(", platform=").append(event.sourcePlatform());
              builder.append(", metadata=").append(event.metadata());
              builder.append('\n');
          }

          builder.append("\nSummarize likely portrait, traits, and deep needs.");
          return builder.toString();
      }

      private String buildPortrait(PreferenceProfile preferenceProfile, boolean llmCallSucceeded) {
          String interestSummary = summarizeInterests(preferenceProfile.interests());
          Map<String, Double> signals = preferenceProfile.styleSignals();

          double noveltyPreference = signals.getOrDefault("noveltyPreference", 0.0);
          double depthPreference = signals.getOrDefault("depthPreference", 0.0);
          double intentStrength = signals.getOrDefault("intentStrength", 0.0);

          StringBuilder portrait = new StringBuilder();
          portrait.append("A user who appears to prefer ");
          portrait.append(interestSummary);
          portrait.append(".");

          if (depthPreference >= 0.70) {
              portrait.append(" They seem to value depth, structure, and explanatory clarity.");
          } else {
              portrait.append(" They appear to be building understanding from repeated technical exposure.");
          }

          if (noveltyPreference >= 0.60) {
              portrait.append(" They show active curiosity and a willingness to explore beyond passive consumption.");
          }

          if (intentStrength >= 0.70) {
              portrait.append(" Their behavior suggests high-intent learning rather than casual browsing.");
          }

          if (llmCallSucceeded) {
              portrait.append(" Their profile synthesis path is now routed through the shared model abstraction.");
          }

          return portrait.toString();
      }

      private String summarizeInterests(List<InterestTag> interests) {
          if (interests == null || interests.isEmpty()) {
              return "general technical content";
          }

          List<String> topNames = new ArrayList<>();
          for (int i = 0; i < Math.min(3, interests.size()); i++) {
              topNames.add(interests.get(i).name());
          }

          return String.join(", ", topNames);
      }

      private List<String> buildCoreTraits(Map<String, Double> signals, boolean llmCallSucceeded) {
          List<String> traits = new ArrayList<>();

          double depthPreference = signals.getOrDefault("depthPreference", 0.0);
          double noveltyPreference = signals.getOrDefault("noveltyPreference", 0.0);
          double intentStrength = signals.getOrDefault("intentStrength", 0.0);
          double consumptionDepth = signals.getOrDefault("consumptionDepth", 0.0);

          if (depthPreference >= 0.70) {
              traits.add("depth oriented");
          }
          if (noveltyPreference >= 0.60) {
              traits.add("actively exploratory");
          }
          if (intentStrength >= 0.70) {
              traits.add("high intent learner");
          }
          if (consumptionDepth >= 0.50) {
              traits.add("consistent consumer");
          }

          if (llmCallSucceeded) {
              traits.add("profile synthesis ready");
          }

          if (traits.isEmpty()) {
              traits.add("signal still forming");
          }

          return List.copyOf(traits);
      }

      private List<String> buildDeepNeeds(Map<String, Double> signals, boolean llmCallSucceeded) {
          List<String> deepNeeds = new ArrayList<>();

          double depthPreference = signals.getOrDefault("depthPreference", 0.0);
          double noveltyPreference = signals.getOrDefault("noveltyPreference", 0.0);
          double intentStrength = signals.getOrDefault("intentStrength", 0.0);

          if (depthPreference >= 0.70) {
              deepNeeds.add("needs clarity");
              deepNeeds.add("prefers deep explanation");
          }

          if (noveltyPreference >= 0.60) {
              deepNeeds.add("needs discovery");
          }

          if (intentStrength >= 0.70) {
              deepNeeds.add("needs actionable understanding");
          }

          if (llmCallSucceeded) {
              deepNeeds.add("ready for model-backed profile synthesis");
          }

          if (deepNeeds.isEmpty()) {
              deepNeeds.add("needs more stable preference signals");
          }

          return List.copyOf(deepNeeds);
      }
}
