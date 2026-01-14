package com.r3linex.relDiscord.managers;

import com.r3linex.relDiscord.RelDiscord;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateManager {
    private final RelDiscord plugin;
    private final String currentVersion;
    private String latestVersion;
    private boolean isUpdateAvailable = false;

    public UpdateManager(RelDiscord plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();

        if (plugin.getConfigManager().isUpdateCheckerEnabled()) {
            checkForUpdates();
        }
    }

    private void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.github.com/repos/relDiscord/relDiscord/releases/latest");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                connection.setRequestProperty("User-Agent", "RelDiscord-UpdateChecker");

                if (connection.getResponseCode() == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    // Simple JSON parsing (we only need tag_name)
                    String content = response.toString();
                    if (content.contains("\"tag_name\":\"")) {
                        latestVersion = content.split("\"tag_name\":\"")[1].split("\"")[0];
                        if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                            isUpdateAvailable = true;
                            plugin.getLogger().warning("A new version of RelDiscord is available: " + latestVersion);
                        }
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not check for updates: " + e.getMessage());
            }
        });
    }

    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}
