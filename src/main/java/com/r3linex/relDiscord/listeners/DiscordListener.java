package com.r3linex.relDiscord.listeners;

import com.r3linex.relDiscord.RelDiscord;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

import java.util.UUID;

public class DiscordListener extends ListenerAdapter {
    private final RelDiscord plugin;

    public DiscordListener(RelDiscord plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;

        String channelId = event.getChannel().getId();
        String message = event.getMessage().getContentRaw();

        // Account Linking Channel restriction
        String linkChannelId = plugin.getConfigManager().getLinkChannelId();
        String botCommand = plugin.getLocaleManager().getMessage("linking.bot-command");

        if (channelId.equals(linkChannelId)) {
            if (message.startsWith(botCommand + " ")) {
                handleLinkCommand(event, message.substring(botCommand.length() + 1).trim());
            } else {
                event.getMessage().delete().queue();
            }
            return;
        }

        // Chat Relay
        if (channelId.equals(plugin.getConfigManager().getChatChannelId())) {
            if (message.startsWith(botCommand + " ")) {
                handleLinkCommand(event, message.substring(botCommand.length() + 1).trim());
                return;
            }
            String format = plugin.getLocaleManager().getMessage("discord.chat-format",
                    event.getAuthor().getName(), message);
            Bukkit.getScheduler().runTask(plugin, () -> Bukkit.broadcastMessage(format));
        }

        // Console Commands
        if (channelId.equals(plugin.getConfigManager().getConsoleChannelId())) {
            handleConsoleCommand(event, message);
        }
    }

    private void handleLinkCommand(MessageReceivedEvent event, String code) {
        UUID playerUuid = plugin.getLinkManager().getPlayerByCode(code);

        if (playerUuid == null) {
            event.getChannel().sendMessage(plugin.getLocaleManager().getMessage("linking.invalid-code")).queue();
            return;
        }

        plugin.getLinkManager().link(playerUuid, event.getAuthor().getId());

        String mcName = Bukkit.getOfflinePlayer(playerUuid).getName();
        event.getChannel().sendMessage(plugin.getLocaleManager().getMessage("linking.success-discord", mcName)).queue();

        org.bukkit.entity.Player player = Bukkit.getPlayer(playerUuid);
        if (player != null) {
            player.sendMessage(plugin.getLocaleManager().getMessage("prefix") +
                    plugin.getLocaleManager().getMessage("linking.success", event.getAuthor().getAsTag()));
        }
    }

    private void handleConsoleCommand(MessageReceivedEvent event, String command) {
        Member member = event.getMember();
        if (member == null)
            return;

        String authorizedRoleId = plugin.getConfigManager().getAuthorizedRoleId();
        boolean isAuthorized = false;

        if (authorizedRoleId == null || authorizedRoleId.isEmpty()) {
            isAuthorized = member.isOwner(); // Default to owner if no role set
        } else {
            for (Role role : member.getRoles()) {
                if (role.getId().equals(authorizedRoleId)) {
                    isAuthorized = true;
                    break;
                }
            }
        }

        if (!isAuthorized) {
            event.getChannel().sendMessage(plugin.getLocaleManager().getMessage("discord.unauthorized")).queue();
            return;
        }

        // Execute command on main thread
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            event.getChannel().sendMessage(plugin.getLocaleManager().getMessage("discord.command-sent", command))
                    .queue();
        });
    }
}
