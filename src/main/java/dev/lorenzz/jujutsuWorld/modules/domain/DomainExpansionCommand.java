package dev.lorenzz.jujutsuWorld.modules.domain;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.lorenzz.jujutsuWorld.JujutsuWorld;
import dev.lorenzz.jujutsuWorld.storage.PersonalWorldRepository;
import dev.lorenzz.jujutsuWorld.util.C;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

@CommandAlias("domainexpansion|domain")
@CommandPermission("jujutsu.domainexpansion.use")
public final class DomainExpansionCommand extends BaseCommand {

    private final PersonalWorldRepository repo;

    public DomainExpansionCommand(PersonalWorldRepository repo) {
        this.repo = repo;
    }

    @Default
    public void onDomainExpansion(Player player) {
        String worldName = "domain_" + player.getName().toLowerCase();
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        send(player, "domain-expansion-creating", worldName, name);

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = createDomain(worldName);
            if (world == null) {
                send(player, "domain-expansion-failed", worldName, name);
                return;
            }
            registerDomain(uuid, name, worldName, player);
        } else {
            send(player, "domain-expansion-already", worldName, name);
        }

        playDomainSounds(player);
        player.teleport(world.getSpawnLocation());
    }

    private World createDomain(String worldName) {
        if (useBulMultiverse()) {
            World viaBmv = createViaBulMultiverse(worldName);
            if (viaBmv != null) {
                return viaBmv;
            }
            JujutsuWorld.getInstance().getLogger().warning(
                    "BulMultiverse create failed for " + worldName + " — falling back to raw WorldCreator.");
        }
        try {
            WorldCreator wc = WorldCreator.name(worldName).environment(World.Environment.NORMAL);
            wc.type(parseWorldType());
            World world = wc.createWorld();
            if (world != null) {
                world.setKeepSpawnInMemory(false);
            }
            return world;
        } catch (Exception ex) {
            JujutsuWorld.getInstance().getLogger()
                    .log(Level.SEVERE, "Domain world creation failed: " + worldName, ex);
            return null;
        }
    }

    private World createViaBulMultiverse(String worldName) {
        if (BulMultiverseUtil.isBulMultiverseMissing()) {
            return null;
        }
        World existing = Bukkit.getWorld(worldName);
        if (existing != null) {
            return existing;
        }
        if (BulMultiverseUtil.worldFolderExists(worldName)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bmv load " + worldName);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "bmv create " + worldName + " -t " + worldTypeName());
        }
        return Bukkit.getWorld(worldName);
    }

    private boolean useBulMultiverse() {
        return config().getBoolean("HOOKS.Bulmultiverse", false)
                && !BulMultiverseUtil.isBulMultiverseMissing();
    }

    private String worldTypeName() {
        String type = config().getString("DOMAIN.world-type", "FLAT").toUpperCase();
        try {
            return WorldType.valueOf(type).name();
        } catch (IllegalArgumentException ex) {
            return "FLAT";
        }
    }

    private void registerDomain(UUID uuid, String name, String worldName, Player player) {
        repo.savePersonalWorld(uuid, name, worldName, PersonalWorldRepository.Status.CREATED)
                .thenAccept(ok -> Bukkit.getScheduler().runTask(JujutsuWorld.getInstance(), () ->
                        send(player, ok ? "domain-expansion-success" : "domain-expansion-db-error", worldName, name)));
    }

    private WorldType parseWorldType() {
        String type = config().getString("DOMAIN.world-type", "FLAT").toUpperCase();
        try {
            return WorldType.valueOf(type);
        } catch (IllegalArgumentException ex) {
            return WorldType.FLAT;
        }
    }

    private void playDomainSounds(Player player) {
        List<?> sounds = config().getList("DOMAIN.sounds");
        if (sounds == null) {
            return;
        }
        for (Object o : sounds) {
            if (!(o instanceof Map<?, ?>)) {
                continue;
            }
            Map<?, ?> m = (Map<?, ?>) o;
            final String soundName = String.valueOf(m.get("sound"));
            final float volume = fnum(m.get("volume"), 1.0f);
            final float pitch = fnum(m.get("pitch"), 1.0f);
            long delay = lnum(m.get("delay"), 0L);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Sound sound = soundOf(soundName);
                    if (sound != null) {
                        player.playSound(player.getLocation(), sound, volume, pitch);
                    }
                }
            }.runTaskLater(JujutsuWorld.getInstance(), delay);
        }
    }

    private Sound soundOf(String name) {
        if (name == null || name.equalsIgnoreCase("null")) {
            return null;
        }
        String key = name.toUpperCase().replace('-', '_').replace(' ', '_');
        try {
            return Sound.valueOf(key);
        } catch (IllegalArgumentException ex) {
            JujutsuWorld.getInstance().getLogger().warning("Unknown sound in config: " + name);
            return null;
        }
    }

    private float fnum(Object o, float def) {
        return o instanceof Number ? ((Number) o).floatValue() : def;
    }

    private long lnum(Object o, long def) {
        return o instanceof Number ? ((Number) o).longValue() : def;
    }

    private void send(Player player, String path, String worldName, String name) {
        String msg = C.msg(path)
                .replace("{worldname}", worldName)
                .replace("{player}", name);
        player.sendMessage(msg);
    }

    private FileConfiguration config() {
        return JujutsuWorld.getInstance().getConfigFile();
    }
}