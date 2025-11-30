package de.peaqe.revitalizecore.modules.connection.listener;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.modules.connection.ConnectionModule;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 26.11.2025 | 18:51 Uhr
 * *
 */

public class QuitListener implements Listener {

    private final ConnectionModule connectionModule;
    private final RevitalizeCore revitalizeCore;

    public QuitListener(ConnectionModule connectionModule) {
        this.connectionModule = connectionModule;
        this.revitalizeCore = this.connectionModule.getRevitalizeCore();
        this.revitalizeCore.getServer().getPluginManager().registerEvents(this, this.revitalizeCore);

        this.connectionModule.getLogger().info("QuitListener registered.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        this.connectionModule.getLogger().debug("PlayerQuitEvent triggered for " + event.getPlayer().getName());

        event.quitMessage(Component.empty());

        if (!this.connectionModule.getConnectionConfig().isQuitEnabled()) {
            this.connectionModule.getLogger().debug("Quit messages disabled → ignoring quit message for " + event.getPlayer().getName());
            return;
        }

        // ============================================================
        // SECTION 1 : MESSAGING
        // ============================================================

        var quitMessage = this.connectionModule.getConnectionConfig().getQuitMessage();
        this.connectionModule.getLogger().debug("Raw quit message: " + quitMessage);

        var prefixedQuitMessage = this.connectionModule.getMessageUtil().compileMessageKey(
                quitMessage,
                "%player%", event.getPlayer().getName()
        );

        this.connectionModule.getLogger().info("Quit message compiled for " + event.getPlayer().getName() + ": " + prefixedQuitMessage);

        this.revitalizeCore.getServer().getConsoleSender().sendMessage(prefixedQuitMessage);
        this.connectionModule.getLogger().debug("Quit message sent to console.");

        this.revitalizeCore.getServer().getOnlinePlayers().forEach(
                player -> {
                    player.sendMessage(prefixedQuitMessage);
                    this.connectionModule.getLogger().debug("Quit message sent to player " + player.getName());
                }
        );

        // ============================================================
        // SECTION 1 : MESSAGING
        // ============================================================


        // ============================================================
        // SECTION 2
        // ============================================================

        // TODO: Effects or something else

        // ============================================================
        // SECTION 2
        // ============================================================

    }

}
