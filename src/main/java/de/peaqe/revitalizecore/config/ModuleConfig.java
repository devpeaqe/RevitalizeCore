package de.peaqe.revitalizecore.config;

import de.peaqe.revitalizecore.RevitalizeCore;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 26.11.2025 | 19:49 Uhr
 * *
 */

@Getter
public class ModuleConfig {

    private final RevitalizeCore core;
    private final FileConfiguration config;

    public ModuleConfig(RevitalizeCore core) {
        this.core = core;
        this.config = core.getConfig();
    }

    public boolean isModuleEnabled(String name) {
        return this.config.getBoolean("modules." + name + ".enabled");
    }

    public boolean hasModule(String name) {
        return this.config.isConfigurationSection("modules." + name);
    }

    public void addModule(String name, boolean enabledByDefault) {
        this.config.set("modules." + name + ".enabled", enabledByDefault);
    }

    public void save() {
        this.core.saveConfig();
    }
}
