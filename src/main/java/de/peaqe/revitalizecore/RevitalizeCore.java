package de.peaqe.revitalizecore;

import de.peaqe.revitalizecore.command.RepositoryCommand;
import de.peaqe.revitalizecore.database.DatabaseManager;
import de.peaqe.revitalizecore.database.HikariDatabaseProvider;
import de.peaqe.revitalizecore.framework.loader.ModuleLoader;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class RevitalizeCore extends JavaPlugin {

    private HikariDatabaseProvider hikariDatabaseProvider;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {

        this.saveDefaultConfig();

        this.hikariDatabaseProvider = new HikariDatabaseProvider(
                this.getConfig().getString("database.hostname"),
                this.getConfig().getInt("database.port"),
                this.getConfig().getString("database.database"),
                this.getConfig().getString("database.username"),
                this.getConfig().getString("database.password")
        );

        this.databaseManager = new DatabaseManager(this.hikariDatabaseProvider);
        this.databaseManager.startAutoRefresh(30);

        var loadedModules = ModuleLoader.loadModules(this);
        this.getLogger().info("Loaded " + loadedModules.size() + " modules.");

        // Command's
        new RepositoryCommand(this);

    }

    @Override
    public void onDisable() {
        this.databaseManager.shutdown();
    }

}
