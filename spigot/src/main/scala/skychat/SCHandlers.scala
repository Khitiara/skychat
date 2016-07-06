package skychat

import java.io.DataInput
import java.util.UUID
import java.util.logging.Level

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.{AsyncPlayerChatEvent, PlayerJoinEvent, PlayerKickEvent, PlayerQuitEvent}
import org.bukkit.event.{EventHandler, EventPriority, Listener}
import skychat.utils._

object SCHandlers extends Listener {
  def init(): Unit = {
    SCNet.listen("CHAT", chat)
    SCNet.listen("MSG", msg)
    SCNet.listen("SYNC", sync)
  }

  private def msgSend(in: DataInput, p: Player): Unit = {
    val name = in.readUTF()
    val tgt = PlayerData.getOnlinePlayer(name)
    val fromId = UUID.fromString(in.readUTF())
    var from = in.readUTF()
    val sender = PlayerData.getPlayer(fromId)
    val tgtMsg = in.readUTF()
    val echoMsg = in.readUTF()
    if (tgt != null && tgt.online) {
      if (sender != null)
        if (sender.online)
          from = Formatting.FormatStringAll(SkyChatSpigot.chat.getPlayerPrefix(sender.player.get)
            + sender.nick)
        else
          from = Formatting.FormatStringAll(from)
      tgt.player.foreach(_.sendMessage(tgtMsg
        .replace("{from}", from)
        .replace("{to}", Formatting.FormatStringAll(
          SkyChatSpigot.chat.getPlayerPrefix(tgt.player.get)
            + tgt.nick))))
      tgt.replyPlayer = Some(fromId)
      SkyChatSpigot.inst.getServer.getScheduler.scheduleSyncDelayedTask(SkyChatSpigot.inst, new Runnable {
        override def run(): Unit = SkyChatSpigot.inst.synchronize(tgt)
      })
      p.sendPluginDataMessage(SkyChatSpigot.inst, "SkyChat") { out =>
        out.writeUTF("MSG")
        out.writeUTF("ECHO")
        out.writeUTF(tgt.id.toString)
        out.writeUTF(Formatting.FormatStringAll(
          SkyChatSpigot.chat.getPlayerPrefix(tgt.player.get)
            + tgt.nick))
        out.writeUTF(echoMsg)
      }
    } else {
      p.sendPluginDataMessage(SkyChatSpigot.inst, "SkyChat") { out =>
        out.writeUTF("MSG")
        out.writeUTF("OFFLINE")
        out.writeUTF(name)
        out.writeUTF(fromId.toString)
      }
    }
  }

  private def msgOffline(in: DataInput, p: Player): Unit = {
    val player = in.readUTF()
    val id = UUID.fromString(in.readUTF())
    val plr = PlayerData.getOnlinePlayer(id)
    if (plr != null) {
      plr.player.foreach(
        _.sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + player + ChatColor.RED + " is not online."))
      plr.replyPlayer = null
    }
  }

  private def msgEcho(in: DataInput, p: Player): Unit = {
    val tgt = UUID.fromString(in.readUTF())
    val sender: String = in.readUTF()
    val plr = PlayerData.getOnlinePlayer(sender)
    if (plr != null) {
      val tgtNick = in.readUTF()
      val echoMsg = in.readUTF()
      plr.replyPlayer = Some(tgt)
      plr.sendMessage(echoMsg
        .replace("{from}", Formatting.FormatStringAll(SkyChatSpigot.chat.getPlayerPrefix(plr.player.get) + plr.nick))
        .replace("{to}", tgtNick))
    }
  }

  private def msg(in: DataInput, p: Player): Unit =
    in.readUTF() match {
      case "SEND" => msgSend(in, p)
      case "OFFLINE" => msgOffline(in, p)
      case "ECHO" => msgEcho(in, p)
    }


  private def chat(in: DataInput, p: Player): Unit = {
    val msg = in.readUTF()

    Bukkit.getConsoleSender.sendMessage(msg)
    PlayerData.online.filter(_.online).foreach(_.player.foreach(_.sendMessage(msg)))
  }

  private def sync(in: DataInput, p: Player): Unit = {
    val id = UUID.fromString(in.readUTF())
    val plr = PlayerData.getPlayer(id)
    plr.replyPlayer = if (in.readBoolean()) Some(UUID.fromString(in.readUTF())) else None
  }

  @EventHandler
  def onChat(e: AsyncPlayerChatEvent): Unit = if (!e.isCancelled) {
    val player: Player = e.getPlayer
    val scPlayer = PlayerData.getPlayer(player)
    if (scPlayer.afk) {
      scPlayer.afk = false
      player.sendMessage(ChatColor.GOLD + "You are no longer AFK.")
      player.trySendPluginMessage(SkyChatSpigot.inst, "SkyChat") { afkOut =>
        afkOut.writeUTF("AFK")
        afkOut.writeUTF(scPlayer.id.toString)
        afkOut.writeBoolean(false)
      }.recover {
        case ex => SkyChatSpigot.log.log(Level.SEVERE, "Error sending chat message!", ex)
      }
    }
    var msg = e.getMessage
    val fmt = FormatTags.chat(SkyChatSpigot.cfg.getString("format.chat"), player)
    if (player.hasPermission("essentials.chat.color") || scPlayer.id.equals(UUID.fromString("2a01561d-65da-4233-8e88-b08c8d20095c")))
      msg = Formatting.FormatStringColor(msg)
    if (player.hasPermission("essentials.chat.msgic") || scPlayer.id.equals(UUID.fromString("2a01561d-65da-4233-8e88-b08c8d20095c")))
      msg = Formatting.FormatStringMagic(msg)
    e.setMessage(msg)
    val fmtFull = String.format(fmt + " %s", msg)
    e.setFormat(fmtFull.replace("%", "%%"))
    player.trySendPluginMessage(SkyChatSpigot.inst, "SkyChat") { out =>
      out.writeUTF("CHAT")
      out.writeUTF(fmtFull)
    }.recover {
      case ex => SkyChatSpigot.log.log(Level.SEVERE, "Error sending chat message!", ex)
    }
  }

  def playerLeaving(getPlayer: Player): Unit = {

  }

  @EventHandler(priority = EventPriority.LOW)
  def onKick(e: PlayerKickEvent): Unit = {
    playerLeaving(e.getPlayer)
  }

  @EventHandler(priority = EventPriority.LOW)
  def onQuit(e: PlayerQuitEvent): Unit = {
    playerLeaving(e.getPlayer)
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  def onJoin(e: PlayerJoinEvent): Unit = {
    val p = e.getPlayer
    var scp = PlayerData.getPlayer(p)
    if(scp == null) {
      val id = p.getUniqueId

      scp = SCPlayer(id, p.getName)
    }
    scp.setOnline(true)
    PlayerData.online += scp
    if(scp.nick == scp.name)
      scp.nick = p.getName
    scp.name = p.getName
  }
}
