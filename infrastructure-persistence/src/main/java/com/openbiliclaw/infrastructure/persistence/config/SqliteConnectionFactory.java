/**
 * file: SqliteConnectionFactory.java
 * author: daiyinbao
 * date: 2026-08-03
 */
package com.openbiliclaw.infrastructure.persistence.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.openbiliclaw.infrastructure.persistence.common.PersistenceOperationException;

public class SqliteConnectionFactory {
    private final Path databasePath;

    public SqliteConnectionFactory(Path databasePath) {
        this.databasePath = databasePath;
    }
   public Connection getConnection(){
        try {
            Path parent = databasePath.toAbsolutePath().getParent();
            if (parent!=null) {
                Files.createDirectories(parent);
            }
            return DriverManager.getConnection("jdbc:sqlite:" + databasePath.toAbsolutePath());
        } catch (SQLException e) {
            throw new PersistenceOperationException("failed to open SQLite connecton",e);
        }catch(Exception exception){
            throw new PersistenceOperationException("failed to prepare SQLite database path",exception); 
        }
   } 

}
