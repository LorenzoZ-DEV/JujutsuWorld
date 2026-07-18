package dev.lorenzz.jujutsuWorld.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.lorenzz.jujutsuWorld.JujutsuWorld;
import dev.lorenzz.jujutsuWorld.manager.Manager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager implements Manager {

    @Getter
    private static DatabaseManager instance;

    @Getter
    private HikariDataSource dataSource;

    @Override
    public void start() {
        instance = this;

        FileConfiguration cfg = JujutsuWorld.getInstance().getConfigFile();
        String host = cfg.getString("STORAGE.IP", "localhost");
        int port = cfg.getInt("STORAGE.PORT", 3306);
        String db = cfg.getString("STORAGE.DATABASE", "db");
        String user = cfg.getString("STORAGE.USERNAME", "root");
        String pass = cfg.getString("STORAGE.PASSWORD", "");

        String url = "jdbc:mariadb://" + host + ":" + port + "/" + db
                + "?useSSL=false&autoReconnect=true&maxReconnects=3";

        HikariConfig hc = new HikariConfig();
        hc.setPoolName("JujutsuWorld-Hikari");
        hc.setJdbcUrl(url);
        hc.setUsername(user);
        hc.setPassword(pass);
        hc.setMaximumPoolSize(10);
        hc.setMinimumIdle(2);
        hc.setConnectionTimeout(5000);
        hc.setIdleTimeout(600000);
        hc.setMaxLifetime(1800000);
        hc.addDataSourceProperty("cachePrepStmts", "true");
        hc.addDataSourceProperty("prepStmtCacheSize", "250");

        this.dataSource = new HikariDataSource(hc);
        initSchema();
        JujutsuWorld.getInstance().getLogger().info("Database pool started (" + url + ").");
    }

    @Override
    public void stop() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
        if (instance != null) {
            instance = null;
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Hikari pool not initialized");
        }
        return dataSource.getConnection();
    }

    private void initSchema() {
        String sql = "CREATE TABLE IF NOT EXISTS personal_worlds ("
                + "  id INT AUTO_INCREMENT PRIMARY KEY,"
                + "  owner_uuid VARCHAR(36) NOT NULL,"
                + "  owner_name VARCHAR(32) NOT NULL,"
                + "  world_name VARCHAR(64) NOT NULL,"
                + "  status VARCHAR(16) NOT NULL DEFAULT 'PENDING',"
                + "  created_at BIGINT NOT NULL,"
                + "  UNIQUE KEY uq_owner_world (owner_uuid, world_name)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException ex) {
            JujutsuWorld.getInstance().getLogger().severe("Schema init failed: " + ex.getMessage());
        }
    }
}