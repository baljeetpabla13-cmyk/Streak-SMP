package com.xazymc.streak;

import java.util.UUID;

public final class PlayerStreak {
    private final UUID playerId;
    private int level;
    private int progress;
    private UUID lastVictim;
    private int consecutiveKills;

    public PlayerStreak(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() { return playerId; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = Math.max(0, level); }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = Math.max(0, progress); }
    public UUID getLastVictim() { return lastVictim; }
    public int getConsecutiveKills() { return consecutiveKills; }

    public void recordVictim(UUID victimId) {
        if (victimId.equals(lastVictim)) {
            consecutiveKills++;
        } else {
            lastVictim = victimId;
            consecutiveKills = 1;
        }
    }

    public void reset() {
        level = 0;
        progress = 0;
        lastVictim = null;
        consecutiveKills = 0;
    }
}