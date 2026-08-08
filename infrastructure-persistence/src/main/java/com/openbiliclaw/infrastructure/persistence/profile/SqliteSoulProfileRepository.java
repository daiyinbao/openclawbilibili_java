/**
 * file: SqliteSoulProfileRepository.java
 * author: daiyinbao
 * date: 2026-08-05
 */
package com.openbiliclaw.infrastructure.persistence.profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openbiliclaw.domain.profile.InterestTag;
import com.openbiliclaw.domain.profile.ProfileId;
import com.openbiliclaw.domain.profile.SoulProfile;
import com.openbiliclaw.domain.profile.SoulProfileRepository;
import com.openbiliclaw.infrastructure.persistence.common.PersistenceOperationException;
import com.openbiliclaw.infrastructure.persistence.config.SqliteConnectionFactory;
import com.openbiliclaw.infrastructure.persistence.schema.ProfileTableInitializer;

public class SqliteSoulProfileRepository implements SoulProfileRepository{
    
    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
      };
      private static final TypeReference<List<InterestTag>> INTEREST_LIST_TYPE = new TypeReference<>() {
      };

      private final SqliteConnectionFactory connectionFactory;
      private final ProfileTableInitializer tableInitializer;
      private final ObjectMapper objectMapper;

      public SqliteSoulProfileRepository(
              SqliteConnectionFactory connectionFactory,
              ProfileTableInitializer tableInitializer,
              ObjectMapper objectMapper
      ) {
          this.connectionFactory = connectionFactory;
          this.tableInitializer = tableInitializer;
          this.objectMapper = objectMapper;
      }

      @Override
      public Optional<SoulProfile> loadCurrent() {
          tableInitializer.ensureInitialized();

          String sql = """
              SELECT id, portrait, core_traits_json, deep_needs_json, interests_json, disliked_topics_json, updated_at
              FROM soul_profiles
              ORDER BY id DESC
              LIMIT 1
              """;

          try (Connection connection = connectionFactory.getConnection();
               PreparedStatement statement = connection.prepareStatement(sql);
               ResultSet resultSet = statement.executeQuery()) {

              if (!resultSet.next()) {
                  return Optional.empty();
              }

              SoulProfile profile = new SoulProfile(
                      new ProfileId(resultSet.getLong("id")),
                      resultSet.getString("portrait"),
                      readStringList(resultSet.getString("core_traits_json")),
                      readStringList(resultSet.getString("deep_needs_json")),
                      readInterestTags(resultSet.getString("interests_json")),
                      readInterestTags(resultSet.getString("disliked_topics_json")),
                      Instant.parse(resultSet.getString("updated_at"))
              );

              return Optional.of(profile);
          } catch (Exception exception) {
              throw new PersistenceOperationException("Failed to load current soul profile", exception);
          }
      }

      @Override
      public void save(SoulProfile profile) {
          tableInitializer.ensureInitialized();

          String sql = """
              INSERT INTO soul_profiles(
                  id, portrait, core_traits_json, deep_needs_json, interests_json, disliked_topics_json, updated_at
              )
              VALUES (?, ?, ?, ?, ?, ?, ?)
              ON CONFLICT(id) DO UPDATE SET
                  portrait = excluded.portrait,
                  core_traits_json = excluded.core_traits_json,
                  deep_needs_json = excluded.deep_needs_json,
                  interests_json = excluded.interests_json,
                  disliked_topics_json = excluded.disliked_topics_json,
                  updated_at = excluded.updated_at
              """;

          try (Connection connection = connectionFactory.getConnection();
               PreparedStatement statement = connection.prepareStatement(sql)) {

              statement.setLong(1, profile.id().value());
              statement.setString(2, profile.portrait());
              statement.setString(3, writeAsJson(profile.coreTraits()));
              statement.setString(4, writeAsJson(profile.deepNeeds()));
              statement.setString(5, writeAsJson(profile.interests()));
              statement.setString(6, writeAsJson(profile.dislikedTopics()));
              statement.setString(7, profile.updatedAt().toString());

              statement.executeUpdate();
          } catch (Exception exception) {
              throw new PersistenceOperationException("Failed to save soul profile", exception);
          }
      }

      private String writeAsJson(Object value) {
          try {
              return objectMapper.writeValueAsString(value);
          } catch (JsonProcessingException exception) {
              throw new PersistenceOperationException("Failed to serialize soul profile field", exception);
          }
      }

      private List<String> readStringList(String json) {
          try {
              return objectMapper.readValue(json, STRING_LIST_TYPE);
          } catch (Exception exception) {
              throw new PersistenceOperationException("Failed to deserialize string list", exception);
          }
      }

      private List<InterestTag> readInterestTags(String json) {
          try {
              return objectMapper.readValue(json, INTEREST_LIST_TYPE);
          } catch (Exception exception) {
              throw new PersistenceOperationException("Failed to deserialize interest tag list", exception);
          }
      }


}
