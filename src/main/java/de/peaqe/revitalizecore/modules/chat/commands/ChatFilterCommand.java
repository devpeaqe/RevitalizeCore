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
            this.chatModule.getLogger().info("ChatFilterCommand registered.");
        }
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] args
    ) {

        this.chatModule.getLogger().debug("ChatFilterCommand executed by " + sender.getName());

        if (args.length == 0) {
            this.chatModule.getLogger().debug("No arguments → sending usage.");
            this.sendUsage(sender);
            return true;
        }

        // /chatfilter enable
        if (args.length == 1 && args[0].equalsIgnoreCase("enable")) {

            this.chatModule.getLogger().debug(sender.getName() + " issued: enable");

            if (!sender.hasPermission("revitalize.command.chatfilter.enable")) {
                this.chatModule.getLogger().warn(sender.getName() + " lacks permission: chatfilter.enable");
                return true;
            }

            if (this.chatModule.getChatConfig().getEnabled()) {
                this.chatModule.getLogger().debug("ChatFilter already enabled.");
                sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                        "Der %s ist bereits %s.",
                        "ChatFilter", "§aaktiviert"
                ));
                return true;
            }

            this.chatModule.getChatConfig().setEnabled(true);
            this.chatModule.getLogger().info("ChatFilter enabled by " + sender.getName());

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

            this.chatModule.getLogger().debug(sender.getName() + " issued: disable");

            if (!sender.hasPermission("revitalize.command.chatfilter.disable")) {
                this.chatModule.getLogger().warn(sender.getName() + " lacks permission: chatfilter.disable");
                return true;
            }

            if (!this.chatModule.getChatConfig().getEnabled()) {
                this.chatModule.getLogger().debug("ChatFilter already disabled.");
                sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                        "Der %s ist bereits %s.",
                        "ChatFilter", "deaktiviert"
                ));
                return true;
            }

            this.chatModule.getChatConfig().setEnabled(false);
            this.chatModule.getLogger().info("ChatFilter disabled by " + sender.getName());

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

        // SECOND ARG COMMANDS

        if (args.length == 2) {

            // mode
            if (args[0].equalsIgnoreCase("mode") &&
                    sender.hasPermission("revitalize.command.chatfilter.mode")) {

                this.chatModule.getLogger().debug(sender.getName() + " issued: mode " + args[1]);

                var filterMode = FilterMode.fromString(args[1]);

                if (filterMode == null) {
                    this.chatModule.getLogger().warn("Invalid filter mode: " + args[1]);
                    sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                            "Ungültiger %s. Gültige Optionen: %s",
                            "Filter-Modus", "BLOCK, REPLACE"
                    ));
                    return true;
                }

                var current = this.chatModule.getChatConfig().getFilterMode();
                if (filterMode == current) {
                    this.chatModule.getLogger().debug("Filter mode already set to " + args[1]);
                    sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                            "Der %s ist bereits %s.",
                            "Filter-Modus", args[1]
                    ));
                    return true;
                }

                this.chatModule.getChatConfig().setFilterMode(filterMode);
                this.chatModule.getLogger().info("FilterMode changed to " + filterMode.name() + " by " + sender.getName());

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

            // add
            if ((args[0].equalsIgnoreCase("add") ||
                    args[0].equalsIgnoreCase("addword")) &&
                    sender.hasPermission("revitalize.command.chatfilter.add")) {

                this.chatModule.getLogger().debug(sender.getName() + " issued: add " + args[1]);

                var raw = args[1];
                var normalized = MessageUtil.normalize(raw);
                var badWords = this.chatModule.getChatConfig().getBadWords();

                boolean exists = badWords.stream()
                        .map(MessageUtil::normalize)
                        .anyMatch(normalized::equals);

                if (exists) {
                    this.chatModule.getLogger().warn("Word already in filter: " + raw);
                    sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                            "Das Wort %s ist bereits im %s.",
                            raw, "Filter"
                    ));
                    return true;
                }

                this.chatModule.getChatConfig().addBadWord(raw);
                this.chatModule.getLogger().info("Added badword \"" + raw + "\" by " + sender.getName());

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

            // remove
            if ((args[0].equalsIgnoreCase("remove") ||
                    args[0].equalsIgnoreCase("removeword")) &&
                    sender.hasPermission("revitalize.command.chatfilter.remove")) {

                this.chatModule.getLogger().debug(sender.getName() + " issued: remove " + args[1]);

                var raw = args[1];
                var normalized = MessageUtil.normalize(raw);
                var badWords = this.chatModule.getChatConfig().getBadWords();

                boolean exists = badWords.stream()
                        .map(MessageUtil::normalize)
                        .anyMatch(normalized::equals);

                if (!exists) {
                    this.chatModule.getLogger().warn("Word not found in filter: " + raw);
                    sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                            "Das Wort %s konnte nicht im %s gefunden werden!",
                            raw, "Filter"
                    ));
                    return true;
                }

                this.chatModule.getChatConfig().removeBadWord(raw);
                this.chatModule.getLogger().info("Removed badword \"" + raw + "\" by " + sender.getName());

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

            // notify enable/disable
            if (args[0].equalsIgnoreCase("notify") &&
                    sender.hasPermission("revitalize.command.chatfilter.notify.change")) {

                this.chatModule.getLogger().debug(sender.getName() + " issued: notify " + args[1]);

                if (args[1].equalsIgnoreCase("enable")) {

                    if (this.chatModule.getChatConfig().getStaffNotifyEnabled()) {
                        this.chatModule.getLogger().debug("Notify already enabled.");
                        sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                                "Die %s für den %s sind bereits %s.",
                                "Benachrichtigungen", "ChatFilter", "§aaktiviert"
                        ));
                        return true;
                    }

                    this.chatModule.getChatConfig().setStaffNotifyEnabled(true);
                    this.chatModule.getLogger().info("Notify enabled by " + sender.getName());

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
                        this.chatModule.getLogger().debug("Notify already disabled.");
                        sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                                "Die %s für den %s sind bereits %s.",
                                "Benachrichtigungen", "ChatFilter", "deaktiviert"
                        ));
                        return true;
                    }

                    this.chatModule.getChatConfig().setStaffNotifyEnabled(false);
                    this.chatModule.getLogger().info("Notify disabled by " + sender.getName());

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

                this.chatModule.getLogger().debug("Invalid notify arg → usage sent.");
                this.sendUsage(sender);
                return true;
            }

            this.chatModule.getLogger().debug("Second arg not matched → usage.");
            this.sendUsage(sender);
            return true;
        }

        // notify splitter
        if (args.length == 3 &&
                args[0].equalsIgnoreCase("notify") &&
                args[1].equalsIgnoreCase("splitter") &&
                sender.hasPermission("revitalize.command.chatfilter.notify.splitter.change")) {

            this.chatModule.getLogger().debug(sender.getName() + " issued: notify splitter " + args[2]);

            if (args[2].equalsIgnoreCase("enable")) {

                if (this.chatModule.getChatConfig().getStaffSplitterEnabled()) {
                    this.chatModule.getLogger().debug("Splitter already enabled.");
                    sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                            "Die %s für den %s sind bereits %s.",
                            "Trennnachrichten", "ChatFilter", "§aaktiviert"
                    ));
                    return true;
                }

                this.chatModule.getChatConfig().setStaffSplitterEnabled(true);
                this.chatModule.getLogger().info("Splitter enabled by " + sender.getName());

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
                    this.chatModule.getLogger().debug("Splitter already disabled.");
                    sender.sendMessage(this.chatModule.getMessageUtil().compileMessage(
                            "Die %s für den %s sind bereits %s.",
                            "Trennnachrichten", "ChatFilter", "deaktiviert"
                    ));
                    return true;
                }

                this.chatModule.getChatConfig().setStaffSplitterEnabled(false);
                this.chatModule.getLogger().info("Splitter disabled by " + sender.getName());

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

            this.chatModule.getLogger().debug("Invalid splitter arg → usage.");
            this.sendUsage(sender);
            return true;
        }

        this.chatModule.getLogger().debug("Invalid arguments → usage.");
        this.sendUsage(sender);
        return true;
    }

    // TAB COMPLETION ---------------------------------------------------------------------

    @Nullable
    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] args
    ) {

        this.chatModule.getLogger().debug("TabComplete triggered: args=" + String.join(", ", args));

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

        this.chatModule.getLogger().debug("sendUsage() called.");

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
