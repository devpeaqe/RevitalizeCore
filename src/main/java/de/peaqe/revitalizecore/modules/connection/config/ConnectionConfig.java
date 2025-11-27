package de.peaqe.revitalizecore.modules.connection.config;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.modules.connection.ConnectionModule;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 26.11.2025 | 18:47 Uhr
 * *
 */

public class ConnectionConfig {

    private final ConnectionModule connectionModule;
    private final RevitalizeCore revitalizeCore;

    private final File file;
    private final FileConfiguration fileConfiguration;

    public ConnectionConfig(ConnectionModule connectionModule) {
        this.connectionModule = connectionModule;
        this.revitalizeCore = this.connectionModule.getRevitalizeCore();
        this.file = new File(this.revitalizeCore.getDataFolder(), "connection.yml");
        this.fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    // ------------------------------------------

    // CHAT

    // ------------------------------------------

    public String getPrefix() {
        return this.fileConfiguration.getString("chat.prefix").replace('&', '§');
    }

    public String getTextColor() {
        return this.fileConfiguration.getString("chat.textColor").replace('&', '§');
    }

    public String getHighlightColor() {
        return this.fileConfiguration.getString("chat.highlightColor").replace('&', '§');
    }

    // ------------------------------------------

    // CHAT

    // ------------------------------------------

    // CONNECTION

    // ------------------------------------------

    public boolean isJoinEnabled() {
        return this.fileConfiguration.getBoolean("connection.join.enabled");
    }

    public String getJoinMessage() {
        return this.fileConfiguration.getString("connection.join.message").replace('&', '§');
    }

    public boolean isQuitEnabled() {
        return this.fileConfiguration.getBoolean("connection.quit.enabled");
    }

    public String getQuitMessage() {
        return this.fileConfiguration.getString("connection.quit.message").replace('&', '§');
    }

    // ------------------------------------------

    // CONNECTION

    // ------------------------------------------

}
