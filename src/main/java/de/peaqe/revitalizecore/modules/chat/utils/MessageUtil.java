package de.peaqe.revitalizecore.modules.chat.utils;

import de.peaqe.revitalizecore.modules.chat.ChatModule;
import org.bukkit.ChatColor;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 26.11.2025 | 14:25 Uhr
 * *
 */

public class MessageUtil {

    private final ChatModule chatModule;
    private final String prefix;

    public MessageUtil(ChatModule chatModule) {
        this.chatModule = chatModule;
        this.prefix = chatModule.getChatConfig().getPrefix();
    }

    public String compileMessage(String message, String... highlights) {

        var textColor = this.chatModule.getChatConfig().getTextColor();
        var highlightColor = this.chatModule.getChatConfig().getHighlightColor();

        for (var highlight : highlights) {
            var colored = highlightColor + highlight + textColor;
            message = message.replaceFirst("%s", colored);
        }

        message = textColor + message;

        return this.formatMessage(message);
    }

    public String compileMessageKey(String message, String... replacements) {

        var textColor = this.chatModule.getChatConfig().getTextColor();
        var highlightColor = this.chatModule.getChatConfig().getHighlightColor();

        // Sicherstellen, dass die Anzahl der Parameter stimmt (key, value Paare)
        if (replacements.length % 2 != 0) {
            this.chatModule.getRevitalizeCore().getLogger().warning(
                    "[MessageUtil] Ungerade Anzahl von Parametern in compileMessageKey(). " +
                            "Die Methode erwartet key/value Paare."
            );
        }

        for (int i = 0; i < replacements.length - 1; i += 2) {

            var key = replacements[i];
            var value = replacements[i + 1];

            var coloredValue = highlightColor + value + textColor;

            message = message.replace(key, coloredValue);
        }

        message = textColor + message;

        // Prefix anwenden
        return this.formatMessage(message);
    }

    @SuppressWarnings("deprecation")
    private String formatMessage(String message) {
        var full = this.prefix.replace("%message%", message).replace("%prefix%", "");
        return ChatColor.translateAlternateColorCodes('&', full);
    }

    public void notifyStaff(String message, String... highlight) {
        this.chatModule.getRevitalizeCore().getServer().getOnlinePlayers().forEach(player -> {
            if (!this.chatModule.getChatConfig().getStaffNotifyEnabled()) return;
            if (!player.hasPermission("revitalize.chat.notify")) return;

            var consoleSender = this.chatModule.getRevitalizeCore().getServer().getConsoleSender();

            if (this.chatModule.getChatConfig().getStaffSplitterEnabled()) {
                var splitMessage = this.chatModule.getChatConfig().getStaffSplitterText();

                player.sendMessage(this.compileMessage(splitMessage));
                player.sendMessage(this.compileMessage(message, highlight));
                player.sendMessage(this.compileMessage(splitMessage));

                consoleSender.sendMessage(this.compileMessage(splitMessage));
                consoleSender.sendMessage(this.compileMessage(message, highlight));
                consoleSender.sendMessage(this.compileMessage(splitMessage));

                return;
            }

            player.sendMessage(this.compileMessage(message, highlight));
            consoleSender.sendMessage(this.compileMessage(message, highlight));

        });
    }

    public void notifyStaffKey(String message, String... highlight) {
        this.chatModule.getRevitalizeCore().getServer().getOnlinePlayers().forEach(player -> {
            if (!this.chatModule.getChatConfig().getStaffNotifyEnabled()) return;
            if (!player.hasPermission("revitalize.chat.notify")) return;

            var consoleSender = this.chatModule.getRevitalizeCore().getServer().getConsoleSender();

            if (this.chatModule.getChatConfig().getStaffSplitterEnabled()) {
                var splitMessage = this.chatModule.getChatConfig().getStaffSplitterText();

                player.sendMessage(this.compileMessageKey(splitMessage));
                player.sendMessage(this.compileMessageKey(message, highlight));
                player.sendMessage(this.compileMessageKey(splitMessage));

                consoleSender.sendMessage(this.compileMessageKey(splitMessage));
                consoleSender.sendMessage(this.compileMessageKey(message, highlight));
                consoleSender.sendMessage(this.compileMessageKey(splitMessage));

                return;
            }

            player.sendMessage(this.compileMessageKey(message, highlight));
            consoleSender.sendMessage(this.compileMessageKey(message, highlight));
        });
    }

    public static String normalize(String input) {
        return input
                .toLowerCase()
                .replaceAll("[^a-zA-Z0-9]", "");
    }

}
