package com.github.xenon.battlefield

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
}