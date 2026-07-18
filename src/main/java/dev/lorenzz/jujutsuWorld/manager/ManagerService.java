package dev.lorenzz.jujutsuWorld.manager;

import dev.lorenzz.jujutsuWorld.JujutsuWorld;
import dev.lorenzz.jujutsuWorld.modules.ModuleManger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.JukeboxSong;

import java.util.ArrayList;
import java.util.List;

public class ManagerService

{
    @Getter
    private static ManagerService instance;
    private List<Manager> managers;

    public void start() {
        instance = this;
        this.managers = new ArrayList<>();
        registerManagers();

    }

    public void stop (){
        if (managers == null) {
            if (instance != null) {
                instance = null;
            }
            return;
        }
        for (Manager manager : managers) {
            manager.stop();
        }
        if (instance != null) {
            instance = null;
        }
        managers.clear();
    }

    private void registerManagers(){
        addManager(new ModuleManger(), "ModuleManager");
    }

    private void addManager(final Manager manager, final String name){
       try{
           long startTime = System.currentTimeMillis();
           if(managers.contains(manager)){
               return;
           }
           managers.add(manager);
           manager.start();
           JujutsuWorld.getInstance().getLogger().info("Manager di : " + name + " registrato con successo in " + (System.currentTimeMillis() - startTime) + "ms");
       } catch (Exception ex){
              JujutsuWorld.getInstance().getLogger().warning("Non sono riuscito a registrare il Manager di : " + name + " a causa di un errore: " + ex.getMessage());
              JujutsuWorld.getInstance().getLogger().warning("Questo manager non sarà disponibile per l'uso.");
              JujutsuWorld.getInstance().getLogger().warning("StackTrace: ");
              ex.printStackTrace();
       }
    }

}