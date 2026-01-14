package com.r3linex.relDiscord.managers;

import com.r3linex.relDiscord.RelDiscord;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LinkManager {
    private final RelDiscord plugin;
    private final Map<String, UUID> pendingCodes = new HashMap<>();
    private final Map<String, Long> codeExpiries = new HashMap<>();
    private final Map<UUID, String> linkedAccounts = new HashMap<>(); // UUID -> DiscordID
    private final Random random = new Random();

    public LinkManager(RelDiscord plugin) {
        this.plugin = plugin;
        // In a real implementation, load from SQLite here
    }

    public String generateCode(UUID playerUuid) {
        String code;
        do {
            code = String.format("%06d", random.nextInt(1000000));
        } while (pendingCodes.containsKey(code));

        pendingCodes.put(code, playerUuid);
        long expiry = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(
                plugin.getConfigManager().getConfig().getLong("linking.code-expiry-minutes", 5));
        codeExpiries.put(code, expiry);

        return code;
    }

    public UUID getPlayerByCode(String code) {
        if (!pendingCodes.containsKey(code))
            return null;

        if (System.currentTimeMillis() > codeExpiries.get(code)) {
            pendingCodes.remove(code);
            codeExpiries.remove(code);
            return null;
        }

        return pendingCodes.get(code);
    }

    public void link(UUID playerUuid, String discordId) {
        linkedAccounts.put(playerUuid, discordId);
        // In a real implementation, save to SQLite here

        // Remove pending codes for this player
        pendingCodes.entrySet().removeIf(entry -> entry.getValue().equals(playerUuid));
    }

    public boolean isLinked(UUID playerUuid) {
        return linkedAccounts.containsKey(playerUuid);
    }

    public String getDiscordId(UUID playerUuid) {
        return linkedAccounts.get(playerUuid);
    }
}
