package de.peaqe.revitalizecore.modules.chat.listener;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.modules.chat.ChatModule;
import de.peaqe.revitalizecore.modules.chat.utils.FilterMode;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.regex.Pattern;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 26.11.2025 | 14:09 Uhr
 * *
 */

public class ChatListener implements Listener {

    private final ChatModule chatModule;
    private final RevitalizeCore revitalizeCore;

    public ChatListener(ChatModule chatModule) {
        this.chatModule = chatModule;
        this.revitalizeCore = this.chatModule.getRevitalizeCore();
        this.revitalizeCore.getServer().getPluginManager().registerEvents(this, this.revitalizeCore);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncChat(AsyncChatEvent event) {

        if (!this.chatModule.getChatConfig().getEnabled()) return;

        var player = event.getPlayer();
        var messageComponent = event.originalMessage();
        var plainTextMessage = PlainTextComponentSerializer.plainText().serialize(messageComponent);

        // Bypass
        if (player.hasPermission("revitalize.chat.bypass")) return;

        // Commands ignorieren außer private msg commands
        if (plainTextMessage.startsWith("/") &&
                !plainTextMessage.matches("^/(msg|tell|w|whisper|pm|m|r)\\b.*")) {
            return;
        }

        var badWords = chatModule.getChatConfig().getBadWords();

        // Normalize message for matching (lowercase, remove punctuation)
        var normalizedMessage = plainTextMessage.toLowerCase()
                .replaceAll("[^a-zA-Z0-9äöüß ]", "") // remove punctuation
                .trim();

        var normalizedWords = normalizedMessage.split("\\s+");

        for (var word : normalizedWords) {

            for (var bad : badWords) {

                // WORD DETECTION
                var p = Pattern.compile("\\b" + Pattern.quote(bad.toLowerCase()) + "\\b");
                if (p.matcher(word).find()) {

                    // BLOCK
                    if (this.chatModule.getChatConfig().getFilterMode().equals(FilterMode.BLOCK)) {

                        player.sendMessage(chatModule.getMessageUtil().compileMessage(
                                chatModule.getChatConfig().getBlockMessage()
                        ));

                        var notifyMsg = chatModule.getChatConfig().getStaffNotifyText();
                        this.chatModule.getMessageUtil().notifyStaffKey(
                                notifyMsg,
                                "%player%", player.getName(),
                                "%message%", plainTextMessage
                        );

                        event.setCancelled(true);
                        return;
                    }

                    // REPLACE
                    if (this.chatModule.getChatConfig().getFilterMode().equals(FilterMode.REPLACE)) {

                        var replacement = this.chatModule.getChatConfig().getReplacement();

                        var textColor = this.chatModule.getChatConfig().getTextColor();

                        var replacedMessage = plainTextMessage.replaceAll(
                                "(?i)\\b" + Pattern.quote(bad) + "\\b",
                                replacement
                        );

                        replacedMessage = textColor + replacedMessage;
                        event.message(Component.text(replacedMessage));

                        // Team-Notify (unverändert)
                        /*var notifyMsg = chatModule.getChatConfig().getNotify();
                        this.chatModule.getMessageUtil().notifyStaffKey(
                                chatModule.getMessageUtil().compileMessageKey(
                                        notifyMsg,
                                        "%player%", player.getName(),
                                        "%message%", replacedMessage
                                )
                        );*/

                        return;
                    }

                    return;
                }
            }
        }
    }

}
