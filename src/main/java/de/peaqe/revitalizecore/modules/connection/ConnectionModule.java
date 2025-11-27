package de.peaqe.revitalizecore.modules.connection;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.framework.annotation.RevitalizeModule;
import de.peaqe.revitalizecore.modules.connection.config.ConnectionConfig;
import de.peaqe.revitalizecore.modules.connection.listener.JoinListener;
import de.peaqe.revitalizecore.modules.connection.listener.QuitListener;
import de.peaqe.revitalizecore.utils.MessageUtil;
import lombok.Getter;

import java.io.File;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 26.11.2025 | 18:43 Uhr
 * *
 */

@Getter
@RevitalizeModule(
        name = "connection",
        enabledByDefault = true
)
public class ConnectionModule {

    private RevitalizeCore revitalizeCore;

    private ConnectionConfig connectionConfig;
    private MessageUtil messageUtil;

    private JoinListener joinListener;
    private QuitListener quitListener;

    public ConnectionModule() {}

    public void onLoad(RevitalizeCore revitalizeCore) {
        this.revitalizeCore = revitalizeCore;
        if (!(new File(this.revitalizeCore.getDataFolder(), "connection.yml").exists()))
            this.revitalizeCore.saveResource("connection.yml", false);
    }

    public void onEnable(RevitalizeCore core) {
        this.connectionConfig = new ConnectionConfig(this);
        this.messageUtil = new MessageUtil(
                core,
                this.connectionConfig.getPrefix(),
                this.connectionConfig.getTextColor(),
                this.connectionConfig.getHighlightColor()
        );
        this.joinListener = new JoinListener(this);
        this.quitListener = new QuitListener(this);
    }

}
