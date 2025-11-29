package de.peaqe.revitalizecore.command;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.framework.loader.ModuleLoader;
import de.peaqe.revitalizecore.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 29.11.2025 | 01:30 Uhr
 * *
 */

public class ModuleCommand implements CommandExecutor, TabExecutor {

    private final MessageUtil messageUtil;

    public ModuleCommand(RevitalizeCore revitalizeCore) {
        this.messageUtil = new MessageUtil(revitalizeCore);

        var moduleCommand = revitalizeCore.getServer().getPluginCommand("modules");
        if (moduleCommand == null) return;

        moduleCommand.setExecutor(this);
        moduleCommand.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             String @NotNull [] args) {

        if (!(sender.hasPermission("revitalize.command.module"))) return true;

        var moduleNames = ModuleLoader.getModules();
        if (moduleNames.isEmpty()) {
            sender.sendMessage(this.messageUtil.compileMessage(
                    "Derzeit sind keine %s geladen.", "Module"
            ));
            return true;
        }

        sender.sendMessage(this.messageUtil.compileMessage(
                "Aktuell sind folgende %s geladen:", "Module"
        ));
        moduleNames.forEach((s1) -> {
            sender.sendMessage(this.messageUtil.compileMessage(
                    "§8» %s", s1
            ));
        });

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String label,
                                      String @NotNull [] args) {
        return List.of();
    }

}
