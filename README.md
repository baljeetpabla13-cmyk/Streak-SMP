# StreakSMP

A kill-based progression plugin for Paper 1.21.11.

## Features
- Every 5 valid player kills grants one level by default.
- Vanilla XP bar displays kills towards the next level.
- Death resets level and progress to zero.
- The third and later consecutive kills against the same victim do not count.
- Configurable rewards executed as console commands at each level.
- `/level` and `/top` commands.
- Persistent levels and progress across restarts.

## Build
Requires Java 21 and Maven.

```bash
mvn clean package
```

The plugin JAR is created at `target/StreakSMP.jar`.

## Install
Put the JAR in your Paper server's `plugins` folder and restart the server.