package skychat

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.PluginMessageListener

class SkyChat extends JavaPlugin with PluginMessageListener {
  override def onLoad(): Unit = {

  }

  override def onPluginMessageReceived(channel: String, player: Player, message: Array[Byte]): Unit = if (channel == "SkyChat") {

  }
}
