package robochat

import java.io.DataInput
import java.util.UUID

import com.google.common.io.ByteStreams
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.server.FMLServerHandler

object RCHandlers {

  def send(in: DataInput): Unit = {
    val to = in.readUTF()
    val fromId = UUID.fromString(in.readUTF())
    val from = in.readUTF()
    val targetMsg = in.readUTF()
    val echoMsg = in.readUTF()
    val out = ByteStreams.newDataOutput()
    val player: EntityPlayerMP = FMLServerHandler.instance().getServer.getPlayerList.getPlayerByUUID(fromId)
    if(player != null) {
      player.addChatMessage(new TextComponentString(targetMsg.replace("{playerfrom}", from).replace("{playerto}", to)))

    }
  }

  def init() {
    listen("Send", send)
  }

  def listen(key: String, f: DataInput => Unit): Unit = RCNet.listen {
    case (`key`, in) => f(in)
  }
}
