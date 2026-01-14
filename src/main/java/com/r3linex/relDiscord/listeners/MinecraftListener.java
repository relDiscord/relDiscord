package com.r3linex.relDiscord.listeners;

import com.r3linex.relDiscord.RelDiscord;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MinecraftListener implements Listener {
    private final RelDiscord plugin;

    public MinecraftListener(RelDiscord plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = "**" + event.getPlayer().getName() + "**: " + event.getMessage();
        plugin.getDiscordManager().sendToChat(message);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updateBotStatus();

        // Update notification for OPs
        if (event.getPlayer().isOp() && plugin.getUpdateManager().isUpdateAvailable()) {
            String repo = "relDiscord/relDiscord";
            String latest = plugin.getUpdateManager().getLatestVersion();
            event.getPlayer().sendMessage(plugin.getLocaleManager().getMessage("prefix") +
                    plugin.getLocaleManager().getMessage("commands.update-new-version", latest, repo));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // Delayed update to account for player leaving
        Bukkit.getScheduler().runTaskLater(plugin, this::updateBotStatus, 20L);
    }

    private void updateBotStatus() {
        int playerCount = Bukkit.getOnlinePlayers().size();
        plugin.getDiscordManager().updateStatus(playerCount);
    }
}
