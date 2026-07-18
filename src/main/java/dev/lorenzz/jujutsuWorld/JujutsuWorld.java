package dev.lorenzz.jujutsuWorld;

import dev.lorenzz.jujutsuWorld.manager.ManagerService;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class JujutsuWorld extends JavaPlugin {

    @Getter
    private static JujutsuWorld instance;
    private ManagerService managerService = new ManagerService();
    @Override

    public void onLoad(){
        getLogger().info("Questo plugin è sviluppato per il provino Developer su FruitMC");
        getLogger().info("Developed by Lorenzzzzzz");
        getLogger().info("Loading JujutsuWorld...");
    }

    public void onEnable() {
        instance = this;
        this.managerService.start();
    }

    @Override
    public void onDisable() {
        this.managerService.stop();
        if (instance != null) {
            instance = null;
        }
    }
}
