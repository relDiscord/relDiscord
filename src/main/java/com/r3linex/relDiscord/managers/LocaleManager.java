package com.r3linex.relDiscord.managers;

import com.r3linex.relDiscord.RelDiscord;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LocaleManager {
    private final RelDiscord plugin;
    private FileConfiguration localeConfig;
    private final String language;

    public LocaleManager(RelDiscord plugin, String language) {
        this.plugin = plugin;
        this.language = language;
        loadLocale();
    }

    private void loadLocale() {
        File localesFolder = new File(plugin.getDataFolder(), "locales");
        if (!localesFolder.exists()) {
            localesFolder.mkdirs();
        }

        String fileName = language + ".yml";
        File localeFile = new File(localesFolder, fileName);

        if (!localeFile.exists()) {
            plugin.saveResource("locales/" + fileName, false);
        }

        localeConfig = YamlConfiguration.loadConfiguration(localeFile);

        // Load default values from internal resource if missing
        InputStream defaultStream = plugin.getResource("locales/" + fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration
                    .loadConfiguration(new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            localeConfig.setDefaults(defaultConfig);
        }
    }

    public String getMessage(String path) {
        // First check the main config
        String message = localeConfig.getString(path);

        // If not found in main config, check the defaults explicitly just in case
        if (message == null && localeConfig.getDefaults() != null) {
            message = localeConfig.getDefaults().getString(path);
        }

        // Final fallback if still null
        if (message == null) {
            return "Missing message: " + path;
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(String path, Object... args) {
        String message = getMessage(path);
        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return message;
    }
}
