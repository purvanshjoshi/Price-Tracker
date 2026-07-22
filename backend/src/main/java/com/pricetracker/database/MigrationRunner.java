package com.pricetracker.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigrationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MigrationRunner.class);
    private static final String DB_URL = "jdbc:sqlite:price_history.db";
    private static final String MIGRATIONS_DIR = "migrations/";

    public static void migrate() {
        ensureVersionTable();

        List<String> applied = getAppliedVersions();
        List<Migration> pending = getPendingMigrations(applied);

        for (Migration migration : pending) {
            applyMigration(migration);
        }

        if (pending.isEmpty()) {
            LOG.info("No pending database migrations");
        }
    }

    private static void ensureVersionTable() {
        String sql = "CREATE TABLE IF NOT EXISTS schema_version (" +
                "version TEXT PRIMARY KEY," +
                "applied_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create schema_version table", e);
        }
    }

    private static List<String> getAppliedVersions() {
        List<String> versions = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT version FROM schema_version ORDER BY version")) {
            while (rs.next()) {
                versions.add(rs.getString("version"));
            }
        } catch (Exception e) {
            LOG.error("Failed to read applied migrations", e);
        }
        return versions;
    }

    private static List<Migration> getPendingMigrations(List<String> applied) {
        List<Migration> pending = new ArrayList<>();
        try {
            InputStream dir = MigrationRunner.class.getClassLoader()
                    .getResourceAsStream(MIGRATIONS_DIR);
            if (dir == null) {
                return pending;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(dir, StandardCharsets.UTF_8));
            List<String> files = reader.lines()
                    .filter(f -> f.endsWith(".sql"))
                    .sorted()
                    .collect(Collectors.toList());
            for (String file : files) {
                String version = file.split("__")[0].substring(1);
                if (!applied.contains(version)) {
                    pending.add(new Migration(version, file));
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to list migration files", e);
        }
        return pending;
    }

    private static void applyMigration(Migration migration) {
        try {
            InputStream is = MigrationRunner.class.getClassLoader()
                    .getResourceAsStream(MIGRATIONS_DIR + migration.fileName);
            if (is == null) {
                LOG.error("Migration file not found: {}", migration.fileName);
                return;
            }
            String sql = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            try (Connection conn = DriverManager.getConnection(DB_URL);
                 Statement stmt = conn.createStatement()) {
                conn.setAutoCommit(false);
                stmt.execute(sql);

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO schema_version(version) VALUES(?)")) {
                    ps.setString(1, migration.version);
                    ps.executeUpdate();
                }
                conn.commit();
                LOG.info("Applied migration: {} ({})", migration.fileName, migration.version);
            }
        } catch (Exception e) {
            log.error("Failed to apply migration: {}", migration.fileName, e);
            throw new RuntimeException("Migration failed: " + migration.fileName, e);
        }
    }

    private static class Migration {
        final String version;
        final String fileName;

        Migration(String version, String fileName) {
            this.version = version;
            this.fileName = fileName;
        }
    }
}
