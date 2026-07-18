package dev.lorenzz.jujutsuWorld.modules;

import dev.lorenzz.jujutsuWorld.JujutsuWorld;
import dev.lorenzz.jujutsuWorld.manager.Manager;
import dev.lorenzz.jujutsuWorld.modules.domain.DomainExpansionModule;
import dev.lorenzz.jujutsuWorld.storage.DatabaseManager;
import org.bukkit.Bukkit;

public class ModuleManger implements Manager {
    private DatabaseManager database;
    private DomainExpansionModule domainExpansion;

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
        this.database = new DatabaseManager();
        database.start();

        this.domainExpansion = new DomainExpansionModule(database);
        domainExpansion.start();
    }

    @Override
    public void stop() {
        if (domainExpansion != null) {
            domainExpansion.stop();
            domainExpansion = null;
        }
        if (database != null) {
            database.stop();
            database = null;
        }
    }
}