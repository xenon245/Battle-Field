package com.github.xenon.battlefield

import com.github.monun.kommand.kommand
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class BattleFieldPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: BattleFieldPlugin
    }
    override fun onEnable() {
        val folder = File(dataFolder, "field")
        if(folder.exists()) {
            folder.listFiles().forEach {
                val yaml = YamlConfiguration.loadConfiguration(it)
                BattleField.field[it.nameWithoutExtension] = BattleFieldScheduler(it.nameWithoutExtension)
                BattleField.field[it.nameWithoutExtension]?.load(yaml)
            }
        }
        instance = this
        server.worlds.first().worldBorder.let {
            it.setCenter(0.0, 0.0)
            it.setSize(1000.0, 0)
        }
        server.pluginManager.registerEvents(BattleListener(), this)
        server.worlds.first().spawnLocation = server.worlds.first().getHighestBlockAt(0, 0).location.apply {
            y += 1
        }
        kommand {
            register("bf") {
                BattleKommand.register(this)
            }
        }
    }

    override fun onDisable() {
        BattleField.field.values.forEach {
            val folder = File(dataFolder, "field").also { it.mkdirs() }
            val file = File(folder, "${it.name}.yml")
            val config = YamlConfiguration()
            it.save(config)
            config.save(file)
        }
    }
}