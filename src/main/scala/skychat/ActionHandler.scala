package skychat

import org.bukkit.{Bukkit, OfflinePlayer}
import org.bukkit.entity.Player
import skychat.irc.SkyChatIrc

object ActionHandler {
  def handle(player: OfflinePlayer, s: String): Boolean = handle(Bukkit.getPlayer(player.getUniqueId), s)

  val colorPattern = """(?i)§[0-9A-FR]""".r
  val magicPattern = """(?i)§[K-O]""".r
  def handle(p: Player, s: String): Boolean = {
    var msg = s

    if (!p.hasPermission("robochat.chat.color")) {
      msg = colorPattern.replaceAllIn(msg, "")
    }
    if (!p.hasPermission("robochat.chat.magic")) {
      msg = magicPattern.replaceAllIn(msg, "")
    }

    val fmt = TextFormat.ingameAction(Map(
      "prefix" -> SkyChat.chat.getPlayerPrefix(p),
      "name" -> p.getDisplayName,
      "msg" -> msg
    ))

    Bukkit.broadcastMessage(fmt)
    SkyChatIrc.broadcast(fmt)

    true
  }

}
