package com.r3linex.relDiscord.commands;

import com.r3linex.relDiscord.RelDiscord;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LinkCommand implements CommandExecutor {
    private final RelDiscord plugin;

    public LinkCommand(RelDiscord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (plugin.getLinkManager().isLinked(player.getUniqueId())) {
            player.sendMessage(plugin.getLocaleManager().getMessage("prefix") +
                    plugin.getLocaleManager().getMessage("linking.already-linked"));
            return true;
        }

        String code = plugin.getLinkManager().generateCode(player.getUniqueId());
        String botCommand = plugin.getLocaleManager().getMessage("linking.bot-command");

        player.sendMessage(plugin.getLocaleManager().getMessage("prefix") +
                plugin.getLocaleManager().getMessage("linking.start", botCommand, code));

        return true;
    }
}
