package de.peaqe.revitalizecore.modules.connection;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.framework.annotation.RevitalizeModule;
import de.peaqe.revitalizecore.framework.loader.ModuleBase;
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
public class ConnectionModule extends ModuleBase {

    private ConnectionConfig connectionConfig;
    private MessageUtil messageUtil;

    private JoinListener joinListener;
    private QuitListener quitListener;

    public ConnectionModule() {}

    public void onLoad(RevitalizeCore revitalizeCore) {
        this.setCore(revitalizeCore);

        this.getLogger().info("Loading ConnectionModule...");

        var file = new File(this.getRevitalizeCore().getDataFolder(), "connection.yml");
        if (!file.exists()) {
            this.getLogger().info("connection.yml not found → creating default file.");
            this.getRevitalizeCore().saveResource("connection.yml", false);
        } else {
            this.getLogger().debug("connection.yml found.");
        }
    }

    public void onEnable(RevitalizeCore core) {

        this.getLogger().info("Enabling ConnectionModule...");

        this.connectionConfig = new ConnectionConfig(this);
        this.getLogger().debug("ConnectionConfig initialized.");

        this.messageUtil = new MessageUtil(
                core,
                this.connectionConfig.getPrefix(),
                this.connectionConfig.getTextColor(),
                this.connectionConfig.getHighlightColor()
        );
        this.getLogger().debug("MessageUtil initialized with prefix=" +
                this.connectionConfig.getPrefix() +
                ", textColor=" + this.connectionConfig.getTextColor() +
                ", highlightColor=" + this.connectionConfig.getHighlightColor()
        );

        this.joinListener = new JoinListener(this);
        this.getLogger().debug("JoinListener registered.");

        this.quitListener = new QuitListener(this);
        this.getLogger().debug("QuitListener registered.");

        this.getLogger().info("ConnectionModule enabled.");
    }

}
