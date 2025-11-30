package de.peaqe.revitalizecore.modules.player;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.framework.annotation.RevitalizeModule;
import de.peaqe.revitalizecore.framework.loader.ModuleBase;
import de.peaqe.revitalizecore.modules.player.command.PlayerInfoCommand;
import de.peaqe.revitalizecore.modules.player.listener.PlayerJoinListener;
import de.peaqe.revitalizecore.modules.player.listener.PlayerQuitListener;
import de.peaqe.revitalizecore.modules.player.repository.PlayerRepository;
import de.peaqe.revitalizecore.utils.MessageUtil;
import lombok.Getter;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 28.11.2025 | 15:50 Uhr
 * *
 */

@Getter
@RevitalizeModule(name = "player")
public class PlayerModule extends ModuleBase {

    private PlayerRepository playerRepository;
    private MessageUtil messageUtil;

    public PlayerModule() {
    }

    public void onLoad(RevitalizeCore revitalizeCore) {

        this.setCore(revitalizeCore);
        this.getLogger().info("Loading PlayerModule...");

        this.playerRepository = new PlayerRepository(this);
        this.getLogger().debug("PlayerRepository instance created.");

        this.getRevitalizeCore().getDatabaseManager().registerRepository(
                "player", this.playerRepository
        );
        this.getLogger().info("PlayerRepository registered under key 'player'.");

        this.playerRepository.setupTable(revitalizeCore.getHikariDatabaseProvider());
        this.getLogger().debug("PlayerRepository table setup executed.");

        this.messageUtil = new MessageUtil(this.getRevitalizeCore());
        this.getLogger().debug("MessageUtil initialized (default constructor).");

        this.getLogger().info("PlayerModule loaded.");
    }

    public void onEnable(RevitalizeCore revitalizeCore) {

        this.getLogger().info("Enabling PlayerModule...");

        new PlayerInfoCommand(this);
        this.getLogger().debug("PlayerInfoCommand registered.");

        new PlayerJoinListener(this);
        this.getLogger().debug("PlayerJoinListener registered.");

        new PlayerQuitListener(this);
        this.getLogger().debug("PlayerQuitListener registered.");

        this.getLogger().info("PlayerModule enabled.");
    }

}
