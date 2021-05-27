package com.github.xenon.battlefield

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.random.Random
import kotlin.random.Random.Default.nextDouble

class BattleFieldScheduler(var name: String) : Runnable {
    var phase = 0
    var center = Bukkit.getWorlds().first().getHighestBlockAt(0, 0)
    val border = Bukkit.getWorlds().first().worldBorder
    var ticks = 0
    var firstPhase = 600 * 20
    var secondPhase = 20
    var thirdPhase = 40
    var fourthPhase = 60
    var fifthPhase = 80
    var sixthPhase = 100
    var seventhPhase = 120
    var eightPhase = 140
    var ninePhase = 160
    var tenthPhase = 180
    var fieldBar : BossBar? = null
    var shrink = false
    var nextLocation : Location? = null
    var borderTicks = 0
    init {
        fieldBar = Bukkit.createBossBar("전장 축소까지 600 초", BarColor.BLUE, BarStyle.SEGMENTED_10).apply {
            Bukkit.getOnlinePlayers().forEach {
                addPlayer(it)
            }
            isVisible = false
            progress = 1.0
        }
    }
    override fun run() {
        ++ticks
        if(!shrink) {
            fieldBar?.isVisible = true
            fieldBar?.style = BarStyle.SEGMENTED_10
            fieldBar?.color = BarColor.BLUE
            val time = if(phase == 0) firstPhase - ticks else if(phase == 1) secondPhase - ticks else if(phase == 2) thirdPhase - ticks else if(phase == 3) fourthPhase - ticks else if(phase == 4) fifthPhase - ticks else if(phase == 5) sixthPhase - ticks else if(phase == 6) seventhPhase - ticks else if (phase == 7) eightPhase - ticks else if (phase == 8) ninePhase - ticks else tenthPhase - ticks
            if(time >= -1) {
                fieldBar?.setTitle("전장 축소까지 ${(time / 20).toInt()}.${(time / 2) % 10}초")
                fieldBar?.progress = (time.toDouble() / (time + ticks).toDouble()).coerceIn(0.0, 1.0)
            } else {
                shrink = true
                ticks = 0
            }
        } else {
            if(ticks <= 100 * 20) {
                fieldBar?.style = BarStyle.SOLID
                fieldBar?.color = BarColor.RED
                fieldBar?.setTitle("전장 축소 중... ${ticks / 2000}.${ticks / 200}")
                fieldBar?.progress = (ticks.toDouble() / 2000).coerceIn(0.0, 1.0)
                borderTicks++
                if(borderTicks == 20) {
                    border.setSize(border.size - 1, 1)
                    borderTicks = 0
                }
            } else {
                if(phase == 9) {
                    fieldBar?.isVisible = false
                    BattleField.running[name]?.cancel()
                    BattleField.running.remove(name)
                } else {
                    shrink = false
                    ticks = 0
                    phase++
                }
            }
        }
    }
    fun Location.random(spread: Double): Location {
        x += nextDouble(-spread, spread)
        z += nextDouble(-spread, spread)
        return toHighestLocation().add(0.5, 1.0, 0.5)
    }
    fun save(config: YamlConfiguration) {
        config["name"] = name
        config["phase"] = phase
        config["firstPhase"] = firstPhase
        config["secondPhase"] = secondPhase
        config["thirdPhase"] = thirdPhase
        config["fourthPhase"] = fourthPhase
        config["fifthPhase"] = fifthPhase
        config["center"] = center.location
    }
    fun load(config: YamlConfiguration) {
        name = requireNotNull(config.getString("name")) { "Name must not be null!" }
        phase = requireNotNull(config.getInt("phase")) { "Phase must not be null!" }
        firstPhase = requireNotNull(config.getInt("firstPhase")) { "First Phase must not be null!" }
        secondPhase = requireNotNull(config.getInt("secondPhase")) { "First Phase must not be null!" }
        thirdPhase = requireNotNull(config.getInt("thirdPhase")) { "First Phase must not be null!" }
        fourthPhase = requireNotNull(config.getInt("fourthPhase")) { "First Phase must not be null!" }
        fifthPhase = requireNotNull(config.getInt("fifthPhase")) { "First Phase must not be null!" }
        val loc = requireNotNull(config.getLocation("center")) { "Location must not be null!" }
        center = loc.block
    }
}