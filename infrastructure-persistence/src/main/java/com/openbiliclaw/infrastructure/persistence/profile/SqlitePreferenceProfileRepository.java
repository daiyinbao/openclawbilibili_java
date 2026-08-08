/**
 * file: SqlitePreferenceProfileRepository.java
 * author: daiyinbao
 * date: 2026-08-05
 */
package com.openbiliclaw.infrastructure.persistence.profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openbiliclaw.domain.profile.InterestTag;
import com.openbiliclaw.domain.profile.PreferenceProfile;
import com.openbiliclaw.domain.profile.PreferenceProfileRepository;
import com.openbiliclaw.domain.profile.ProfileId;
import com.openbiliclaw.infrastructure.persistence.common.PersistenceOperationException;
import com.openbiliclaw.infrastructure.persistence.config.SqliteConnectionFactory;
import com.openbiliclaw.infrastructure.persistence.schema.ProfileTableInitializer;

public class SqlitePreferenceProfileRepository implements PreferenceProfileRepository{

  
    private static final TypeReference<List<InterestTag>> INTEREST_LIST_TYPE = new TypeReference<>() {
      };
      private static final TypeReference<Map<String, Double>> STYLE_SIGNAL_MAP_TYPE = new TypeReference<>() {
      };

      private final SqliteConnectionFactory connectionFactory;
      private final ProfileTableInitializer tableInitializer;
      private final ObjectMapper objectMapper;

      public SqlitePreferenceProfileRepository(
              SqliteConnectionFactory connectionFactory,
              ProfileTableInitializer tableInitializer,
              ObjectMapper objectMapper
      ) {
          this.connectionFactory = connectionFactory;
          this.tableInitializer = tableInitializer;
          this.objectMapper = objectMapper;
      }

      @Override
      public Optional<PreferenceProfile> loadCurrent() {
          tableInitializer.ensureInitialized();

          String sql = """
              SELECT id, interests_json, disliked_topics_json, style_signals_json, updated_at
              FROM preference_profiles
              ORDER BY id DESC
              LIMIT 1
              """;

          try (Connection connection = connectionFactory.getConnection();
               PreparedStatement statement = connection.prepareStatement(sql);
               ResultSet resultSet = statement.executeQuery()) {

              if (!resultSet.next()) {
                  return Optional.empty();
              }

              PreferenceProfile profile = new PreferenceProfile(
                      new ProfileId(resultSet.getLong("id")),
                      readInterestTags(resultSet.getString("interests_json")),
                      readInterestTags(resultSet.getString("disliked_topics_json")),
                      readStyleSignals(resultSet.getString("style_signals_json")),
                      Instant.parse(resultSet.getString("updated_at"))
              );

              return Optional.of(profile);
          } catch (Exception exception) {
              throw new PersistenceOperationException("Failed to load current preference profile", exception);
          }
      }

      @Override
      public void save(PreferenceProfile profile) {
          tableInitializer.ensureInitialized();

          String sql = """
              INSERT INTO preference_profiles(id, interests_json, disliked_topics_json, style_signals_json, updated_at)
              VALUES (?, ?, ?, ?, ?)
              ON CONFLICT(id) DO UPDATE SET
                  interests_json = excluded.interests_json,
                  disliked_topics_json = excluded.disliked_topics_json,
                  style_signals_json = excluded.style_signals_json,
                  updated_at = excluded.updated_at
              """;

          try (Connection connection = connectionFactory.getConnection();
               PreparedStatement statement = connection.prepareStatement(sql)) {

              statement.setLong(1, profile.id().value());
              statement.setString(2, writeAsJson(profile.interests()));
              statement.setString(3, writeAsJson(profile.dislikedTopics()));
              statement.setString(4, writeAsJson(profile.styleSignals()));
              statement.setString(5, profile.updatedAt().toString());

              statement.executeUpdate();
          } catch (Exception exception) {
              throw new PersistenceOperationException("Failed to save preference profile", exception);
          }
      }

      private String writeAsJson(Object value) {
          try {
              return objectMapper.writeValueAsString(value);
          } catch (JsonProcessingException exception) {
              throw new PersistenceOperationException("Failed to serialize preference profile field", exception);
          }
      }

      private List<InterestTag> readInterestTags(String json) {
          try {
              return objectMapper.readValue(json, INTEREST_LIST_TYPE);
          } catch (Exception exception) {
              throw new PersistenceOperationException("Failed to deserialize interest tags", exception);
          }
      }

      private Map<String, Double> readStyleSignals(String json) {
          try {
              return objectMapper.readValue(json, STYLE_SIGNAL_MAP_TYPE);
          } catch (Exception exception) {
              throw new PersistenceOperationException("Failed to deserialize style signals", exception);
          }
      }
 


}
