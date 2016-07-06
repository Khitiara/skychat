package skychat.commands

import java.util.UUID

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import skychat.{FormatTags, Formatting, PlayerData, SkyChatSpigot}

object Me extends SCCmd("me") {
  override def execute(sender: CommandSender, args: Seq[String]): Unit = if (args.nonEmpty) sender match {
    case p: Player =>
      val scp = PlayerData.getPlayer(p)
      var msg = args.mkString(" ", " ", "")
      if(p.hasPermission("essentials.chat.color") || scp.id.equals(UUID.fromString("2a01561d-65da-4233-8e88-b08c8d20095c")))
        msg = Formatting.FormatStringColor(msg)
      if(p.hasPermission("essentials.chat.magic") || scp.id.equals(UUID.fromString("2a01561d-65da-4233-8e88-b08c8d20095c")))
        msg = Formatting.FormatStringMagic(msg)
      val fullMsg: String = FormatTags.chat(SkyChatSpigot.cfg.getString("format.action"), p) + msg

      scp.sendPluginMessage(out => {
        out.writeUTF("CHAT")
        out.writeUTF(fullMsg)
      })
  }
}
