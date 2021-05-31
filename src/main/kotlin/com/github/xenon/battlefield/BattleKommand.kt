package com.github.xenon.battlefield

import com.github.monun.kommand.KommandBuilder
import com.github.monun.kommand.KommandContext
import com.github.monun.kommand.argument.*
import com.github.monun.kommand.sendFeedback
import com.github.xenon.battlefield.BattleFieldPlugin.Companion.instance
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import org.bukkit.scheduler.BukkitTask
import java.io.File

object BattleKommand {
    fun register(builder: KommandBuilder) {
        builder.apply {
            then("create") {
                then("name" to string()) {
                    executes {
                        require(!BattleField.field.contains(it.parseArgument("name"))) { "The name has already used" }
                        BattleField.field[it.parseArgument("name")] = BattleFieldScheduler(it.parseArgument("name"))
                    }
                }
            }
            then("remove") {
                then("task" to TaskArgument()) {
                    executes {
                        val task = it.parseArgument<BattleFieldScheduler>("task")
                        val folder = File(instance.dataFolder, "field")
                        val file = File(folder, "${task.name}.yml")
                        file.delete()
                        if(BattleField.running.contains(task.name)) {
                            BattleField.running[task.name]?.cancel()
                            BattleField.running.remove(task.name)
                        }
                        BattleField.field[task.name]?.fieldBar?.isVisible = false
                        BattleField.field.remove(task.name)
                    }
                }
            }
            then("item") {
                executes {
                    val map = ItemStack(Material.FILLED_MAP)
                    val meta = map.itemMeta as MapMeta
                    meta.mapView = Bukkit.getServer().createMap(Bukkit.getWorlds().first())
                    val mapView = meta.mapView
                    val renderer = CustomMapRenderer()
                    mapView?.renderers?.forEach(mapView::removeRenderer)
                    meta.isScaling = true
                    mapView?.addRenderer(renderer)
                    mapView?.scale = MapView.Scale.FAR
                    mapView?.setWorld(Bukkit.getWorlds().first())
                    mapView?.centerX = 0
                    mapView?.centerZ = 0
                    meta.mapView = mapView
                    map.itemMeta = meta
                    Bukkit.getOnlinePlayers().forEach {
                        it.inventory.addItem(map)
                    }
                }
            }
            then("phase") {
                then("task" to TaskArgument(), "phase" to integer(1, 8)) {
                    executes {
                        val task = it.parseArgument<BattleFieldScheduler>("task")
                        val phase = it.parseArgument<Int>("phase")
                        task.phase = phase - 1
                        task.ticks = 0
                        if(phase - 1 == 7) {
                            task.border.setSize((1000 - 100 * (phase - 1) - 100).toDouble(), 0)
                        } else {
                            task.border.setSize((1000 - 100 * (phase - 1)).toDouble(), 0)
                        }
                        task.shrink = false
                    }
                }
            }
            then("center") {
                then("task" to TaskArgument()) {
                    then("x" to double(-500.0, 500.0), "z" to double(-500.0, 500.0)) {
                        executes {
                            val task = it.parseArgument<BattleFieldScheduler>("task")
                            task.nextLoc = Bukkit.getWorlds().first().getHighestBlockAt(it.parseArgument<Double>("x").toInt(), it.parseArgument<Double>("z").toInt())
                        }
                    }
                }
            }
            then("cancel") {
                then("task" to Task2Argument()) {
                    executes {
                        val task = it.parseArgument<BukkitTask>("task")
                        Bukkit.getServer().scheduler.runTask(instance, task::cancel)
                        BattleField.running.remove(it.rawArguments[1])
                        BattleField.field[it.rawArguments[1]]?.fieldBar?.isVisible = false
                    }
                }
            }
            then("cancelall") {
                executes {
                    Bukkit.getServer().scheduler.cancelTasks(instance)
                    BattleField.field.values.forEach {
                        it.fieldBar?.isVisible = false
                    }
                    BattleField.running.clear()
                }
            }
            then("reload") {
                executes {
                    Bukkit.getServer().run {
                        scheduler.cancelTasks(instance)
                    }
                    BattleField.running.clear()
                    BattleField.field.values.forEach {
                        val folder = File(instance.dataFolder, "field").also { it.mkdirs() }
                        val file = File(folder, "${it.name}.yml")
                        val yaml = YamlConfiguration.loadConfiguration(file)
                        it.load(yaml)
                    }
                    it.sender.sendFeedback {
                        text("Battle Field Reloaded!")
                    }
                }
            }
            then("load") {
                then("task" to TaskArgument()) {
                    executes {
                        val task = it.parseArgument<BattleFieldScheduler>("task")
                        val task2 = Bukkit.getScheduler().runTaskTimer(instance, task, 0L, 1L)
                        BattleField.running[task.name] = task2
                    }
                }
            }
        }
    }
}
class TaskArgument : KommandArgument<BattleFieldScheduler> {
    override fun parse(context: KommandContext, param: String): BattleFieldScheduler? {
        return BattleField.field[param]
    }

    override fun suggest(context: KommandContext, target: String): Collection<String> {
        return BattleField.field.keys.suggest(target)
    }
}
class Task2Argument : KommandArgument<BukkitTask> {
    override fun parse(context: KommandContext, param: String): BukkitTask? {
        return BattleField.running[param]
    }

    override fun suggest(context: KommandContext, target: String): Collection<String> {
        return BattleField.running.keys.suggest(target)
    }
}