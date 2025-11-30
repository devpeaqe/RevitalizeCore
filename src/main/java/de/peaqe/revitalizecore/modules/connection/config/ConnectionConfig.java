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

        this.connectionModule.getLogger().info("ConnectionConfig loaded from " + file.getName());
    }

    // ------------------------------------------
    // CHAT
    // ------------------------------------------

    public String getPrefix() {
        var value = this.fileConfiguration.getString("chat.prefix");
        if (value == null) {
            this.connectionModule.getLogger().warn("Missing config value: chat.prefix");
            return "";
        }
        var result = value.replace('&', '§');
        this.connectionModule.getLogger().debug("getPrefix() → " + result);
        return result;
    }

    public String getTextColor() {
        var value = this.fileConfiguration.getString("chat.textColor");
        if (value == null) {
            this.connectionModule.getLogger().warn("Missing config value: chat.textColor");
            return "";
        }
        var result = value.replace('&', '§');
        this.connectionModule.getLogger().debug("getTextColor() → " + result);
        return result;
    }

    public String getHighlightColor() {
        var value = this.fileConfiguration.getString("chat.highlightColor");
        if (value == null) {
            this.connectionModule.getLogger().warn("Missing config value: chat.highlightColor");
            return "";
        }
        var result = value.replace('&', '§');
        this.connectionModule.getLogger().debug("getHighlightColor() → " + result);
        return result;
    }

    // ------------------------------------------
    // CHAT
    // ------------------------------------------

    // CONNECTION
    // ------------------------------------------

    public boolean isJoinEnabled() {
        var result = this.fileConfiguration.getBoolean("connection.join.enabled");
        this.connectionModule.getLogger().debug("isJoinEnabled() → " + result);
        return result;
    }

    public String getJoinMessage() {
        var value = this.fileConfiguration.getString("connection.join.message");
        if (value == null) {
            this.connectionModule.getLogger().warn("Missing config value: connection.join.message");
            return "";
        }
        var result = value.replace('&', '§');
        this.connectionModule.getLogger().debug("getJoinMessage() → " + result);
        return result;
    }

    public boolean isQuitEnabled() {
        var result = this.fileConfiguration.getBoolean("connection.quit.enabled");
        this.connectionModule.getLogger().debug("isQuitEnabled() → " + result);
        return result;
    }

    public String getQuitMessage() {
        var value = this.fileConfiguration.getString("connection.quit.message");
        if (value == null) {
            this.connectionModule.getLogger().warn("Missing config value: connection.quit.message");
            return "";
        }
        var result = value.replace('&', '§');
        this.connectionModule.getLogger().debug("getQuitMessage() → " + result);
        return result;
    }

    // ------------------------------------------
    // CONNECTION
    // ------------------------------------------

}
