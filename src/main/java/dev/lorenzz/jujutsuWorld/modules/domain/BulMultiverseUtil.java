package dev.lorenzz.jujutsuWorld.modules.domain;

import org.bukkit.Bukkit;

import java.io.File;

public final class BulMultiverseUtil {

    static final String PLUGIN_NAME = "BulMultiverse";

    private BulMultiverseUtil() {
    }

    static boolean isBulMultiverseMissing() {
        return !Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME);
    }

    static boolean worldFolderExists(String worldName) {
        return new File(Bukkit.getWorldContainer(), worldName).isDirectory();
    }
}