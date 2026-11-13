package com.xazymc.streak;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class StreakManager {
    private final StreakSMP plugin;
    private final Map<UUID, PlayerStreak> streaks = new HashMap<>();
    private final File dataFile;
    private final YamlConfiguration data;

    public StreakManager(StreakSMP plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        this.data = YamlConfiguration.loadConfiguration(dataFile);
        load();
    }

    private void load() {
        ConfigurationSection players = data.getConfigurationSection("players");
        if (players == null) return;
        for (String key : players.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                PlayerStreak streak = new PlayerStreak(id);
                String path = "players." + key + ".";
                streak.setLevel(data.getInt(path + "level"));
                streak.setProgress(data.getInt(path + "progress"));
                // Anti-farming history is intentionally session-based.
                streaks.put(id, streak);
            } catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Skipping invalid player UUID in data.yml: " + key);
            }
        }
    }

    public void save() {
        data.set("players", null);
        for (PlayerStreak streak : streaks.values()) {
            String path = "players." + streak.getPlayerId() + ".";
            data.set(path + "level", streak.getLevel());
            data.set(path + "progress", streak.getProgress());
        }
        try {
            data.save(dataFile);
        } catch (IOException exception) {
            plugin.getLogger().severe("Could not save streak data: " + exception.getMessage());
        }
    }

    public PlayerStreak get(UUID playerId) {
        return streaks.computeIfAbsent(playerId, PlayerStreak::new);
    }

    public int getKillsPerLevel() {
        return Math.max(1, plugin.getConfig().getInt("kills-per-level", 5));
    }

    public boolean recordKill(Player killer, Player victim) {
        PlayerStreak streak = get(killer.getUniqueId());
        streak.recordVictim(victim.getUniqueId());

        int limit = Math.max(1, plugin.getConfig().getInt("max-consecutive-kills-on-same-player", 2));
        if (streak.getConsecutiveKills() > limit) {
            send(killer, "invalid-repeat");
            updateBar(killer);
            return false;
        }

        streak.setProgress(streak.getProgress() + 1);
        if (streak.getProgress() >= getKillsPerLevel()) {
            streak.setProgress(0);
            streak.setLevel(streak.getLevel() + 1);
            sendLevelUp(killer, streak.getLevel());
            giveRewards(killer, streak.getLevel());
        }
        updateBar(killer);
        save();
        return true;
    }

    public void reset(Player player) {
        get(player.getUniqueId()).reset();
        player.setExp(0.0f);
        player.setLevel(0);
        send(player, "streak-reset");
        save();
    }

    public void updateBar(Player player) {
        PlayerStreak streak = get(player.getUniqueId());
        player.setLevel(streak.getLevel());
        player.setExp(Math.min(0.999f, (float) streak.getProgress() / getKillsPerLevel()));
    }

    public List<PlayerStreak> getTop(int amount) {
        return streaks.values().stream()
                .sorted(Comparator.comparingInt(PlayerStreak::getLevel)
                        .thenComparingInt(PlayerStreak::getProgress).reversed())
                .limit(amount)
                .collect(Collectors.toList());
    }

    public String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public void send(Player player, String key) {
        player.sendMessage(color(plugin.getConfig().getString("messages.prefix", "") +
                plugin.getConfig().getString("messages." + key, "")));
    }

    private void sendLevelUp(Player player, int level) {
        String message = plugin.getConfig().getString("messages.level-up", "&aLevel up!");
        player.sendMessage(color(plugin.getConfig().getString("messages.prefix", "") +
                message.replace("%level%", String.valueOf(level))));
    }

    private void giveRewards(Player player, int level) {
        ConfigurationSection rewards = plugin.getConfig().getConfigurationSection("rewards");
        if (rewards == null) return;
        List<String> commands = rewards.getStringList(String.valueOf(level));
        for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                    .replace("%player%", player.getName())
                    .replace("%level%", String.valueOf(level)));
        }
    }

    public String formatLevel(PlayerStreak streak) {
        return plugin.getConfig().getString("messages.level", "Level %level%")
                .replace("%level%", String.valueOf(streak.getLevel()))
                .replace("%progress%", String.valueOf(streak.getProgress()))
                .replace("%required%", String.valueOf(getKillsPerLevel()));
    }

    public String formatTop(PlayerStreak streak, int position) {
        OfflinePlayer offline = Bukkit.getOfflinePlayer(streak.getPlayerId());
        String name = offline.getName() == null ? streak.getPlayerId().toString().substring(0, 8) : offline.getName();
        return plugin.getConfig().getString("messages.top-entry", "%position%. %player% - %level%")
                .replace("%position%", String.valueOf(position))
                .replace("%player%", name)
                .replace("%level%", String.valueOf(streak.getLevel()));
    }
}