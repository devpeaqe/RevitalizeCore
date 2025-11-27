package de.peaqe.revitalizecore.modules.chat.commands;

import de.peaqe.revitalizecore.modules.chat.ChatModule;
import de.peaqe.revitalizecore.modules.chat.utils.FilterMode;
import de.peaqe.revitalizecore.modules.chat.utils.MessageUtil;
import de.peaqe.revitalizecore.modules.chat.utils.UsageEntry;
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
 * @since 26.11.2025 | 14:21 Uhr
 * *
 */

public class ChatFilterCommand implements CommandExecutor, TabExecutor {

    private final ChatModule chatModule;

    public ChatFilterCommand(ChatModule chatModule) {
        this.chatModule = chatModule;

        var pluginCommand = this.chatModule.getRevitalizeCore().getServer().getPluginCommand("chatfilter");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] args
    ) {

        if (args.length == 0) {
            this.sendUsage(sender);
            return true;
        }

        // /chatfilter enable
        if (args.length == 1 && args[0].equalsIgnoreCase("enable")) {

            if (!sender.hasPermission("revitalize.command.chatfilter.enable"))
                return true;

            if (this.chatModule.getChatConfig().getEnabled()) {
                sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                        "Der %s ist bereits %s.",
                        "ChatFilter", "§aaktiviert"
                ));
                return true;
            }

            this.chatModule.getChatConfig().setEnabled(true);
            this.chatModule.getMessageUtil().notifyStaff(
                    "Der %s wurde von %s %s",
                    "ChatFilter", sender.getName(), "§aaktiviert"
            );
            sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                    "Der %s wurde %s.",
                    "ChatFilter", "§aaktiviert"
            ));
            return true;
        }

        // /chatfilter disable
        if (args.length == 1 && args[0].equalsIgnoreCase("disable")) {

            if (!sender.hasPermission("revitalize.command.chatfilter.disable"))
                return true;

            if (!this.chatModule.getChatConfig().getEnabled()) {
                sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                        "Der %s ist bereits %s.",
                        "ChatFilter", "deaktiviert"
                ));
                return true;
            }

            this.chatModule.getChatConfig().setEnabled(false);
            this.chatModule.getMessageUtil().notifyStaff(
                    "Der %s wurde von %s %s",
                    "ChatFilter", sender.getName(), "deaktiviert"
            );
            sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                    "Der %s wurde %s.",
                    "ChatFilter", "deaktiviert"
            ));
            return true;
        }

        // ---- SECOND ARGUMENT COMMANDS ---- //

        if (args.length == 2) {

            // /chatfilter mode <mode>
            if (args[0].equalsIgnoreCase("mode") &&
                    sender.hasPermission("revitalize.command.chatfilter.mode")) {

                var filterMode = FilterMode.fromString(args[1]);

                if (filterMode == null) {
                    sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                            "Ungültiger %s. Gültige Optionen: %s",
                            "Filter-Modus", "BLOCK, REPLACE"
                    ));
                    return true;
                }

                var current = this.chatModule.getChatConfig().getFilterMode();
                if (filterMode == current) {
                    sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                            "Der %s ist bereits %s.",
                            "Filter-Modus", args[1]
                    ));
                    return true;
                }

                this.chatModule.getChatConfig().setFilterMode(filterMode);
                this.chatModule.getMessageUtil().notifyStaff(
                        "Der %s wurde von %s auf %s gesetzt",
                        "Filter-Modus", sender.getName(), args[1]
                );
                sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                        "Der %s wurde auf %s gesetzt.",
                        "Filter-Modus", args[1]
                ));
                return true;
            }

            // /chatfilter add <word>
            if ((args[0].equalsIgnoreCase("add") ||
                    args[0].equalsIgnoreCase("addword")) &&
                    sender.hasPermission("revitalize.command.chatfilter.add")) {

                var raw = args[1];
                var normalized = MessageUtil.normalize(raw);
                var badWords = this.chatModule.getChatConfig().getBadWords();

                boolean exists = badWords.stream()
                        .map(MessageUtil::normalize)
                        .anyMatch(normalized::equals);

                if (exists) {
                    sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                            "Das Wort %s ist bereits im %s.",
                            raw, "Filter"
                    ));
                    return true;
                }

                this.chatModule.getChatConfig().addBadWord(raw); // 🔥 speichert original
                this.chatModule.getMessageUtil().notifyStaff(
                        "Das Wort %s wurde von %s zum %s hinzugefügt.",
                        raw, sender.getName(), "Filter"
                );
                sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                        "Das Wort %s wurde zum %s hinzugefügt.",
                        raw, "Filter"
                ));
                return true;
            }

            // /chatfilter remove <word>
            if ((args[0].equalsIgnoreCase("remove") ||
                    args[0].equalsIgnoreCase("removeword")) &&
                    sender.hasPermission("revitalize.command.chatfilter.remove")) {

                var raw = args[1];
                var normalized = MessageUtil.normalize(raw);
                var badWords = this.chatModule.getChatConfig().getBadWords();

                boolean exists = badWords.stream()
                        .map(MessageUtil::normalize)
                        .anyMatch(normalized::equals);

                if (!exists) {
                    sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                            "Das Wort %s konnte nicht im %s gefunden werden!",
                            raw, "Filter"
                    ));
                    return true;
                }

                this.chatModule.getChatConfig().removeBadWord(raw);
                this.chatModule.getMessageUtil().notifyStaff(
                        "Das Wort %s wurde von %s vom %s entfernt.",
                        raw, sender.getName(), "Filter"
                );
                sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                        "Das Wort %s wurde vom %s entfernt.",
                        raw, "Filter"
                ));
                return true;
            }

            // /chatfilter notify <enable|disable>
            if (args[0].equalsIgnoreCase("notify") &&
                    sender.hasPermission("revitalize.command.chatfilter.notify.change")) {

                if (args[1].equalsIgnoreCase("enable")) {

                    if (this.chatModule.getChatConfig().getStaffNotifyEnabled()) {
                        sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                                "Die %s für den %s sind bereits %s.",
                                "Benachrichtigungen", "ChatFilter", "§aaktiviert"
                        ));
                        return true;
                    }

                    this.chatModule.getChatConfig().setStaffNotifyEnabled(true);
                    this.chatModule.getMessageUtil().notifyStaff(
                            "Die %s für den %s wurden von %s %s",
                            "Benachrichtigungen", "ChatFilter", sender.getName(), "§aaktiviert"
                    );
                    sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                            "Die %s für den %s wurden %s",
                            "Benachrichtigungen", "ChatFilter", "§aaktiviert"
                    ));
                    return true;
                }

                if (args[1].equalsIgnoreCase("disable")) {

                    if (!this.chatModule.getChatConfig().getStaffNotifyEnabled()) {
                        sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                                "Die %s für den %s sind bereits %s.",
                                "Benachrichtigungen", "ChatFilter", "deaktiviert"
                        ));
                        return true;
                    }

                    this.chatModule.getChatConfig().setStaffNotifyEnabled(false);
                    this.chatModule.getMessageUtil().notifyStaff(
                            "Die %s für den %s wurden von %s %s",
                            "Benachrichtigungen", "ChatFilter", sender.getName(), "deaktiviert"
                    );
                    sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                            "Die %s für den %s wurden %s",
                            "Benachrichtigungen", "ChatFilter", "deaktiviert"
                    ));
                    return true;
                }

                this.sendUsage(sender);
                return true;
            }

            this.sendUsage(sender);
            return true;
        }

        // ---- NOTIFY SPLITTER ---- //

        if (args.length == 3 &&
                args[0].equalsIgnoreCase("notify") &&
                args[1].equalsIgnoreCase("splitter") &&
                sender.hasPermission("revitalize.command.chatfilter.notify.splitter.change")) {

            if (args[2].equalsIgnoreCase("enable")) {

                if (this.chatModule.getChatConfig().getStaffSplitterEnabled()) {
                    sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                            "Die %s für den %s sind bereits %s.",
                            "Trennnachrichten", "ChatFilter", "§aaktiviert"
                    ));
                    return true;
                }

                this.chatModule.getChatConfig().setStaffSplitterEnabled(true);
                this.chatModule.getMessageUtil().notifyStaff(
                        "Die %s für den %s wurden von %s %s",
                        "Trennnachrichten", "ChatFilter", sender.getName(), "§aaktiviert"
                );
                sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                        "Die %s für den %s wurden %s",
                        "Trennnachrichten", "ChatFilter", "§aaktiviert"
                ));
                return true;
            }

            if (args[2].equalsIgnoreCase("disable")) {

                if (!this.chatModule.getChatConfig().getStaffSplitterEnabled()) {
                    sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                            "Die %s für den %s sind bereits %s.",
                            "Trennnachrichten", "ChatFilter", "deaktiviert"
                    ));
                    return true;
                }

                this.chatModule.getChatConfig().setStaffSplitterEnabled(false);
                this.chatModule.getMessageUtil().notifyStaff(
                        "Die %s für den %s wurden von %s %s",
                        "Trennnachrichten", "ChatFilter", sender.getName(), "deaktiviert"
                );
                sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                        "Die %s für den %s wurden %s",
                        "Trennnachrichten", "ChatFilter", "deaktiviert"
                ));
                return true;
            }

            this.sendUsage(sender);
            return true;
        }

        this.sendUsage(sender);
        return true;
    }

    // -----------------------------------------------------------------------------------------
    // TAB COMPLETION
    // -----------------------------------------------------------------------------------------

    @Nullable
    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] args
    ) {

        if (args.length == 1) {
            var list = new ArrayList<String>();

            if (sender.hasPermission("revitalize.command.chatfilter.enable")
                    && !this.chatModule.getChatConfig().getEnabled())
                list.add("enable");

            if (sender.hasPermission("revitalize.command.chatfilter.disable")
                    && this.chatModule.getChatConfig().getEnabled())
                list.add("disable");

            if (sender.hasPermission("revitalize.command.chatfilter.mode"))
                list.add("mode");

            if (sender.hasPermission("revitalize.command.chatfilter.add")) {
                list.add("addword");
                list.add("add");
            }

            if (sender.hasPermission("revitalize.command.chatfilter.remove")) {
                list.add("removeword");
                list.add("remove");
            }

            if (sender.hasPermission("revitalize.command.chatfilter.notify"))
                list.add("notify");

            return list;
        }

        if (args.length == 2) {
            var list = new ArrayList<String>();

            if (args[0].equalsIgnoreCase("mode")
                    && sender.hasPermission("revitalize.command.chatfilter.mode")) {

                if (this.chatModule.getChatConfig().getFilterMode().equals(FilterMode.BLOCK))
                    list.add("REPLACE");
                else
                    list.add("BLOCK");
            }

            if (args[0].equalsIgnoreCase("notify")
                    && sender.hasPermission("revitalize.command.chatfilter.notify.change")) {

                list.add("enable");
                list.add("disable");

                if (sender.hasPermission("revitalize.command.chatfilter.notify.splitter.change"))
                    list.add("splitter");
            }

            return list;
        }

        if (args.length == 3 &&
                args[0].equalsIgnoreCase("notify") &&
                args[1].equalsIgnoreCase("splitter") &&
                sender.hasPermission("revitalize.command.chatfilter.notify.splitter.change")) {

            var list = new ArrayList<String>();

            if (this.chatModule.getChatConfig().getStaffSplitterEnabled())
                list.add("disable");
            else
                list.add("enable");

            return list;
        }

        return List.of();
    }

    private void sendUsage(CommandSender sender) {

        var entries = List.of(
                new UsageEntry("enable", "enable"),
                new UsageEntry("disable", "disable"),
                new UsageEntry("notify", "notify <enable, disable>"),
                new UsageEntry("notify.splitter", "notify splitter <enable, disable>"),
                new UsageEntry("mode", "mode <filterMode>"),
                new UsageEntry("addword", "addword <word>"),
                new UsageEntry("add", "add <word>"),
                new UsageEntry("removeword", "removeword <word>"),
                new UsageEntry("remove", "remove <word>")
        );

        entries.forEach(entry -> {
            if (sender.hasPermission("revitalize.command.chatfilter." + entry.permission())) {
                sender.sendMessage(
                        this.chatModule.getMessageUtil().compileMessage(
                                "Bitte verwende: %s",
                                "/chatfilter " + entry.usage()
                        )
                );
            }
        });
    }

}
