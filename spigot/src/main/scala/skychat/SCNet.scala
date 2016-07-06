package skychat

import java.io.DataInput

import org.bukkit.entity.Player

object SCNet {
  type HANDLER = PartialFunction[(String, DataInput, Player), Unit]
  private var listeners: HANDLER = PartialFunction.empty

  def listen(f: HANDLER): Unit = listeners = listeners.orElse(f)
  def listen(key: String, f: (DataInput, Player) => Unit): Unit = listen {
    case (`key`, in, player) => f(in, player)
  }

  private[SkyChatSpigot] def fire(tag: String, in: DataInput, player: Player): Unit =
    listeners.apply(tag, in, player)

}
