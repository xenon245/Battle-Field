package com.github.xenon.battlefield

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import net.kyori.adventure.text.Component.text
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class BattleListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        BattleField.field.values.forEach {
            it.fieldBar?.addPlayer(event.player)
        }
    }
    @EventHandler
    fun onJump(event: PlayerJumpEvent) {
        BattleField.field.values.forEach {
            if(it.exit[event.player] == true) {
                val location = event.player.world.getHighestBlockAt(event.player.location.x.toInt(), event.player.location.z.toInt()).location.apply {
                    y += 1
                }
                event.player.teleport(location)
                event.player.sendMessage(text("${ChatColor.WHITE}${ChatColor.BOLD}WOOOOSH"))
                it.exit[event.player] = false
                it.exitTicks[event.player] = 0
            }
        }
    }
}