package robochat

import java.io.DataInput
import java.util

import io.netty.buffer.{ByteBuf, ByteBufInputStream}
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket
import net.minecraftforge.fml.common.network.{FMLEmbeddedChannel, NetworkRegistry}
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.server.FMLServerHandler

object RCNet {
  type HANDLER = PartialFunction[(String, DataInput), Unit]
  private var channels: util.EnumMap[Side, FMLEmbeddedChannel] = null
  private var listeners: HANDLER = PartialFunction.empty

  def init(): Unit = {
    channels = NetworkRegistry.INSTANCE.newChannel("RoboChat")
  }

  def listen(f: HANDLER): Unit = listeners = listeners.orElse(f)

  private def handleMsgPkt(msg: FMLProxyPacket): Unit = {
    val payload: ByteBuf = msg.payload()
    val input: ByteBufInputStream = new ByteBufInputStream(payload)
    FMLServerHandler.instance().getServer.addScheduledTask(new Runnable {
      override def run(): Unit = {
        val kind = input.readUTF()
        listeners.apply((kind, input))
        input.close()
      }
    })
  }

  class InboundHandler extends SimpleChannelInboundHandler[FMLProxyPacket] {
    override def channelRead0(ctx: ChannelHandlerContext, msg: FMLProxyPacket): Unit = {
      handleMsgPkt(msg)
    }
  }

}
