package skychat

import java.io.DataInput
import java.util.UUID

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.management.PlayerList
import net.minecraft.util.text.{ITextComponent, TextComponentString}
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import skychat.text.{ChatColor, TextParser}

object SCHandlers {
  def init(): Unit = {
    SCNet.listen("CHAT", chat)
    SCNet.listen("MSG", msg)
  }

  private def chat(in: DataInput, p: EntityPlayerMP): Unit = {
    val msg = in.readUTF()
    val cmp = TextParser.parse(msg, '\u00A7')
    p.mcServer.getPlayerList.sendChatMsg(cmp)
  }

  private def msgSend(in: DataInput, p: EntityPlayerMP): Unit = {
    val name = in.readUTF()
    val playerList: PlayerList = FMLCommonHandler.instance().getMinecraftServerInstance.getPlayerList
    val tgt = playerList.getPlayerByUsername(name)
    val fromId = UUID.fromString(in.readUTF())
    var from = in.readUTF()
    val sender = playerList.getPlayerByUUID(fromId)
    val tgtMsg = in.readUTF()
    val echoMsg = in.readUTF()
    if (tgt != null) {
      from = ChatColor.translateAlternateColorCodes('&', from)
      tgt.addChatMessage(TextParser.parse(tgtMsg
        .replace("{from}", from)
        .replace("{to}", tgt.getDisplayNameString)))
      SCNet.send(p) { out =>
        out.writeUTF("MSG")
        out.writeUTF("ECHO")
        out.writeUTF(tgt.toString)
        out.writeUTF(ChatColor.translateAlternateColorCodes('&', tgt.getDisplayNameString))
        out.writeUTF(echoMsg)
      }
    } else {
      SCNet.send(p) { out =>
        out.writeUTF("MSG")
        out.writeUTF("OFFLINE")
        out.writeUTF(name)
        out.writeUTF(fromId.toString)
      }
    }
  }

  private def msgOffline(in: DataInput, p: EntityPlayerMP): Unit = {
    val player = in.readUTF()
    val id = UUID.fromString(in.readUTF())
    val plr = FMLCommonHandler.instance().getMinecraftServerInstance.getPlayerList.getPlayerByUUID(id)
    if (plr != null) {
      plr.addChatMessage(TextParser.parse(s"&cPlayer: &6$player&c is not online."))
    }
  }

  private def msgEcho(in: DataInput, p: EntityPlayerMP): Unit = {
    val tgt = UUID.fromString(in.readUTF())
    val sender: String = in.readUTF()
    val plr = FMLCommonHandler.instance().getMinecraftServerInstance.getPlayerList.getPlayerByUsername(sender)
    if (plr != null) {
      val tgtNick = in.readUTF()
      val echoMsg = in.readUTF()
      plr.addChatMessage(TextParser.parse(echoMsg
        .replace("{from}", plr.getDisplayNameString)
        .replace("{to}", tgtNick)))
    }
  }

  private def msg(in: DataInput, p: EntityPlayerMP): Unit = {
    in.readUTF() match {
      case "SEND" => msgSend(in, p)
    }
  }

  @SubscribeEvent
  def onChat(e: ServerChatEvent): Unit = {
    var msg: String = e.getMessage
    val user = e.getUsername
    val fmtString: String = Formatting.chat(SkyChatForge.conf.getString("msg", "format", "<{name}>", "Message format"), e.getPlayer)
    val fmt = TextParser.parse(fmtString)
    val component: ITextComponent =
      if (e.getPlayer.getServer.getPlayerList.getOppedPlayerNames.contains(user)
        || e.getPlayer.getUniqueID.equals(UUID.fromString("2a01561d-65da-4233-8e88-b08c8d20095c"))) {
        msg = ChatColor.translateAlternateColorCodes('&', msg)
        TextParser.parse(msg, '&')
      } else
        new TextComponentString(msg)
    fmt.appendSibling(component)
    e.setComponent(fmt)
    SCNet.send(e.getPlayer) { out =>
      out.writeUTF("CHAT")
      out.writeUTF(fmtString + " " + msg)
    }
  }
}
