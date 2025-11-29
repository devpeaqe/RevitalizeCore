package de.peaqe.revitalizecore.utils;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 26.11.2025 | 19:14 Uhr
 * *
 */

public class MessageUtil {

    private final Plugin plugin;
    private final String prefix, textColor, highlightColor;

    public MessageUtil(Plugin plugin, String prefix, String textColor, String highlightColor) {
        this.plugin = plugin;
        this.prefix = prefix;
        this.textColor = textColor;
        this.highlightColor = highlightColor;
    }

    public MessageUtil(Plugin plugin) {
        this.plugin = plugin;
        this.prefix = "§8[§cSYSTEM§8] ×";
        this.textColor = "§7";
        this.highlightColor = "§c";
    }

    public String compileMessage(String message, String... highlights) {

        for (var highlight : highlights) {
            var colored = highlightColor + highlight + textColor;
            message = message.replaceFirst("%s", colored);
        }

        message = textColor + message;

        return formatMessage(message);
    }

    public String compileMessageKey(String message, String... replacements) {

        if (replacements.length % 2 != 0) {
            plugin.getLogger().warning(
                    "[Plugin] Ungerade Anzahl von Parametern in compileMessageKey(). " +
                            "Die Methode erwartet key/value Paare."
            );
        }

        for (int i = 0; i < replacements.length - 1; i += 2) {
            var key = replacements[i];
            var value = replacements[i + 1];
            var colored = highlightColor + value + textColor;

            message = message.replace(key, colored);
        }

        message = textColor + message;

        return formatMessage(message);
    }

    @SuppressWarnings("deprecation")
    private String formatMessage(String message) {
        var cleanPrefix = prefix.replace("%prefix%", "");
        var full = cleanPrefix + " " + message;

        return ChatColor.translateAlternateColorCodes('&', full).replace("  ", " ");
    }

    public static String normalize(String input) {
        return input
                .toLowerCase()
                .replaceAll("[^a-zA-Z0-9]", "");
    }
}
