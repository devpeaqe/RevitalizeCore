package de.peaqe.revitalizecore.modules.connection.listener;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.modules.connection.ConnectionModule;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 26.11.2025 | 18:51 Uhr
 * *
 */

public class JoinListener implements Listener {

    private final ConnectionModule connectionModule;
    private final RevitalizeCore revitalizeCore;

    public JoinListener(ConnectionModule connectionModule) {
        this.connectionModule = connectionModule;
        this.revitalizeCore = this.connectionModule.getRevitalizeCore();
        this.revitalizeCore.getServer().getPluginManager().registerEvents(this, this.revitalizeCore);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        event.joinMessage(Component.empty());
        if (!this.connectionModule.getConnectionConfig().isJoinEnabled()) return;

        // ============================================================
        // SECTION 1 : MESSAGING
        // ============================================================

        var joinMessage = this.connectionModule.getConnectionConfig().getJoinMessage();
        var prefixedJoinMessage = this.connectionModule.getMessageUtil().compileMessageKey(joinMessage,
                "%player%", event.getPlayer().getName());

        this.revitalizeCore.getServer().getConsoleSender().sendMessage(prefixedJoinMessage);
        this.revitalizeCore.getServer().getOnlinePlayers().forEach(
                player -> player.sendMessage(prefixedJoinMessage)
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
