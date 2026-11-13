package com.xazymc.streak;

import org.bukkit.plugin.java.JavaPlugin;

public final class StreakSMP extends JavaPlugin {
    private StreakManager streakManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.streakManager = new StreakManager(this);
        getServer().getPluginManager().registerEvents(new StreakListener(this, streakManager), this);

        LevelCommand levelCommand = new LevelCommand(streakManager);
        getCommand("level").setExecutor(levelCommand);
        getCommand("level").setTabCompleter(levelCommand);
        getCommand("top").setExecutor(new TopCommand(streakManager));

        getLogger().info("StreakSMP enabled.");
    }

    @Override
    public void onDisable() {
        if (streakManager != null) {
            streakManager.save();
        }
    }
}