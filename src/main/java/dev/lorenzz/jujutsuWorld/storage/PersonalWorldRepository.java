package dev.lorenzz.jujutsuWorld.storage;

import dev.lorenzz.jujutsuWorld.JujutsuWorld;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;


public final class PersonalWorldRepository {

    private final DatabaseManager db;

    public PersonalWorldRepository(DatabaseManager db) {
        this.db = db;
    }

    public enum Status { PENDING, CREATED, ERROR }

    public CompletableFuture<Boolean> savePersonalWorld(UUID ownerUuid,
                                                         String ownerName,
                                                         String worldName,
                                                         Status status) {
        String sql = "INSERT INTO personal_worlds"
                + " (owner_uuid, owner_name, world_name, status, created_at)"
                + " VALUES (?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " owner_name = VALUES(owner_name),"
                + " status = VALUES(status),"
                + " created_at = VALUES(created_at)";
        return CompletableFuture.supplyAsync(() -> {
            try (Connection c = db.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, ownerUuid.toString());
                ps.setString(2, ownerName);
                ps.setString(3, worldName);
                ps.setString(4, status.name());
                ps.setLong(5, System.currentTimeMillis());
                return ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                JujutsuWorld.getInstance().getLogger().log(Level.SEVERE, "savePersonalWorld failed", ex);
                return false;
            }
        });
    }
}