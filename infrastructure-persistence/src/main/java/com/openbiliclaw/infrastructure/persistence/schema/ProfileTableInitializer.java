/**
 * file: ProfileTableInitializer.java
 * author: daiyinbao
 * date: 2026-08-05
 */
package com.openbiliclaw.infrastructure.persistence.schema;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

import com.openbiliclaw.infrastructure.persistence.common.PersistenceOperationException;
import com.openbiliclaw.infrastructure.persistence.config.SqliteConnectionFactory;

public class ProfileTableInitializer {

    private final SqliteConnectionFactory sqliteConnectionFactory;
    private AtomicBoolean initialized = new AtomicBoolean(false);
    public ProfileTableInitializer(SqliteConnectionFactory sqliteConnectionFactory) {
        this.sqliteConnectionFactory = sqliteConnectionFactory;
    }
    
    public void ensureInitialized(){
        if(initialized.get()){
            return ;
        }

        synchronized(this){
            if(initialized.get()){
                return ;
            }

            try(Connection connection =sqliteConnectionFactory.getConnection();Statement statement = connection.createStatement()) {

                statement.execute("""
                      CREATE TABLE IF NOT EXISTS preference_profiles (
                          id INTEGER PRIMARY KEY,
                          interests_json TEXT NOT NULL,
                          disliked_topics_json TEXT NOT NULL,
                          style_signals_json TEXT NOT NULL,
                          updated_at TEXT NOT NULL
                      )
                      """);

                  statement.execute("""
                      CREATE TABLE IF NOT EXISTS soul_profiles (
                          id INTEGER PRIMARY KEY,
                          portrait TEXT NOT NULL,
                          core_traits_json TEXT NOT NULL,
                          deep_needs_json TEXT NOT NULL,
                          interests_json TEXT NOT NULL,
                          disliked_topics_json TEXT NOT NULL,
                          updated_at TEXT NOT NULL
                      )
                      """);
                    
                  initialized.set(true);

            } catch (SQLException exception) {
                throw new PersistenceOperationException("Failed to initialize profile tables", exception);
            }

        }


    }


    


}
