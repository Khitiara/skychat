package skychat

import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.{EventHandler, EventPriority, Listener}

object ChatListener extends Listener {
  val colorPattern = """(?i)§[0-9A-FR]""".r
  val magicPattern = """(?i)§[K-O]""".r

  @EventHandler(priority = EventPriority.HIGHEST)
  def chat(e: AsyncPlayerChatEvent): Unit = {
    val p: Player = e.getPlayer
    PlayerData.Player.update(p)

    var msg = e.getMessage

    if (!p.hasPermission("robochat.chat.color")) {
      msg = colorPattern.replaceAllIn(msg, "")
    }
    if (!p.hasPermission("robochat.chat.magic")) {
      msg = magicPattern.replaceAllIn(msg, "")
    }

    val fmt = TextFormat.ingameChat.unparse(Map(
      "prefix" -> SkyChat.chat.getPlayerPrefix(p),
      "name" -> p.getDisplayName,
      "msg" -> msg
    ))
    e.setFormat(fmt)

    SkyChatIrc.broadcast(fmt)
  }
}
