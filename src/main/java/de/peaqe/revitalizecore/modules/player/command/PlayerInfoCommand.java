package de.peaqe.revitalizecore.modules.player.command;

import de.peaqe.revitalizecore.modules.player.PlayerModule;
import de.peaqe.revitalizecore.modules.player.repository.PlayerRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 29.11.2025 | 00:56 Uhr
 * *
 */

public class PlayerInfoCommand implements CommandExecutor, TabExecutor {

    private final PlayerModule playerModule;
    private final PlayerRepository playerRepository;

    public PlayerInfoCommand(PlayerModule playerModule) {
        this.playerModule = playerModule;
        this.playerRepository = this.playerModule.getPlayerRepository();
        this.playerModule.getRevitalizeCore().getServer().getPluginCommand("playerinfo").setExecutor(this);
        this.playerModule.getRevitalizeCore().getServer().getPluginCommand("playerinfo").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        // TODO: Permission check

        if (args.length == 1) {

            var targetName = args[0];
            var targetUniqueId = this.playerModule.getRevitalizeCore().getServer().getPlayerUniqueId(targetName);

            if (targetUniqueId == null) {
                sender.sendMessage(this.playerModule.getMessageUtil().compileMessage(
                        "Der Spieler %s konnte nicht gefunden werden!",
                        targetName
                ));
                return true;
            }

            var targetObject = this.playerRepository.load(targetUniqueId.toString(),
                    this.playerModule.getRevitalizeCore().getHikariDatabaseProvider());

            if (targetObject == null) {
                sender.sendMessage(this.playerModule.getMessageUtil().compileMessage(
                        "Der Spieler %s ist nicht auf dem %s registriert!",
                        targetName, "Server"
                ));
                return true;
            }

            sender.sendMessage(this.playerModule.getMessageUtil().compileMessage(
                    "§8=================================="
            ));
            sender.sendMessage(this.playerModule.getMessageUtil().compileMessage(
                    "Name: %s", targetObject.getName()
            ));
            sender.sendMessage(this.playerModule.getMessageUtil().compileMessage(
                    "UUID: %s", targetObject.getUniqueId().toString()
            ));
            sender.sendMessage(this.playerModule.getMessageUtil().compileMessage(
                    "Coins: %s", String.valueOf(targetObject.getCoins())
            ));
            sender.sendMessage(this.playerModule.getMessageUtil().compileMessage(
                    "§8=================================="
            ));

        }

        // TODO: Usage message

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        var list = new ArrayList<String>();

        // TODO: Permission check

        if (args.length == 1) {
            var input = args[0];
            this.playerModule.getRevitalizeCore().getServer().getOnlinePlayers().forEach(player -> {
                if (player.getName().toLowerCase().startsWith(input.toLowerCase())) list.add(player.getName());
            });
        }

        return list;
    }
}
