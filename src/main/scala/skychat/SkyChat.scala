package skychat

import java.util.UUID

import net.milkbowl.vault.chat.Chat
import org.bukkit.{Bukkit, OfflinePlayer}
import org.bukkit.command.{Command, CommandSender}
import org.bukkit.plugin.java.JavaPlugin
import skychat.irc.SkyChatIrc

class SkyChat extends JavaPlugin {
  override def onEnable(): Unit = {
    if (!getServer.getPluginManager.isPluginEnabled("Vault")) {
      getLogger.severe("SkyChat disabled due to lack of vault!")
      getServer.getPluginManager.disablePlugin(this)
      return
    }
    SkyChatIrc.init(getConfig.getConfigurationSection("irc"))
    SkyChat.inst = this
    SkyChat.chat = getServer.getServicesManager.getRegistration(classOf[Chat]).getProvider
    PlayerData.init()
  }

  def doMsg(player: OfflinePlayer, who: String, msg: String): Boolean = Option(Bukkit.getPlayer(who)) match {
    case Some(p) =>
      doMsgImpl(player, p.getUniqueId, msg)
      true
    case None =>
      player.getPlayer.sendMessage("That player isn't online!")
      false
  }

  def doMsgImpl(player: OfflinePlayer, id: UUID, msg: String): Unit = PlayerData.getPlayer(player).foreach { p =>
    Option(getServer.getPlayer(id)) match {
      case None => player.getPlayer.sendMessage("That player isn't online!")
      case Some(tgt) =>
        PlayerData.update(PlayerData.Player.update(tgt).copy(replyTarget = Some(player.getUniqueId)))
        PlayerData.update(p.copy(replyTarget = Some(id)))

        tgt.sendMessage(TextFormat.ingameMsgRx(Map(
          "prefix" -> SkyChat.chat.getPlayerPrefix(player.getPlayer),
          "name" -> player.getPlayer.getDisplayName,
          "msg" -> msg
        )))
        player.getPlayer.sendMessage(TextFormat.ingameMsgEcho(Map(
          "prefix" -> SkyChat.chat.getPlayerPrefix(tgt),
          "name" -> tgt.getDisplayName,
          "msg" -> msg
        )))
    }
  }

  def doReply(player: OfflinePlayer, msg: String): Boolean = PlayerData.getPlayer(player).exists(_.replyTarget match {
    case Some(id) =>
      doMsgImpl(player, id, msg)
      true
    case None =>
      player.getPlayer.sendMessage("No reply target!")
      false
  })

  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = command.getName match {
    case "r" => doReply(sender.asInstanceOf[OfflinePlayer], args.mkString(" "))
    case "msg" => doMsg(sender.asInstanceOf[OfflinePlayer], args.head, args.tail.mkString(" "))
    case "me" => ActionHandler.handle(sender.asInstanceOf[OfflinePlayer], args.mkString(" "))
  }
}

object SkyChat {
  var chat: Chat = _

  var inst: SkyChat = _
}
