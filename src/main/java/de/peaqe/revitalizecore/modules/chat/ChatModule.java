package de.peaqe.revitalizecore.modules.chat;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.framework.annotation.RevitalizeModule;
import de.peaqe.revitalizecore.framework.loader.ModuleBase;
import de.peaqe.revitalizecore.modules.chat.commands.ChatFilterCommand;
import de.peaqe.revitalizecore.modules.chat.config.ChatConfig;
import de.peaqe.revitalizecore.modules.chat.listener.ChatListener;
import de.peaqe.revitalizecore.modules.chat.utils.MessageUtil;
import lombok.Getter;

import java.io.File;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 26.11.2025 | 14:09 Uhr
 * *
 */

@Getter
@RevitalizeModule(
        name = "chat",
        enabledByDefault = true
)
public class ChatModule extends ModuleBase {

    // ChatModule Utility classes
    private ChatConfig chatConfig;
    private MessageUtil messageUtil;

    // ChatModule Listener
    private ChatListener chatListener;

    // ChatModule Commands
    private ChatFilterCommand chatFilterCommand;

    // Wichtig: leeren Konstruktor lassen!
    public ChatModule() {}

    public void onLoad(RevitalizeCore core) {
        this.setCore(core);

        if (!(new File(core.getDataFolder(), "chat.yml").exists()))
            core.saveResource("chat.yml", false);
    }

    public void onEnable(RevitalizeCore core) {
        this.chatConfig = new ChatConfig(this);
        this.messageUtil = new MessageUtil(this);
        this.chatListener = new ChatListener(this);
        this.chatFilterCommand = new ChatFilterCommand(this);
    }

}
