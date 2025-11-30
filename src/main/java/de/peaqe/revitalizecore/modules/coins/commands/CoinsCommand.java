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

        this.coinsModule.getLogger().info("CoinsCommand registriert.");
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {

        this.coinsModule.getLogger().info(sender.getName() + " führt /coins aus: " + String.join(" ", args));

        if (args.length == 0) {

            if (!(sender instanceof Player player)) {

                this.coinsModule.getLogger().warn(sender.getName() + " versuchte /coins außerhalb des Spiels auszuführen.");

                sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                        "Dieser Befehl kann nur von %s ausgeführt werden!",
                        "Spielern"
                ));
                return true;
            }

            this.coinsModule.getLogger().debug("Lade Coins von " + player.getName());

            var playerObject = this.getPlayerRepository().load(
                    player.getUniqueId().toString(),
                    this.hikariDatabaseProvider
            );

            if (playerObject == null) {

                this.coinsModule.getLogger().error("PlayerObject ist null für " + player.getName());

                player.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                        "%s", "Es ist ein Fehler aufgetreten, bitte versuche es später erneut!"
                ));
                return true;
            }

            this.coinsModule.getLogger().debug("Coins von " + player.getName() + ": " + playerObject.getCoins());

            player.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                    "Du hast %s Coins.", String.valueOf(playerObject.getCoins())
            ));

            return true;
        }

        if (args.length == 2) {

            var sub = args[0].toLowerCase();
            var targetName = args[1];

            this.coinsModule.getLogger().debug("Subcommand: " + sub + ", Target: " + targetName);

            if ((sub.equals("check") || sub.equals("info"))
                    && sender.hasPermission("revitalize.command.coins.check")) {

                var optionalTarget = this.getPlayerObjectFromName(sender, targetName);

                if (optionalTarget.isEmpty()) {
                    this.coinsModule.getLogger().warn("Target nicht gefunden: " + targetName);
                    return true;
                }

                var target = optionalTarget.get();

                this.coinsModule.getLogger().info(sender.getName() + " prüft Coins von " + target.getName());

                sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                        "Der Spieler %s hat %s Coins.",
                        target.getName(), String.valueOf(target.getCoins())
                ));

                return true;
            }

            if (sub.equals("reset")
                    && sender.hasPermission("revitalize.command.coins.reset")) {

                var optionalTarget = this.getPlayerObjectFromName(sender, targetName);

                if (optionalTarget.isEmpty()) {
                    this.coinsModule.getLogger().warn("Target nicht gefunden: " + targetName);
                    return true;
                }

                var target = optionalTarget.get();
                target.setCoins(CoinsModule.DEFAULT_COINS);

                this.saveAsync(target);

                this.coinsModule.getLogger().info(sender.getName() + " hat die Coins von " + target.getName() + " zurückgesetzt.");

                sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                        "Die Coins von %s wurden zurückgesetzt.", target.getName()
                ));

                return true;
            }

            this.coinsModule.getLogger().warn(sender.getName() + " nutzte ungültigen Subcommand: " + sub);

            this.sendUsage(sender);
            return true;
        }

        if (args.length == 3) {

            var sub = args[0].toLowerCase();
            var targetName = args[1];
            var amountString = args[2];

            this.coinsModule.getLogger().debug("Add/Remove/Set: " + sub + ", Target: " + targetName + ", Amount: " + amountString);

            if (!(sub.equals("add") || sub.equals("remove") || sub.equals("set"))) {
                this.coinsModule.getLogger().warn("Ungültiges Subcommand: " + sub);
                this.sendUsage(sender);
                return true;
            }

            if (!sender.hasPermission("revitalize.command.coins." + sub)) {
                this.coinsModule.getLogger().warn(sender.getName() + " hat keine Permission für /coins " + sub);
                this.sendUsage(sender);
                return true;
            }

            if (!this.isAllDigits(amountString)) {
                this.coinsModule.getLogger().warn("Ungültige Zahl von " + sender.getName() + ": " + amountString);

                sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                        "Bitte gebe eine gültige Zahl an!"
                ));
                return true;
            }

            var amount = Integer.parseInt(amountString);
            var optionalTarget = this.getPlayerObjectFromName(sender, targetName);

            if (optionalTarget.isEmpty()) {
                this.coinsModule.getLogger().warn("Spieler '" + targetName + "' nicht gefunden.");
                return true;
            }

            var target = optionalTarget.get();
            var before = target.getCoins();

            if (sub.equals("add")) {
                target.setCoins(before + amount);
                this.coinsModule.getLogger().info(sender.getName() + " add " + amount + " Coins zu " + target.getName());
            } else if (sub.equals("remove")) {
                target.setCoins(Math.max(before - amount, 0));
                this.coinsModule.getLogger().info(sender.getName() + " remove " + amount + " Coins von " + target.getName());
            } else {
                target.setCoins(amount);
                this.coinsModule.getLogger().info(sender.getName() + " set Coins von " + target.getName() + " auf " + amount);
            }

            this.saveAsync(target);

            this.coinsModule.getLogger().debug("Vorher: " + before + ", Nachher: " + target.getCoins());

            sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                    "Die Coins von %s wurden auf %s gesetzt.",
                    target.getName(), String.valueOf(target.getCoins())
            ));

            return true;
        }

        this.sendUsage(sender);
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {

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

    private Optional<PlayerObject> getPlayerObjectFromName(CommandSender sender, String name) {
        var object = this.getPlayerRepository().loadByName(name, this.hikariDatabaseProvider);

        if (object == null) {
            this.coinsModule.getLogger().warn("PlayerObject nicht gefunden: " + name);

            sender.sendMessage(this.coinsModule.getMessageUtil().compileMessage(
                    "Der Spieler %s ist nicht registriert!", name
            ));
            return Optional.empty();
        }

        return Optional.of(object);
    }

    private void saveAsync(PlayerObject target) {
        var uuid = target.getUniqueId().toString();
        this.coinsModule.getLogger().debug("Speichere Coins für " + target.getName());
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
