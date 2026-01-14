package com.r3linex.relDiscord.managers;

import com.r3linex.relDiscord.RelDiscord;
import com.r3linex.relDiscord.listeners.DiscordListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordManager {
    private final RelDiscord plugin;
    private JDA jda;
    private final String token;

    public DiscordManager(RelDiscord plugin) {
        this.plugin = plugin;
        this.token = plugin.getConfigManager().getToken();
        startBot();
    }

    private void startBot() {
        if (token == null || token.isEmpty() || token.equals("YOUR_BOT_TOKEN_HERE")) {
            plugin.getLogger().warning("Discord bot token is not set in config.yml!");
            return;
        }

        try {
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new DiscordListener(plugin))
                    .setStatus(OnlineStatus.fromKey(plugin.getConfigManager().getBotStatus()))
                    .setActivity(Activity.playing(plugin.getConfigManager().getActivityFormat().replace("{0}", "0")))
                    .build();
            jda.awaitReady();
            plugin.getLogger().info("Discord bot successfully connected!");

            sendToConsole(plugin.getLocaleManager().getMessage("discord.server-start"));
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to connect to Discord: " + e.getMessage());
        }
    }

    public void stopBot() {
        if (jda != null) {
            sendToConsole(plugin.getLocaleManager().getMessage("discord.server-stop"));
            jda.shutdown();
        }
    }

    public void sendToChat(String message) {
        String channelId = plugin.getConfigManager().getChatChannelId();
        sendToChannel(channelId, message);
    }

    public void sendToConsole(String message) {
        String channelId = plugin.getConfigManager().getConsoleChannelId();
        sendToChannel(channelId, message);
    }

    private void sendToChannel(String channelId, String message) {
        if (jda == null || channelId == null || channelId.isEmpty())
            return;
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            channel.sendMessage(message).queue();
        }
    }

    public void updateStatus(int playerCount) {
        if (jda != null) {
            String format = plugin.getConfigManager().getActivityFormat();
            String status = format.replace("{0}", String.valueOf(playerCount));
            jda.getPresence().setActivity(Activity.playing(status));
        }
    }

    public JDA getJda() {
        return jda;
    }
}
