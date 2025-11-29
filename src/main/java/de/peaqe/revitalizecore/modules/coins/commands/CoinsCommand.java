package de.peaqe.revitalizecore.modules.coins.commands;

import de.peaqe.revitalizecore.cache.CacheRepository;
import de.peaqe.revitalizecore.database.HikariDatabaseProvider;
import de.peaqe.revitalizecore.modules.chat.utils.UsageEntry;
import de.peaqe.revitalizecore.modules.coins.CoinsModule;
import de.peaqe.revitalizecore.modules.player.objects.PlayerObject;
import de.peaqe.revitalizecore.modules.player.repository.PlayerRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final HikariDatabaseProvider hikariDatabaseProvider;
    private CacheRepository<PlayerObject> playerRepository;

    public CoinsCommand(CoinsModule coinsModule) {
        this.coinsModule = coinsModule;
        this.hikariDatabaseProvider = this.coinsModule.getRevitalizeCore().getHikariDatabaseProvider();

        var pluginCommand = this.coinsModule.getRevitalizeCore()
                .getServer().getPluginCommand("coins");

        if (pluginCommand == null) return;

        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {

        // /coins
        if (args.length == 0) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                        "Dieser Befehl kann nur von %s ausgeführt werden!",
                        "Spielern"
                ));
                return true;
            }

            var playerObject = this.getPlayerRepository().load(player.getUniqueId().toString(),
                    this.hikariDatabaseProvider);

            if (playerObject == null) {
                player.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                        "%s", "Es ist ein Fehler aufgetreten, bitte versuche es später erneut!"
                ));
                this.coinsModule.getRevitalizeCore().getLogger().severe(
                        String.format("PlayerObject from %s is null", player.getName())
                );
                return true;
            }

            player.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                    "Du hast %s Coins.", String.valueOf(playerObject.getCoins())
            ));

            return true;
        }

        // /coins check|info <player>
        // /coins reset <player>
        if (args.length == 2) {

            // check/info
            if ((args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("info"))
                    && sender.hasPermission("revitalize.command.coins.check")) {

                var optionalTarget = this.getPlayerObjectFromName(sender, args[1]);

                if (optionalTarget.isEmpty()) return true;

                var target = optionalTarget.get();

                sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                        "Der Spieler %s hat %s Coins.",
                        target.getName(), String.valueOf(target.getCoins())
                ));

                return true;
            }

            // reset
            if (args[0].equalsIgnoreCase("reset")
                    && sender.hasPermission("revitalize.command.coins.reset")) {

                var optionalTarget = this.getPlayerObjectFromName(sender, args[1]);

                if (optionalTarget.isEmpty()) return true;

                var target = optionalTarget.get();
                target.setCoins(CoinsModule.DEFAULT_COINS);

                this.saveAsync(target);

                sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                        "Die Coins von %s wurden zurückgesetzt.", target.getName()
                ));

                return true;
            }

            this.sendUsage(sender);
            return true;
        }

        // /coins add|remove|set <player> <amount>
        if (args.length == 3) {

            var sub = args[0].toLowerCase();
            if (!(sub.equals("add") || sub.equals("remove") || sub.equals("set"))) {
                this.sendUsage(sender);
                return true;
            }

            // Permission check
            if (!sender.hasPermission("revitalize.command.coins." + sub)) {
                this.sendUsage(sender);
                return true;
            }

            // Amount check
            if (!this.isAllDigits(args[2])) {
                sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                        "Bitte gebe eine gültige Zahl an!"
                ));
                return true;
            }

            var coins = Integer.parseInt(args[2]);
            var optionalTarget = this.getPlayerObjectFromName(sender, args[1]);

            if (optionalTarget.isEmpty()) return true;

            var target = optionalTarget.get();
            var before = target.getCoins();

            // add
            if (sub.equals("add")) target.setCoins(before + coins);

            // remove
            else if (sub.equals("remove")) target.setCoins(Math.max(before - coins, 0));

            // set
            else target.setCoins(coins);

            this.saveAsync(target);

            sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                    "Die Coins von %s wurden auf %s gesetzt.",
                    target.getName(), String.valueOf(target.getCoins())
            ));

            return true;
        }

        this.sendUsage(sender);
        return false;
    }

    // -------------------------------------------------------
    // TabComplete
    // -------------------------------------------------------

    @Nullable
    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {

        // /coins (sub-command)
        if (args.length == 1) {
            var list = new ArrayList<String>();

            if (sender.hasPermission("revitalize.command.coins.check")) {
                list.add("check");
                list.add("info");
            }

            if (sender.hasPermission("revitalize.command.coins.reset"))
                list.add("reset");

            if (sender.hasPermission("revitalize.command.coins.add"))
                list.add("add");

            if (sender.hasPermission("revitalize.command.coins.remove"))
                list.add("remove");

            if (sender.hasPermission("revitalize.command.coins.set"))
                list.add("set");

            return list;
        }

        // /coins <sub> <player>
        if (args.length == 2) {

            var list = new ArrayList<String>();
            var input = args[1].toLowerCase();
            var sub = args[0].toLowerCase();

            boolean allow = (sub.equals("check") || sub.equals("info")) &&
                    sender.hasPermission("revitalize.command.coins.check")
                    || sub.equals("reset") && sender.hasPermission("revitalize.command.coins.reset")
                    || sub.equals("add") && sender.hasPermission("revitalize.command.coins.add")
                    || sub.equals("remove") && sender.hasPermission("revitalize.command.coins.remove")
                    || sub.equals("set") && sender.hasPermission("revitalize.command.coins.set");

            if (!allow) return list;

            this.coinsModule.getRevitalizeCore().getServer().getOnlinePlayers().forEach(player -> {
                if (player.getName().toLowerCase().startsWith(input)) list.add(player.getName());
            });

            return list;
        }

        return List.of();
    }

    // -------------------------------------------------------
    // Helper
    // -------------------------------------------------------

    private Optional<PlayerObject> getPlayerObjectFromName(CommandSender sender, String name) {
        var object = this.getPlayerRepository().loadByName(name, this.hikariDatabaseProvider);

        if (object == null) {
            sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                    "Der Spieler %s ist nicht registriert!", name
            ));
            return Optional.empty();
        }

        return Optional.of(object);
    }

    private void saveAsync(PlayerObject target) {
        var uuid = target.getUniqueId().toString();
        this.getPlayerRepository().save(uuid, target, this.hikariDatabaseProvider);
    }

    private boolean isAllDigits(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++)
            if (!Character.isDigit(s.charAt(i))) return false;
        return true;
    }

    private PlayerRepository getPlayerRepository() {
        if (this.playerRepository == null) {
            this.playerRepository = this.coinsModule.getRevitalizeCore()
                    .getDatabaseManager().getRepository("player");
        }
        return (PlayerRepository) this.playerRepository;
    }

    private void sendUsage(CommandSender sender) {

        var entries = List.of(
                new UsageEntry("", " "),
                new UsageEntry("check", "check <player>"),
                new UsageEntry("reset", "reset <player>"),
                new UsageEntry("add", "add <player> <amount"),
                new UsageEntry("remove", "remove <player> <amount>"),
                new UsageEntry("set", "set <player> <amount>")
        );

        entries.forEach(entry -> {
            if (sender.hasPermission("revitalize.command.coins." + entry.permission()) ||
                    entry.permission().isEmpty()) {
                sender.sendMessage(
                        this.coinsModule.getMessageUtil().compileMessage(
                                "Bitte verwende: %s",
                                "/coins " + entry.usage()
                        )
                );
            }
        });
    }

}
