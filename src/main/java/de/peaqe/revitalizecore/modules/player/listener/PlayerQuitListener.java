package de.peaqe.revitalizecore.modules.player.listener;

import de.peaqe.revitalizecore.modules.player.PlayerModule;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 28.11.2025 | 16:03 Uhr
 * *
 */

public class PlayerQuitListener implements Listener {

    private final PlayerModule playerModule;

    public PlayerQuitListener(PlayerModule playerModule) {
        this.playerModule = playerModule;
        playerModule.getRevitalizeCore().getServer().getPluginManager()
                .registerEvents(this, playerModule.getRevitalizeCore());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        var player = event.getPlayer();
        var uuid = player.getUniqueId().toString();
        var repo = playerModule.getPlayerRepository();
        var db = playerModule.getRevitalizeCore().getHikariDatabaseProvider();

        playerModule.getLogger().info("Player quit: " + player.getName());

        Bukkit.getScheduler().runTaskAsynchronously(
                playerModule.getRevitalizeCore(),
                () -> {
                    playerModule.getLogger().debug("Saving PlayerObject on quit for UUID: " + uuid);
                    var object = repo.get(uuid, db);
                    repo.save(uuid, object, db);
                    playerModule.getLogger().debug("PlayerObject saved for UUID: " + uuid);
                }
        );
    }
}