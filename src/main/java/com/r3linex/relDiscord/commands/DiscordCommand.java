package com.r3linex.relDiscord.commands;

import com.r3linex.relDiscord.RelDiscord;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DiscordCommand implements CommandExecutor {
    private final RelDiscord plugin;

    public DiscordCommand(RelDiscord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("reldiscord.admin") && !sender.isOp()) {
                sender.sendMessage(plugin.getLocaleManager().getMessage("prefix") +
                        plugin.getLocaleManager().getMessage("commands.no-permission"));
                return true;
            }

            plugin.reloadPlugin();
            sender.sendMessage(plugin.getLocaleManager().getMessage("prefix") +
                    plugin.getLocaleManager().getMessage("commands.reload"));
            return true;
        }

        // Default: Show invite link
        String inviteLink = plugin.getConfigManager().getInviteLink();
        sender.sendMessage(plugin.getLocaleManager().getMessage("prefix") +
                plugin.getLocaleManager().getMessage("commands.invite", inviteLink));

        return true;
    }
}
