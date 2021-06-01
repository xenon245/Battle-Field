package com.github.xenon.battlefield

import com.github.xenon.battlefield.BattleFieldPlugin.Companion.instance
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.map.*
import org.bukkit.util.Vector
import java.io.File
import javax.imageio.ImageIO

class CustomMapRenderer : MapRenderer() {
    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        val location = player.location
        val cursorCollection = MapCursorCollection()
        val x = location.x / 500 * 128
        val z = location.z / 500 * 128
        val yaw = player.location.yaw
        val mapYaw = if(yaw >= 0) (yaw / 360 * 15).toByte() else (15 + (yaw / 360 * 15)).toByte()
        if(cursorCollection.size() == 0) {
            cursorCollection.addCursor(x.toInt(), z.toInt(), mapYaw, MapCursor.Type.WHITE_POINTER.value, true)
        }
        val mapCursor = cursorCollection.getCursor(0)
        mapCursor.x = x.toByte()
        mapCursor.y = z.toByte()
        mapCursor.direction = mapYaw
        canvas.cursors = cursorCollection
        val folder = File(instance.dataFolder, "map").also { it.mkdirs() }
        val image = ImageIO.read(File(folder, "map.png"))
        canvas.drawImage(0, 0, image.getSubimage(0, 0, 128, 128))
        val nextLoc = BattleField.field.values.first().nextLoc.location.toVector()
        val phase = BattleField.field.values.first().phase
        val nextWorldBorderSize = ((1000.toDouble() - (phase + 1).toDouble() * 100.toDouble()) / 1000.0 * 64.0).toInt()
        val center = nextLoc.multiply(64 / 1000).add(Vector(64, 0, 64))
        for(i in (center.x - nextWorldBorderSize).toInt()..(center.x + nextWorldBorderSize).toInt()) {
            canvas.setPixel(i, (center.z - nextWorldBorderSize).toInt(), MapPalette.WHITE)
            canvas.setPixel(i, (center.z + nextWorldBorderSize).toInt(), MapPalette.WHITE)
        }
        for(i in (center.z - nextWorldBorderSize).toInt()..(center.z + nextWorldBorderSize).toInt()) {
            canvas.setPixel((center.x - nextWorldBorderSize).toInt(), i, MapPalette.WHITE)
            canvas.setPixel((center.x + nextWorldBorderSize).toInt(), i, MapPalette.WHITE)
        }
        val worldBorderSize = Bukkit.getWorlds().first().worldBorder.size / 1000.0 * 64.0
        for(i in (64.toDouble() - worldBorderSize).toInt()..(64.toDouble() + worldBorderSize).toInt()) {
            if(!BattleField.field.values.first().shrink) {
                canvas.setPixel(i, 64 - worldBorderSize.toInt(), MapPalette.BLUE)
                canvas.setPixel(i, 64 + worldBorderSize.toInt(), MapPalette.BLUE)
                canvas.setPixel(64 - worldBorderSize.toInt(), i, MapPalette.BLUE)
                canvas.setPixel(64 + worldBorderSize.toInt(), i, MapPalette.BLUE)
            } else {
                canvas.setPixel(i, 64 - worldBorderSize.toInt(), MapPalette.RED)
                canvas.setPixel(i, 64 + worldBorderSize.toInt(), MapPalette.RED)
                canvas.setPixel(64 - worldBorderSize.toInt(), i, MapPalette.RED)
                canvas.setPixel(64 + worldBorderSize.toInt(), i, MapPalette.RED)
            }
        }
    }
}