package com.r3linex.relDiscord;

import com.r3linex.relDiscord.commands.DiscordCommand;
import com.r3linex.relDiscord.commands.LinkCommand;
import com.r3linex.relDiscord.listeners.MinecraftListener;
import com.r3linex.relDiscord.managers.ConfigManager;
import com.r3linex.relDiscord.managers.DiscordManager;
import com.r3linex.relDiscord.managers.LinkManager;
import com.r3linex.relDiscord.managers.LocaleManager;
import com.r3linex.relDiscord.managers.UpdateManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class RelDiscord extends JavaPlugin {
    private ConfigManager configManager;
    private LocaleManager localeManager;
    private DiscordManager discordManager;
    private LinkManager linkManager;
    private UpdateManager updateManager;

    @Override
    public void onEnable() {
        // 0. Print Banner
        printBanner();

        // 1. Initialize Configuration
        this.configManager = new ConfigManager(this);

        // 2. Initialize Localization
        String lang = configManager.getLanguage();
        this.localeManager = new LocaleManager(this, lang);

        // 3. Initialize Linking Manager
        this.linkManager = new LinkManager(this);

        // 4. Initialize Discord Manager
        this.discordManager = new DiscordManager(this);

        // 5. Initialize Update Manager
        this.updateManager = new UpdateManager(this);

        // 6. Register Listeners
        getServer().getPluginManager().registerEvents(new MinecraftListener(this), this);

        // 6. Register Commands
        getCommand("eşle").setExecutor(new LinkCommand(this));
        getCommand("discord").setExecutor(new DiscordCommand(this));

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "RelDiscord Plugin enabled!");
    }

    private void printBanner() {
        try (InputStream is = getResource("ascii.txt")) {
            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    getServer().getConsoleSender().sendMessage(ChatColor.AQUA + line);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void reloadPlugin() {
        // 1. Reload configuration
        configManager.reload();

        // 2. Refresh LocaleManager
        this.localeManager = new LocaleManager(this, configManager.getLanguage());

        // 3. Restart Discord Bot to apply new token/intents if changed
        if (discordManager != null) {
            discordManager.stopBot();
        }
        this.discordManager = new DiscordManager(this);

        getLogger().info("RelDiscord has been reloaded!");
    }

    @Override
    public void onDisable() {
        if (discordManager != null) {
            discordManager.stopBot();
        }
        getLogger().info("RelDiscord has been disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public LinkManager getLinkManager() {
        return linkManager;
    }

    public UpdateManager getUpdateManager() {
        return updateManager;
    }
}
