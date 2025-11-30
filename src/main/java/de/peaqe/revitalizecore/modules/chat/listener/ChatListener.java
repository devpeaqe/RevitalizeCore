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

        this.chatModule.getLogger().info("ChatListener registered.");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncChat(AsyncChatEvent event) {

        this.chatModule.getLogger().debug("AsyncChatEvent triggered by " + event.getPlayer().getName());

        if (!this.chatModule.getChatConfig().getEnabled()) {
            this.chatModule.getLogger().debug("Chat filter disabled, ignoring message.");
            return;
        }

        var player = event.getPlayer();
        var messageComponent = event.originalMessage();
        var plainTextMessage = PlainTextComponentSerializer.plainText().serialize(messageComponent);

        this.chatModule.getLogger().debug("Message received: \"" + plainTextMessage + "\" from " + player.getName());

        if (player.hasPermission("revitalize.chat.bypass")) {
            this.chatModule.getLogger().debug(player.getName() + " has bypass permission. Ignoring.");
            return;
        }

        if (plainTextMessage.startsWith("/") &&
                !plainTextMessage.matches("^/(msg|tell|w|whisper|pm|m|r)\\b.*")) {
            this.chatModule.getLogger().debug("Message is a command → ignored.");
            return;
        }

        var badWords = chatModule.getChatConfig().getBadWords();

        var normalizedMessage = plainTextMessage.toLowerCase()
                .replaceAll("[^a-zA-Z0-9äöüß ]", "")
                .trim();

        var normalizedWords = normalizedMessage.split("\\s+");

        for (var word : normalizedWords) {

            for (var bad : badWords) {

                var p = Pattern.compile("\\b" + Pattern.quote(bad.toLowerCase()) + "\\b");
                if (p.matcher(word).find()) {

                    this.chatModule.getLogger().warn("Bad word detected: \"" + bad + "\" in message from " + player.getName());

                    if (this.chatModule.getChatConfig().getFilterMode().equals(FilterMode.BLOCK)) {

                        this.chatModule.getLogger().info("Blocking message from " + player.getName());

                        player.sendMessage(chatModule.getMessageUtil().compileMessage(
                                chatModule.getChatConfig().getBlockMessage()
                        ));

                        var notifyMsg = chatModule.getChatConfig().getStaffNotifyText();
                        this.chatModule.getLogger().debug("Notifying staff: " + notifyMsg);

                        this.chatModule.getMessageUtil().notifyStaffKey(
                                notifyMsg,
                                "%player%", player.getName(),
                                "%message%", plainTextMessage
                        );

                        event.setCancelled(true);
                        this.chatModule.getLogger().debug("Event cancelled.");
                        return;
                    }

                    if (this.chatModule.getChatConfig().getFilterMode().equals(FilterMode.REPLACE)) {

                        this.chatModule.getLogger().info("Replacing bad word in message from " + player.getName());

                        var replacement = this.chatModule.getChatConfig().getReplacement();
                        var textColor = this.chatModule.getChatConfig().getTextColor();

                        var replacedMessage = plainTextMessage.replaceAll(
                                "(?i)\\b" + Pattern.quote(bad) + "\\b",
                                replacement
                        );

                        replacedMessage = textColor + replacedMessage;

                        this.chatModule.getLogger().debug("Replaced message: \"" + replacedMessage + "\"");

                        event.message(Component.text(replacedMessage));

                        return;
                    }

                    this.chatModule.getLogger().debug("No filter mode matched. Returning.");
                    return;
                }
            }
        }

        this.chatModule.getLogger().debug("Message contains no bad words.");
    }

}
