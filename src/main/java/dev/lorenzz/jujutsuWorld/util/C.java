package dev.lorenzz.jujutsuWorld.util;

import dev.lorenzz.jujutsuWorld.JujutsuWorld;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface C
{
    static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    static String translate(String message) {
        if (message == null) return "";

        Matcher matcher = HEX_COLOR_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String hexColor = matcher.group(1);
            matcher.appendReplacement(buffer, convertHexToMinecraftFormat(hexColor));
        }
        matcher.appendTail(buffer);

        return org.bukkit.ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    static String msg(String path) {
        var f = JujutsuWorld.getInstance().getMessagesFile();
        String prefix = f.getString("MESSAGES.prefix", "");
        String body = f.getString("MESSAGES." + path, "&cmissing-message: " + path);
        return translate(prefix + body);
    }

    static void info(String message) {
        JujutsuWorld.getInstance().getLogger().info(message);
    }

    static String convertHexToMinecraftFormat(String hex) {
        StringBuilder output = new StringBuilder("§x");
        for (char c : hex.toCharArray()) {
            output.append("§").append(c);
        }
        return output.toString();
    }
}
