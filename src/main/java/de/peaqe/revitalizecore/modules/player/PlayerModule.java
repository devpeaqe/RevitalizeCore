package de.peaqe.revitalizecore.modules.player;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.framework.annotation.RevitalizeModule;
import de.peaqe.revitalizecore.modules.player.listener.PlayerJoinListener;
import de.peaqe.revitalizecore.modules.player.listener.PlayerQuitListener;
import de.peaqe.revitalizecore.modules.player.repository.PlayerRepository;
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
public class PlayerModule {

    private RevitalizeCore revitalizeCore;
    private PlayerRepository playerRepository;

    public PlayerModule() {
    }

    public void onLoad(RevitalizeCore revitalizeCore) {
        this.revitalizeCore = revitalizeCore;
        this.playerRepository = new PlayerRepository();
        this.revitalizeCore.getDatabaseManager().registerRepository(
                "player", this.playerRepository
        );
        this.playerRepository.setupTable(revitalizeCore.getHikariDatabaseProvider());
    }

    public void onEnable(RevitalizeCore revitalizeCore) {
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
    }

}
