package de.peaqe.revitalizecore;

import de.peaqe.revitalizecore.command.RevitalizeCommand;
import de.peaqe.revitalizecore.command.ModuleCommand;
import de.peaqe.revitalizecore.command.RepositoryCommand;
import de.peaqe.revitalizecore.database.DatabaseManager;
import de.peaqe.revitalizecore.database.HikariDatabaseProvider;
import de.peaqe.revitalizecore.framework.loader.ModuleLoader;
import de.peaqe.revitalizecore.logging.RevitalizeLogger;
import de.peaqe.revitalizecore.utils.MessageUtil;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class RevitalizeCore extends JavaPlugin {

    private HikariDatabaseProvider hikariDatabaseProvider;
    private DatabaseManager databaseManager;
    private RevitalizeLogger revitalizeLogger;
    private MessageUtil messageUtil;

    @Override
    public void onEnable() {

        this.saveDefaultConfig();

        this.revitalizeLogger = new RevitalizeLogger(this.getDataFolder());

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

        this.messageUtil = new MessageUtil(this);

        // Command's
        new RevitalizeCommand(this);
        new ModuleCommand(this);
        new RepositoryCommand(this);

    }

    @Override
    public void onDisable() {
        this.databaseManager.shutdown();
    }

}
