package com.xazymc.streak;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public final class TopCommand implements CommandExecutor {
    private final StreakManager manager;

    public TopCommand(StreakManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(manager.color("&8&m--------------------"));
        sender.sendMessage(manager.color("&b&lStreak Leaderboard"));
        int position = 1;
        for (PlayerStreak streak : manager.getTop(10)) {
            sender.sendMessage(manager.color(manager.formatTop(streak, position++)));
        }
        if (position == 1) sender.sendMessage(ChatColor.GRAY + "No streaks recorded yet.");
        sender.sendMessage(manager.color("&8&m--------------------"));
        return true;
    }
}