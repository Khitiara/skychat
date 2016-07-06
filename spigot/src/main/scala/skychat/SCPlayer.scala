package skychat

import java.io.DataOutput
import java.util.UUID

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import skychat.utils._

class SCPlayer(var id: UUID, var name: String, var nick: String = "", var player: Option[Player] = None, var online: Boolean = false, var afk: Boolean = false, var replyPlayer: Option[UUID] = None) {
  def setOnline(online: Boolean): SCPlayer = {
    this.online = online
    player = if (online) Some(Bukkit.getPlayer(id)) else None
    this
  }
  def trySendPluginMessage(f: DataOutput => Unit) = player.foreach(_.trySendPluginMessage(SkyChatSpigot.inst, "SkyChat")(f))
  def sendPluginMessage(f: DataOutput => Unit) = player.foreach(_.sendPluginDataMessage(SkyChatSpigot.inst, "SkyChat")(f))
  def sendMessage(s: String) = player.foreach(_.sendMessage(s))
}

object SCPlayer {
  def apply(id: UUID, name: String, nick: String = "",
            player: Option[Player] = None, online: Boolean = false,
            afk: Boolean = false, replyPlayer: Option[UUID] = None): SCPlayer =
    new SCPlayer(id, name, nick, player, online, afk, replyPlayer)
}