package de.peaqe.revitalizecore.command;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.logging.LogEntry;
import de.peaqe.revitalizecore.logging.LogLevel;
import de.peaqe.revitalizecore.utils.UsageEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 29.11.2025 | 15:59 Uhr
 * *
 */

public class RevitalizeCommand implements CommandExecutor, TabCompleter {

    private final RevitalizeCore revitalizeCore;

    public RevitalizeCommand(RevitalizeCore revitalizeCore) {
        this.revitalizeCore = revitalizeCore;

        var pluginCommand = this.revitalizeCore.getServer().getPluginCommand("revitalize");
        if (pluginCommand == null) return;

        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);
    }

    // ---------------------------------------------------------------------------------
    // Command
    // ---------------------------------------------------------------------------------

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {

        // /revitalize logs
        if (args.length == 1 && args[0].equalsIgnoreCase("logs")) {

            var logs = this.revitalizeCore.getRevitalizeLogger().getRecentLogs(null, null, 30);

            this.sendLogList(sender, logs);
            return true;
        }

        // /revitalize logs <module>
        if (args.length == 2 && args[0].equalsIgnoreCase("logs")) {

            var module = args[1];

            var logs = this.revitalizeCore.getRevitalizeLogger().getRecentLogs(module, null, 30);

            this.sendLogList(sender, logs);
            return true;
        }

        // /revitalize logs <module> <level>
        if (args.length == 3 && args[0].equalsIgnoreCase("logs")) {

            var module = args[1];
            var level = LogLevel.fromString(args[2]);

            if (level == null) {
                sender.sendMessage(this.revitalizeCore.getMessageUtil().compileMessage(
                        "Bitte gebe ein gültiges Level an! Zulässig: INFO, WARN, ERROR, DEBUG."
                ));
                return true;
            }

            var logs = this.revitalizeCore.getRevitalizeLogger().getRecentLogs(module, level, 30);

            this.sendLogList(sender, logs);
            return true;
        }

        this.sendUsage(sender);
        return true;
    }

    // ---------------------------------------------------------------------------------
    // TabComplete
    // ---------------------------------------------------------------------------------

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {

        // /revitalize <sub>
        if (args.length == 1) {
            var list = new ArrayList<String>();

            if ("logs".startsWith(args[0].toLowerCase()))
                list.add("logs");

            return list;
        }

        // /revitalize logs <module>
        if (args.length == 2 && args[0].equalsIgnoreCase("logs")) {

            var list = new ArrayList<String>();
            var input = args[1].toLowerCase();

            this.revitalizeCore.getRevitalizeLogger()
                    .getRegisteredModuleNames()
                    .forEach(name -> {
                        if (name.toLowerCase().startsWith(input)) {
                            list.add(name);
                        }
                    });

            return list;
        }

        // /revitalize logs <module> <level>
        if (args.length == 3 && args[0].equalsIgnoreCase("logs")) {

            var input = args[2].toUpperCase();
            var list = new ArrayList<String>();

            for (LogLevel level : LogLevel.values()) {
                if (level.name().startsWith(input))
                    list.add(level.name());
            }

            return list;
        }

        return List.of();
    }

    // ---------------------------------------------------------------------------------
    // Helper – Log Ausgabe
    // ---------------------------------------------------------------------------------

    private void sendLogList(CommandSender sender, List<LogEntry> logs) {

        if (logs.isEmpty()) {
            sender.sendMessage(this.revitalizeCore.getMessageUtil().compileMessage(
                    "Es wurden keine Logs gefunden!"
            ));
            return;
        }

        logs.forEach(entry -> sender.sendMessage(
                this.revitalizeCore.getMessageUtil().compileMessage(
                        "[%s] [%s] %s",
                        entry.module(),
                        entry.level().name(),
                        entry.message()
                )
        ));
    }

    private void sendUsage(CommandSender sender) {

        var entries = List.of(
                new UsageEntry("", " "),
                new UsageEntry("", "revitalize logs"),
                new UsageEntry("", "revitalize logs <module>"),
                new UsageEntry("", "revitalize logs <module> <level>")
        );

        entries.forEach(entry ->
                sender.sendMessage(this.revitalizeCore.getMessageUtil().compileMessage(
                        "Bitte verwende: %s",
                        "/" + entry.usage()
                ))
        );
    }

}
