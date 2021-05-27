package com.github.xenon.battlefield

import org.bukkit.Bukkit
import org.bukkit.Location
import kotlin.random.Random
import kotlin.random.Random.Default.nextDouble

class BattleFieldScheduler(val name: String) : Runnable {
    var phase = 0
    var center = Bukkit.getWorlds().first().getHighestBlockAt(0, 0)
    val border = Bukkit.getWorlds().first().worldBorder
    var ticks = 0
    override fun run() {
        ++ticks
        if(ticks == 40) {
            if(border.center.x.toInt() == center.x && border.center.z.toInt() == center.z) {
                border.center = border.center.random(border.size / 2.0)
                border.setSize(900.0, 900)
            }
        }
    }
    fun Location.random(spread: Double): Location {
        x += nextDouble(-spread, spread)
        z += nextDouble(-spread, spread)
        return toHighestLocation().add(0.5, 1.0, 0.5)
    }
}