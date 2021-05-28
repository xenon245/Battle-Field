package com.github.xenon.battlefield

import com.github.xenon.battlefield.BattleFieldPlugin.Companion.instance
import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import java.io.File
import javax.imageio.ImageIO

class CustomMapRenderer : MapRenderer() {
    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        val image = ImageIO.read(File(instance.dataFolder, "map.png"))
    }
}