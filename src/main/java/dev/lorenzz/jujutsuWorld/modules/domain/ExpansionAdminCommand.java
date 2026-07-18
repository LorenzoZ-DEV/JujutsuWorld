package dev.lorenzz.jujutsuWorld.modules.domain;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.lorenzz.jujutsuWorld.JujutsuWorld;
import dev.lorenzz.jujutsuWorld.util.C;
import org.bukkit.command.CommandSender;

@CommandAlias("expansionadmin|dadmin")
@CommandPermission("jujutsu.admin.reload")
public final class ExpansionAdminCommand extends BaseCommand {

    @Subcommand("reload")
    public void onReload(CommandSender sender) {
        try {
            JujutsuWorld.getInstance().getConfigFile().reload();
            JujutsuWorld.getInstance().getMessagesFile().reload();
            sender.sendMessage(C.msg("admin-reload-success"));
        } catch (Exception ex) {
            String err = ex.getMessage() == null ? "unknown" : ex.getMessage();
            sender.sendMessage(C.msg("admin-reload-error").replace("{error}", err));
            JujutsuWorld.getInstance().getLogger().severe("Config reload failed: " + err);
        }
    }

    @Default
    public void onDefault(CommandSender sender) {
        sender.sendMessage(C.msg("admin-usage"));
    }
}