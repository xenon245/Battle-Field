package com.github.xenon.battlefield

import org.bukkit.scheduler.BukkitTask

object BattleField {
    var field = HashMap<String, BattleFieldScheduler>()
    var running = HashMap<String, BukkitTask>()
}