package skychat

import java.io.{DataInput, DataOutput}
import java.util

import io.netty.buffer.{ByteBuf, ByteBufInputStream, ByteBufOutputStream, Unpooled}
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, SimpleChannelInboundHandler}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.{NetHandlerPlayServer, PacketBuffer}
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket
import net.minecraftforge.fml.common.network.{FMLEmbeddedChannel, FMLOutboundHandler, NetworkRegistry}
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.server.FMLServerHandler

object SCNet {
  type HANDLER = PartialFunction[(String, DataInput, EntityPlayerMP), Unit]
  private var channels: util.EnumMap[Side, FMLEmbeddedChannel] = null
  private var listeners: HANDLER = PartialFunction.empty

  def init(): Unit = {
    channels = NetworkRegistry.INSTANCE.newChannel("SkyChat")
  }

  def listen(f: HANDLER): Unit = listeners = listeners.orElse(f)
  def listen(key: String, f: (DataInput, EntityPlayerMP) => Unit): Unit = listen {
    case (`key`, in, player) => f(in, player)
  }

  def send(p: EntityPlayerMP)(msg: DataOutput => Unit): Unit = {
    val buf = new PacketBuffer(Unpooled.buffer())
    val writer = new ByteBufOutputStream(buf)
    msg(writer)
    val pkt = new FMLProxyPacket(buf, "SkyChat")
    channels.get(Side.SERVER)
      .attr(FMLOutboundHandler.FML_MESSAGETARGET)
      .set(FMLOutboundHandler.OutboundTarget.PLAYER)
    channels.get(Side.SERVER)
      .attr(FMLOutboundHandler.FML_MESSAGETARGETARGS)
      .set(p)
    channels.get(Side.SERVER)
      .writeAndFlush(pkt)
      .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
  }

  private def handleMsgPkt(msg: FMLProxyPacket): Unit = {
    val payload: ByteBuf = msg.payload()
    val input: ByteBufInputStream = new ByteBufInputStream(payload)
    msg.handler() match {
      case h: NetHandlerPlayServer =>
        FMLServerHandler.instance().getServer.addScheduledTask(new Runnable {
          override def run(): Unit = {
            val kind = input.readUTF()
            listeners.apply((kind, input, h.playerEntity))
            input.close()
          }
        })
    }
  }

  @Sharable
  class InboundHandler extends SimpleChannelInboundHandler[FMLProxyPacket] {
    override def channelRead0(ctx: ChannelHandlerContext, msg: FMLProxyPacket): Unit = {
      handleMsgPkt(msg)
    }
  }
}
