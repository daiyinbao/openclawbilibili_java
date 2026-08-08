/**
 * file: SqliteEventRepository.java
 * author: daiyinbao
 * date: 2026-08-03
 */
package com.openbiliclaw.infrastructure.persistence.event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openbiliclaw.domain.event.EventId;
import com.openbiliclaw.domain.event.EventRepository;
import com.openbiliclaw.domain.event.EventType;
import com.openbiliclaw.domain.event.UserEvent;
import com.openbiliclaw.domain.source.SourcePlatform;
import com.openbiliclaw.infrastructure.persistence.common.PersistenceOperationException;
import com.openbiliclaw.infrastructure.persistence.config.SqliteConnectionFactory;
import com.openbiliclaw.infrastructure.persistence.schema.EventTableInitializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SqliteEventRepository implements EventRepository {
      private static final TypeReference<Map<String, String>> STRING_MAP_TYPE = new TypeReference<>() {
      };

      private final SqliteConnectionFactory connectionFactory;
      private final EventTableInitializer tableInitializer;
      private final ObjectMapper objectMapper;

      public SqliteEventRepository(
              SqliteConnectionFactory connectionFactory,
              EventTableInitializer tableInitializer,
              ObjectMapper objectMapper
      ) {
          this.connectionFactory = connectionFactory;
          this.tableInitializer = tableInitializer;
          this.objectMapper = objectMapper;
      }

      @Override
      public EventId save(UserEvent event) {
          return saveAll(List.of(event)).get(0);
      }

      @Override
      public List<EventId> saveAll(List<UserEvent> events) {
          tableInitializer.ensureInitialized();

          if (events == null || events.isEmpty()) {
              return List.of();
          }

          String sql = """
              INSERT INTO events(event_type, title, url, occurred_at, source_platform, metadata_json)
              VALUES (?, ?, ?, ?, ?, ?)
              """;

          List<EventId> generatedIds = new ArrayList<>(events.size());

          try (Connection connection = connectionFactory.getConnection()) {
              connection.setAutoCommit(false);

              try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                  for (UserEvent event : events) {
                      bindInsert(statement, event);
                      statement.executeUpdate();

                      try (ResultSet keys = statement.getGeneratedKeys()) {
                          if (!keys.next()) {
                              throw new PersistenceOperationException("Failed to retrieve generated event id");
                          }
                          generatedIds.add(new EventId(keys.getLong(1)));
                      }
                  }
              }

              connection.commit();
              return List.copyOf(generatedIds);
          } catch (Exception exception) {
              throw new PersistenceOperationException("Failed to save events into SQLite", exception);
          }
      }

      @Override
      public List<UserEvent> findRecent(int limit) {
          tableInitializer.ensureInitialized();

          String sql = """
              SELECT id, event_type, title, url, occurred_at, source_platform, metadata_json
              FROM events
              ORDER BY occurred_at DESC, id DESC
              LIMIT ?
              """;

          try (Connection connection = connectionFactory.getConnection();
               PreparedStatement statement = connection.prepareStatement(sql)) {

              statement.setInt(1, limit);
              try (ResultSet resultSet = statement.executeQuery()) {
                  return readEvents(resultSet);
              }
          } catch (Exception exception) {
              throw new PersistenceOperationException("Failed to query recent events", exception);
          }
      }

      @Override
      public List<UserEvent> findByTimeRange(Instant fromInclusive, Instant toExclusive) {
          tableInitializer.ensureInitialized();

          String sql = """
              SELECT id, event_type, title, url, occurred_at, source_platform, metadata_json
              FROM events
              WHERE occurred_at >= ? AND occurred_at < ?
              ORDER BY occurred_at ASC, id ASC
              """;

          try (Connection connection = connectionFactory.getConnection();
               PreparedStatement statement = connection.prepareStatement(sql)) {

              statement.setString(1, fromInclusive.toString());
              statement.setString(2, toExclusive.toString());

              try (ResultSet resultSet = statement.executeQuery()) {
                  return readEvents(resultSet);
              }
          } catch (Exception exception) {
              throw new PersistenceOperationException("Failed to query events by time range", exception);
          }
      }

      private void bindInsert(PreparedStatement statement, UserEvent event) throws Exception {
          statement.setString(1, event.eventType().name());
          statement.setString(2, event.title());
          statement.setString(3, event.url());
          statement.setString(4, event.occurredAt().toString());
          statement.setString(5, event.sourcePlatform().name());
          statement.setString(6, writeMetadata(event.metadata()));
      }

      private List<UserEvent> readEvents(ResultSet resultSet) throws Exception {
          List<UserEvent> events = new ArrayList<>();
          while (resultSet.next()) {
              events.add(new UserEvent(
                      new EventId(resultSet.getLong("id")),
                      EventType.valueOf(resultSet.getString("event_type")),
                      resultSet.getString("title"),
                      resultSet.getString("url"),
                      Instant.parse(resultSet.getString("occurred_at")),
                      SourcePlatform.valueOf(resultSet.getString("source_platform")),
                      readMetadata(resultSet.getString("metadata_json"))
              ));
          }
          return events;
      }

      private String writeMetadata(Map<String, String> metadata) {
          try {
              Map<String, String> safeMetadata = metadata == null ? Map.of() : Map.copyOf(metadata);
              return objectMapper.writeValueAsString(safeMetadata);
          } catch (JsonProcessingException exception) {
              throw new PersistenceOperationException("Failed to serialize event metadata", exception);
          }
      }

      private Map<String, String> readMetadata(String metadataJson) {
          try {
              if (metadataJson == null || metadataJson.isBlank()) {
                  return Map.of();
              }
              return objectMapper.readValue(metadataJson, STRING_MAP_TYPE);
          } catch (Exception exception) {
              throw new PersistenceOperationException("Failed to deserialize event metadata", exception);
          }
      }
  }
