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
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        event.quitMessage(Component.empty());
        if (!this.connectionModule.getConnectionConfig().isQuitEnabled()) return;


        // ============================================================
        // SECTION 1 : MESSAGING
        // ============================================================

        var quitMessage = this.connectionModule.getConnectionConfig().getQuitMessage();
        var prefixedQuitMessage = this.connectionModule.getMessageUtil().compileMessageKey(quitMessage,
                "%player%", event.getPlayer().getName());

        this.revitalizeCore.getServer().getConsoleSender().sendMessage(prefixedQuitMessage);
        this.revitalizeCore.getServer().getOnlinePlayers().forEach(
                player -> player.sendMessage(prefixedQuitMessage)
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
