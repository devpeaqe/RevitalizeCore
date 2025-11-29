package de.peaqe.revitalizecore.command;

import de.peaqe.revitalizecore.RevitalizeCore;
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

public class RepositoryCommand implements CommandExecutor, TabExecutor {

    private final RevitalizeCore revitalizeCore;
    private final MessageUtil messageUtil;

    public RepositoryCommand(RevitalizeCore revitalizeCore) {
        this.revitalizeCore = revitalizeCore;
        this.messageUtil = new MessageUtil(this.revitalizeCore);
        this.revitalizeCore.getServer().getPluginCommand("repository").setExecutor(this);
        this.revitalizeCore.getServer().getPluginCommand("repository").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             String @NotNull [] args) {

        if (!(sender.hasPermission("revitalize.command.repository"))) return true;

        sender.sendMessage(this.messageUtil.compileMessage(
                "Aktuell sind folgende %s geladen:", "Repositories"
        ));
        this.revitalizeCore.getDatabaseManager().getRepositories().forEach((s1, cacheRepository) -> {
            sender.sendMessage(this.messageUtil.compileMessage(
                    "Repository: %s", s1
            ));
        });

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String label,
                                      String @NotNull [] args) {
        return null;
    }

}
