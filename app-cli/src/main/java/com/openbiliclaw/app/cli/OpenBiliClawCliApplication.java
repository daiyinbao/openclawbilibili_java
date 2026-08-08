package com.openbiliclaw.app.cli;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.openbiliclaw.app.cli.bootstrap.CliBootstrap;
import com.openbiliclaw.application.event.IngestEventCommand;
import com.openbiliclaw.application.event.UserEventPayload;
import com.openbiliclaw.application.profile.UpdateProfileCommand;
import com.openbiliclaw.domain.event.EventType;
import com.openbiliclaw.domain.source.SourcePlatform;

public final class OpenBiliClawCliApplication {
   
     private OpenBiliClawCliApplication() {
      }

      public static void main(String[] args) {
          Path databasePath = Path.of("data", "openbiliclaw.db");
          CliBootstrap bootstrap = new CliBootstrap(databasePath);

          UserEventPayload eventPayload = new UserEventPayload(
                  EventType.VIEW,
                  "Bilibili architecture demo",
                  "https://www.bilibili.com/video/BV1demo123",
                  Instant.now(),
                  SourcePlatform.BILIBILI,
                  Map.of(
                          "bvid", "BV1demo123",
                          "author", "OpenBiliClaw Demo",
                          "durationSeconds", "600"
                  )
          );

          IngestEventCommand command = new IngestEventCommand(List.of(eventPayload));
          var ingestResult = bootstrap.ingestEventUseCase().execute(command);

          System.out.println("=== Ingest Result ===");
          System.out.println("Accepted count: " + ingestResult.acceptedCount());
          System.out.println("Rejected count: " + ingestResult.rejectedCount());
          System.out.println("Saved event ids: " + ingestResult.savedEventIds());

          var profileResult = bootstrap.updateProfileUseCase().execute(new UpdateProfileCommand(false));

          System.out.println();
          System.out.println("=== Profile Result ===");
          System.out.println("Consumed event count: " + profileResult.consumedEventCount());
          System.out.println("Portrait: " + profileResult.soulProfile().portrait());
          System.out.println("Core traits: " + profileResult.soulProfile().coreTraits());
          System.out.println("Deep needs: " + profileResult.soulProfile().deepNeeds());
          System.out.println("Interests: " + profileResult.preferenceProfile().interests());

          var storedPreferenceProfile = bootstrap.preferenceProfileRepository().loadCurrent();
          var storedSoulProfile = bootstrap.soulProfileRepository().loadCurrent();

          System.out.println();
          System.out.println("=== Stored Preference Profile ===");
          if (storedPreferenceProfile.isPresent()) {
              var profile = storedPreferenceProfile.get();
              System.out.println("Profile id: " + profile.id().value());
              System.out.println("Updated at: " + profile.updatedAt());
              System.out.println("Interests: " + profile.interests());
              System.out.println("Disliked topics: " + profile.dislikedTopics());
              System.out.println("Style signals: " + profile.styleSignals());
          } else {
              System.out.println("No stored preference profile found.");
          }

          System.out.println();
          System.out.println("=== Stored Soul Profile ===");
          if (storedSoulProfile.isPresent()) {
              var profile = storedSoulProfile.get();
              System.out.println("Profile id: " + profile.id().value());
              System.out.println("Updated at: " + profile.updatedAt());
              System.out.println("Portrait: " + profile.portrait());
              System.out.println("Core traits: " + profile.coreTraits());
              System.out.println("Deep needs: " + profile.deepNeeds());
              System.out.println("Interests: " + profile.interests());
              System.out.println("Disliked topics: " + profile.dislikedTopics());
          } else {
              System.out.println("No stored soul profile found.");
          }

          var recentEvents = bootstrap.eventRepository().findRecent(10);

          System.out.println();
          System.out.println("=== Recent Events ===");
          for (var event : recentEvents) {
              System.out.println("ID: " + (event.id() != null ? event.id().value() : null));
              System.out.println("Type: " + event.eventType());
              System.out.println("Title: " + event.title());
              System.out.println("URL: " + event.url());
              System.out.println("OccurredAt: " + event.occurredAt());
              System.out.println("Platform: " + event.sourcePlatform());
              System.out.println("Metadata: " + event.metadata());
              System.out.println("---");
          }
      }
}

