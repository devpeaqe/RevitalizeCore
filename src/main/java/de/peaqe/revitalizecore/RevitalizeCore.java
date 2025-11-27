package de.peaqe.revitalizecore;

import de.peaqe.revitalizecore.framework.loader.ModuleLoader;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class RevitalizeCore extends JavaPlugin {

    @Override
    public void onEnable() {

        this.saveDefaultConfig();

        var loadedModules = ModuleLoader.loadModules(this);
        this.getLogger().info("Loaded " + loadedModules.size() + " modules.");
    }

    @Override
    public void onDisable() {

    }

}
