package skychat

import java.io.DataOutput

import com.google.common.io.ByteStreams
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

import scala.util.Try

object utils {
  implicit class RichPlayer(p: Player) {
    def sendPluginDataMessage(plugin: Plugin, channel: String)(msg: DataOutput => Unit): Unit = {
      val out = ByteStreams.newDataOutput()
      msg.apply(out)
      p.sendPluginMessage(plugin, channel, out.toByteArray)
    }
    def trySendPluginMessage(plugin: Plugin, channel: String)(msg: DataOutput => Unit): Try[Unit] =
      Try(sendPluginDataMessage(plugin, channel)(msg))
  }
}
