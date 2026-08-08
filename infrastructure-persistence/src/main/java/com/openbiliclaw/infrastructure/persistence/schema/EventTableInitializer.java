/**
 * file: EventTableInitializer.java
 * author: daiyinbao
 * date: 2026-08-03
 */
package com.openbiliclaw.infrastructure.persistence.schema;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

import com.openbiliclaw.infrastructure.persistence.common.PersistenceOperationException;
import com.openbiliclaw.infrastructure.persistence.config.SqliteConnectionFactory;

public class EventTableInitializer {
    
    private final SqliteConnectionFactory sqliteConnectionFactory;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    
    public EventTableInitializer(SqliteConnectionFactory sqliteConnectionFactory){
        this.sqliteConnectionFactory = sqliteConnectionFactory;
    }


    public void ensureInitialized() {
          if (initialized.get()) {
              return;
          }

          synchronized (this) {
              if (initialized.get()) {
                  return;
              }

              try (Connection connection = sqliteConnectionFactory.getConnection();
                   Statement statement = connection.createStatement()) {

                  statement.execute("""
                      CREATE TABLE IF NOT EXISTS events (
                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                          event_type TEXT NOT NULL,
                          title TEXT,
                          url TEXT,
                          occurred_at TEXT NOT NULL,
                          source_platform TEXT NOT NULL,
                          metadata_json TEXT NOT NULL
                      )
                      """);

                  statement.execute("""
                      CREATE INDEX IF NOT EXISTS idx_events_occurred_at
                      ON events(occurred_at)
                      """);

                  statement.execute("""
                      CREATE INDEX IF NOT EXISTS idx_events_event_type
                      ON events(event_type)
                      """);

                  initialized.set(true);
              } catch (SQLException exception) {
                  throw new PersistenceOperationException("Failed to initialize events table", exception);
              }
          }
      }


}
