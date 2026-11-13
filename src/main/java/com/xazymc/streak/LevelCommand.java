package com.xazymc.streak;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public final class LevelCommand implements CommandExecutor, TabCompleter {
    private final StreakManager manager;

    public LevelCommand(StreakManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }
        PlayerStreak streak = manager.get(player.getUniqueId());
        String prefix = manager.color("&8[&bStreak&8] ");
        sender.sendMessage(prefix + manager.color(manager.formatLevel(streak)));
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return java.util.Collections.emptyList();
    }
}