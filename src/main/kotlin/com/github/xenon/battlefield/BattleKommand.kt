package com.github.xenon.battlefield

import com.github.monun.kommand.KommandBuilder
import com.github.monun.kommand.KommandContext
import com.github.monun.kommand.argument.*
import com.github.xenon.battlefield.BattleFieldPlugin.Companion.instance
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import org.bukkit.scheduler.BukkitTask

object BattleKommand {
    fun register(builder: KommandBuilder) {
        builder.apply {
            then("create") {
                then("name" to string()) {
                    executes {
                        BattleField.field[it.parseArgument("name")] = BattleFieldScheduler(it.parseArgument("name"))
                    }
                }
            }
            then("item") {
                require { this is Player }
                executes {
                    val map = ItemStack(Material.FILLED_MAP)
                    val meta = map.itemMeta as MapMeta
                    meta.mapView = Bukkit.getServer().createMap(Bukkit.getWorlds().first())
                    val mapView = meta.mapView
                    mapView?.scale = MapView.Scale.FARTHEST
                    mapView?.setWorld(Bukkit.getWorlds().first())
                    mapView?.centerX = 0
                    mapView?.centerZ = 0
                    mapView?.isLocked = true
                    mapView?.isTrackingPosition = true
                    map.itemMeta = meta
                    (it.sender as Player).inventory.addItem(map)
                }
            }
            then("phase") {
                then("task" to TaskArgument(), "phase" to integer(0, 5)) {
                    executes {
                        val task = it.parseArgument<BattleFieldScheduler>("task")
                        task.phase = it.parseArgument("phase")
                    }
                }
            }
            then("center") {
                then("x" to double(-500.0, 500.0), "z" to double(-500.0, 500.0)) {
                    executes {
                        Bukkit.getWorlds().first().worldBorder.setCenter(it.parseArgument("x"), it.parseArgument("z"))
                    }
                }
            }
            then("cancel") {
                then("task" to Task2Argument()) {
                    executes {
                        val task = it.parseArgument<BukkitTask>("task")
                        Bukkit.getServer().scheduler.runTask(instance, task::cancel)
                    }
                }
            }
            then("cancelall") {
                executes {
                    Bukkit.getServer().scheduler.cancelTasks(instance)
                }
            }
            then("reload") {
                executes {
                    Bukkit.getServer().run {
                        scheduler.cancelTasks(instance)
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