/**
 * file: CliBootstrap.java
 * author: daiyinbao
 * date: 2026-08-04
 */
package com.openbiliclaw.app.cli.bootstrap;

import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openbiliclaw.application.event.DefaultIngestEventUseCase;
import com.openbiliclaw.application.event.IngestEventUseCase;
import com.openbiliclaw.application.profile.DefaultUpdateProfileUseCase;
import com.openbiliclaw.application.profile.UpdateProfileUseCase;
import com.openbiliclaw.domain.event.EventRepository;
import com.openbiliclaw.domain.profile.PreferenceAnalyzer;
import com.openbiliclaw.domain.profile.PreferenceProfileRepository;
import com.openbiliclaw.domain.profile.SoulProfileBuilder;
import com.openbiliclaw.domain.profile.SoulProfileRepository;
import com.openbiliclaw.infrastructure.llm.profile.StubPreferenceAnalyzer;
import com.openbiliclaw.infrastructure.llm.profile.StubSoulProfileBuilder;
import com.openbiliclaw.infrastructure.persistence.config.SqliteConnectionFactory;
import com.openbiliclaw.infrastructure.persistence.event.SqliteEventRepository;
import com.openbiliclaw.infrastructure.persistence.profile.SqlitePreferenceProfileRepository;
import com.openbiliclaw.infrastructure.persistence.profile.SqliteSoulProfileRepository;
import com.openbiliclaw.infrastructure.persistence.schema.EventTableInitializer;
import com.openbiliclaw.infrastructure.persistence.schema.ProfileTableInitializer;

public class CliBootstrap {
    
    
     
      private final EventRepository eventRepository;
      private final PreferenceProfileRepository preferenceProfileRepository;
      private final SoulProfileRepository soulProfileRepository;
      private final IngestEventUseCase ingestEventUseCase;
      private final UpdateProfileUseCase updateProfileUseCase;

      public CliBootstrap(Path databasePath) {
          SqliteConnectionFactory connectionFactory = new SqliteConnectionFactory(databasePath);
          EventTableInitializer eventTableInitializer = new EventTableInitializer(connectionFactory);
          ProfileTableInitializer profileTableInitializer = new ProfileTableInitializer(connectionFactory);
          ObjectMapper objectMapper = new ObjectMapper();

          this.eventRepository = new SqliteEventRepository(
                  connectionFactory,
                  eventTableInitializer,
                  objectMapper
          );

          this.preferenceProfileRepository = new SqlitePreferenceProfileRepository(
                  connectionFactory,
                  profileTableInitializer,
                  objectMapper
          );

          this.soulProfileRepository = new SqliteSoulProfileRepository(
                  connectionFactory,
                  profileTableInitializer,
                  objectMapper
          );

          PreferenceAnalyzer preferenceAnalyzer = new StubPreferenceAnalyzer();
          SoulProfileBuilder soulProfileBuilder = new StubSoulProfileBuilder();

          this.ingestEventUseCase = new DefaultIngestEventUseCase(eventRepository);
          this.updateProfileUseCase = new DefaultUpdateProfileUseCase(
                  eventRepository,
                  preferenceAnalyzer,
                  soulProfileBuilder,
                  preferenceProfileRepository,
                  soulProfileRepository
          );
      }

      public EventRepository eventRepository() {
          return eventRepository;
      }

      public PreferenceProfileRepository preferenceProfileRepository() {
          return preferenceProfileRepository;
      }

      public SoulProfileRepository soulProfileRepository() {
          return soulProfileRepository;
      }

      public IngestEventUseCase ingestEventUseCase() {
          return ingestEventUseCase;
      }

      public UpdateProfileUseCase updateProfileUseCase() {
          return updateProfileUseCase;
      }
 }
