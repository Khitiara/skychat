package skychat

import java.util.logging.Logger

import com.google.common.io.ByteStreams
import net.milkbowl.vault.chat.Chat
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.PluginMessageListener

class SkyChatSpigot extends JavaPlugin with PluginMessageListener {
  def synchronize(tgt: SCPlayer): Unit =
    tgt.trySendPluginMessage { out =>
      out.writeUTF("SYNC")
      out.writeUTF(tgt.id.toString)
      tgt.replyPlayer match {
        case Some(id) =>
          out.writeBoolean(true)
          out.writeUTF(id.toString)
        case None =>
          out.writeBoolean(false)
      }
    }

  var firstRun = true

  private def schedule(): Unit = {
    Bukkit.getMessenger.registerOutgoingPluginChannel(this, "SkyChat")
    Bukkit.getMessenger.registerIncomingPluginChannel(this, "SkyChat", this)
  }

  override def onEnable(): Unit = {
    if (!getServer.getPluginManager.isPluginEnabled("Vault")) {
      getLogger.severe("RoboChat disabled due to lack of vault!")
      getServer.getPluginManager.disablePlugin(this)
      return
    }
    SkyChatSpigot.inst = this
    SkyChatSpigot.chat = getServer.getServicesManager.getRegistration(classOf[Chat]).getProvider
    PlayerData.init()
    schedule()
    if (firstRun)
      PlayerData.load()
    else
      PlayerData.markOnline()
    firstRun = false
    SkyChatSpigot.log = getLogger
  }

  override def onPluginMessageReceived(channel: String, player: Player, message: Array[Byte]): Unit = if (channel == "SkyChat") {
    val in = ByteStreams.newDataInput(message)
    val tag = in.readUTF()
    SCNet.fire(tag, in, player)
  }
}

object SkyChatSpigot {
  var log = Logger.getLogger("Minecraft")
  var inst: SkyChatSpigot = null
  var chat: Chat = null

  def cfg = inst.getConfig
}