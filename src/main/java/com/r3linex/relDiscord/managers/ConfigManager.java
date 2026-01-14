package com.r3linex.relDiscord.managers;

import com.r3linex.relDiscord.RelDiscord;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final RelDiscord plugin;
    private FileConfiguration config;

    public ConfigManager(RelDiscord plugin) {
        this.plugin = plugin;
        setupConfig();
    }

    private void setupConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public String getToken() {
        return config.getString("discord.bot-token", "");
    }

    public String getConsoleChannelId() {
        return config.getString("discord.channels.console", "");
    }

    public String getChatChannelId() {
        return config.getString("discord.channels.chat", "");
    }

    public String getLinkChannelId() {
        return config.getString("discord.channels.link-channel-id", "");
    }

    public String getInviteLink() {
        return config.getString("discord.invite-link", "");
    }

    public String getLanguage() {
        return config.getString("language", "en-US");
    }

    public String getAuthorizedRoleId() {
        return config.getString("discord.authorized-role-id", "");
    }

    public String getBotStatus() {
        return config.getString("discord.presence.status", "online");
    }

    public String getActivityFormat() {
        return config.getString("discord.presence.activity-format", "Online Players: {0}");
    }

    public boolean isUpdateCheckerEnabled() {
        return config.getBoolean("updates.enabled", true);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
