package dev.lorenzz.jujutsuWorld.modules.domain;

import co.aikar.commands.BukkitCommandManager;
import dev.lorenzz.jujutsuWorld.JujutsuWorld;
import dev.lorenzz.jujutsuWorld.manager.Manager;
import dev.lorenzz.jujutsuWorld.storage.DatabaseManager;
import dev.lorenzz.jujutsuWorld.storage.PersonalWorldRepository;

public final class DomainExpansionModule implements Manager {

    private final DatabaseManager database;
    private PersonalWorldRepository repository;
    private BukkitCommandManager commandManager;

    public DomainExpansionModule(DatabaseManager database) {
        this.database = database;
    }

    @Override
    public void start() {
        this.repository = new PersonalWorldRepository(database);

        this.commandManager = new BukkitCommandManager(JujutsuWorld.getInstance());
        this.commandManager.registerCommand(new DomainExpansionCommand(repository));
        this.commandManager.registerCommand(new ExpansionAdminCommand());

        JujutsuWorld.getInstance().getLogger().info("DomainExpansion module started.");
    }

    @Override
    public void stop() {
        if (commandManager != null) {
            commandManager.unregisterCommands();
            commandManager = null;
        }
        this.repository = null;
    }
}