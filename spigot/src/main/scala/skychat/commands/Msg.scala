package skychat.commands

import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import skychat.{Formatting, PlayerData, SkyChatSpigot}

import scala.util.Try

object Msg extends SCCmd("msg") {
  override def execute(sender: CommandSender, args: Seq[String]): Unit = sender match {
    case p: Player =>
      val scp = PlayerData.getPlayer(p)
      if (args.length < 2) {
        scp.sendMessage(ChatColor.RED + "Invalid command: /msg [player] [mag]")
      } else {
        val msg = args.drop(1).mkString(" ", " ", "")
        val tgt =
          if (SkyChatSpigot.cfg.getString("format.msg.echo", "Default").equalsIgnoreCase("Default"))
            s"[{from}§r -> §dYou§r]:$msg"
          else
            Formatting.FormatStringAll(SkyChatSpigot.cfg.getString("format.msg.echo")) + msg
        val echo =
          if (SkyChatSpigot.cfg.getString("format.msg.send", "Default").equalsIgnoreCase("Default"))
            s"[§dYou§r -> {to}§r]:$msg"
          else
            Formatting.FormatStringAll(SkyChatSpigot.cfg.getString("format.msg.send")) + msg
        Try({
          scp.sendPluginMessage(out => {
            out.writeUTF("MSG")
            out.writeUTF("SEND")
            out.writeUTF(args.head)
            out.writeUTF(scp.id.toString)
            out.writeUTF(SkyChatSpigot.chat.getPlayerPrefix(scp.player.get) + scp.name)
            out.writeUTF(tgt)
            out.writeUTF(echo)
          })
        })
      }
  }
}
