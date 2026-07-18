package dev.lorenzz.jujutsuWorld.modules;

import dev.lorenzz.jujutsuWorld.JujutsuWorld;
import dev.lorenzz.jujutsuWorld.manager.Manager;
import org.bukkit.Bukkit;

public class ModuleManger implements Manager {
    @Override
    public void start() {
        try {
            init();
        } catch (Exception ex){
            JujutsuWorld.getInstance().getLogger().severe("Si è verificato un problema durante l'avvio del ModuleManager: " + ex.getMessage());
            ex.printStackTrace();
            Bukkit.getServer().shutdown();
        }
    }
    public void init(){

    }

    @Override
    public void stop() {

    }
}
