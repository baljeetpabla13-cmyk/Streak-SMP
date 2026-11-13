package com.xazymc.streak;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public final class StreakListener implements Listener {
    private final StreakSMP plugin;
    private final StreakManager manager;

    public StreakListener(StreakSMP plugin, StreakManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        Player killer = victim.getKiller();
        if (killer == null || killer.equals(victim)) return;
        manager.recordKill(killer, victim);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        manager.reset(event.getEntity());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        manager.updateBar(event.getPlayer());
    }

    @EventHandler
    public void onVanillaExp(PlayerExpChangeEvent event) {
        // Keep the XP bar reserved for Streak SMP progression.
        event.setAmount(0);
    }
}