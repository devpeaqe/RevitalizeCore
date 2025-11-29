package de.peaqe.revitalizecore.modules.player.listener;

import de.peaqe.revitalizecore.modules.player.PlayerModule;
import de.peaqe.revitalizecore.modules.player.objects.PlayerObject;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 28.11.2025 | 16:03 Uhr
 * *
 */

public class PlayerJoinListener implements Listener {

    private final PlayerModule playerModule;

    public PlayerJoinListener(PlayerModule playerModule) {
        this.playerModule = playerModule;
        playerModule.getRevitalizeCore().getServer().getPluginManager()
                .registerEvents(this, playerModule.getRevitalizeCore());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        var player = event.getPlayer();
        var uuid = player.getUniqueId().toString();
        var repo = playerModule.getPlayerRepository();
        var db = playerModule.getRevitalizeCore().getHikariDatabaseProvider();

        Bukkit.getScheduler().runTaskAsynchronously(
                playerModule.getRevitalizeCore(),
                () -> {

                    var object = repo.get(uuid, db);

                    if (object == null) {
                        object = new PlayerObject(player.getName(), player.getUniqueId(), 1000);
                        repo.update(uuid, object);
                        repo.save(uuid, object, db);

                        Bukkit.getLogger().info("Neuer Spieler gespeichert: " + player.getName());
                    }
                }
        );
    }

}
