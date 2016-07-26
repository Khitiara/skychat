package skychat

import java.util.UUID

import net.milkbowl.vault.chat.Chat
import org.bukkit.OfflinePlayer
import org.bukkit.command.{Command, CommandSender}
import org.bukkit.plugin.java.JavaPlugin

class SkyChat extends JavaPlugin {
  override def onEnable(): Unit = {
    if (!getServer.getPluginManager.isPluginEnabled("Vault")) {
      getLogger.severe("SkyChat disabled due to lack of vault!")
      getServer.getPluginManager.disablePlugin(this)
      return
    }
    SkyChat.inst = this
    SkyChat.chat = getServer.getServicesManager.getRegistration(classOf[Chat]).getProvider
    PlayerData.init()
  }

  def doMsg(player: OfflinePlayer, id: UUID): Boolean = {

  }

  def doReply(player: OfflinePlayer): Boolean = PlayerData.getPlayer(player).replyTarget match {
    case Some(id) => doMsg(player, id)
    case None =>
      player.getPlayer.sendMessage("No reply target!")
      false
  }

  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = command.getName match {
    case "r" => doReply(sender.asInstanceOf[OfflinePlayer])
  }
}

object SkyChat {
  var chat: Chat = _

  var inst: SkyChat = _
}
