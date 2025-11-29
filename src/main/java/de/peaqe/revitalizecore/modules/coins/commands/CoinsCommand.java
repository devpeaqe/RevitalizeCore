package de.peaqe.revitalizecore.modules.coins.commands;

import de.peaqe.revitalizecore.modules.coins.CoinsModule;
import de.peaqe.revitalizecore.modules.player.objects.PlayerObject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 29.11.2025 | 01:18 Uhr
 * *
 */

public class CoinsCommand implements CommandExecutor, TabExecutor {

    private final CoinsModule coinsModule;

    public CoinsCommand(CoinsModule coinsModule) {
        this.coinsModule = coinsModule;
        this.coinsModule.getRevitalizeCore().getServer().getPluginCommand("coins").setExecutor(this);
        this.coinsModule.getRevitalizeCore().getServer().getPluginCommand("coins").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        // TODO: Check permission

        if (args.length == 1) {

            var targetName = args[0];
            var targetUniqueId = this.coinsModule.getRevitalizeCore().getServer().getPlayerUniqueId(targetName);

            if (targetUniqueId == null) {
                sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                        "Der Spieler %s konnte nicht gefunden werden!",
                        targetName
                ));
                return true;
            }


            var playerRepository = this.coinsModule.getRevitalizeCore().getDatabaseManager().getRepository("player");

            var targetObject = (PlayerObject) playerRepository.load(targetUniqueId.toString(),
                    this.coinsModule.getRevitalizeCore().getHikariDatabaseProvider());

            if (targetObject == null) {
                sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                        "Der Spieler %s ist nicht auf dem %s registriert!",
                        targetName, "Server"
                ));
                return true;
            }

            sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                    "§8=================================="
            ));
            sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                    "Name: %s", targetObject.name()
            ));
            sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                    "Coins: %s", String.valueOf(targetObject.coins())
            ));
            sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                    "§8=================================="
            ));


        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of();
    }
}
