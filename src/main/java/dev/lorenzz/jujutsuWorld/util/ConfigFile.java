package dev.lorenzz.jujutsuWorld.util;


import dev.lorenzz.jujutsuWorld.JujutsuWorld;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public class ConfigFile extends YamlConfiguration {
    private final Plugin plugin;
    private final String fileName;
    private final Path filePath;

    public ConfigFile(Plugin plugin, String fileName, boolean load, boolean forceCreate) {
        this.plugin = plugin;
        this.fileName = fileName.endsWith(".yml") ? fileName : fileName + ".yml";
        this.filePath = Path.of(plugin.getDataFolder().getPath(), this.fileName);
        this.ensureDataFolder();

        try {
            if (forceCreate) {
                Files.deleteIfExists(this.filePath);
            }

            if (Files.notExists(this.filePath, new LinkOption[0])) {
                plugin.saveResource(this.fileName, false);
            }

            if (load) {
                super.load(this.filePath.toFile());
                InputStream resource = plugin.getResource(this.fileName);
                if (resource != null) {
                    try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
                        YamlConfiguration defaults = YamlConfiguration.loadConfiguration(reader);
                        this.setDefaults(defaults);
                        this.options().copyDefaults(true);
                    }
                }

                super.save(this.filePath.toFile());
            }
        } catch (InvalidConfigurationException | IOException ex) {
            JujutsuWorld.getInstance().getLogger().severe("Error loading config file: " + this.fileName);
        }

    }

    public ConfigFile(Plugin plugin, String fileName, boolean load) {
        this(plugin, fileName, load, false);
    }

    public ConfigFile(Plugin plugin, String fileName) {
        this(plugin, fileName, true, false);
    }

    private void ensureDataFolder() {
        try {
            Files.createDirectories(Path.of(this.plugin.getDataFolder().getPath()));
        } catch (IOException e) {
            JujutsuWorld.getInstance().getLogger().severe("Error loading config file: " + this.fileName);
        }

    }

    public void reload() {
        try {
            super.load(this.filePath.toFile());
            this.plugin.getLogger().info("Reloaded config file: " + this.fileName);
        } catch (InvalidConfigurationException | IOException ex) {
            JujutsuWorld.getInstance().getLogger().severe("Error loading config file: " + this.fileName);

        }
    }

    public void saveFile() {
        try {
            super.save(this.filePath.toFile());
            C.info("Saved config file: " + this.fileName);
        } catch (IOException ex) {
            JujutsuWorld.getInstance().getLogger().severe("Error loading config file: " + this.fileName);
        }
    }

    public void setDefault(String path, Object value) {
        if (!this.contains(path)) {
            this.set(path, value);
        }

    }

    public void deleteFile() {
        try {
            Files.deleteIfExists(this.filePath);
            JujutsuWorld.getInstance().getLogger().info("Deleted config file: " + this.fileName);
        } catch (IOException ex) {
            JujutsuWorld.getInstance().getLogger().severe("Error loading config file: " + this.fileName);

        }
    }
}

