/**
 * file: StubSoulProfileBuilder.java
 * author: daiyinbao
 * date: 2026-08-08
 */
package com.openbiliclaw.infrastructure.llm.profile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.openbiliclaw.domain.event.UserEvent;
import com.openbiliclaw.domain.profile.InterestTag;
import com.openbiliclaw.domain.profile.PreferenceProfile;
import com.openbiliclaw.domain.profile.SoulProfile;
import com.openbiliclaw.domain.profile.SoulProfileBuilder;

public class StubSoulProfileBuilder implements SoulProfileBuilder {

   
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

          String portrait = buildPortrait(preferenceProfile);
          List<String> coreTraits = buildCoreTraits(preferenceProfile.styleSignals());
          List<String> deepNeeds = buildDeepNeeds(preferenceProfile.styleSignals());

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

      private String buildPortrait(PreferenceProfile preferenceProfile) {
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
              portrait.append(" They appear to be building stable understanding from repeated exposure.");
          }

          if (noveltyPreference >= 0.60) {
              portrait.append(" They also show active curiosity and a willingness to explore beyond passive consumption.");
          }

          if (intentStrength >= 0.70) {
              portrait.append(" Their behavior suggests relatively high-intent learning rather than casual browsing.");
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

      private List<String> buildCoreTraits(Map<String, Double> signals) {
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

          if (traits.isEmpty()) {
              traits.add("signal still forming");
          }

          return List.copyOf(traits);
      }

      private List<String> buildDeepNeeds(Map<String, Double> signals) {
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

          if (deepNeeds.isEmpty()) {
              deepNeeds.add("needs more stable preference signals");
          }

          return List.copyOf(deepNeeds);
      }

}
