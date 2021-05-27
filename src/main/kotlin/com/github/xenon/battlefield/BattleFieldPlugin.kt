package com.github.xenon.battlefield

import com.github.monun.kommand.kommand
import org.bukkit.plugin.java.JavaPlugin

class BattleFieldPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: BattleFieldPlugin
    }
    override fun onEnable() {
        instance = this
        server.worlds.first().worldBorder.let {
            it.setCenter(0.0, 0.0)
            it.setSize(1000.0, 0)
        }
        server.worlds.first().spawnLocation = server.worlds.first().getHighestBlockAt(0, 0).location.apply {
            y += 1
        }
        kommand {
            register("bf") {
                BattleKommand.register(this)
            }
        }
    }
}