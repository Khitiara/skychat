package skychat.irc

import org.bukkit.{BanList, Bukkit}
import org.pircbotx.hooks.events.MessageEvent

import scala.collection.JavaConverters._

object Commands {
  def init(): Unit = {
    import reflect.runtime.universe._
    val r = reflect.runtime.currentMirror.reflect(Impl)
    SkyChatIrc.listen(r.symbol.typeSignature.members.collect {
      case s: MethodSymbol if s.isMethod => r.reflectMethod(s)
    }.map { m =>
      m.symbol.name.toString.trim -> ((s: String, msg: MessageEvent) => m.apply(s, msg): Unit)
    }.toMap)
  }

  object Impl {
    def list(s: String, m: MessageEvent): Unit = {
      m.respondWith(s"Online: ${Bukkit.getOnlinePlayers.asScala.map(_.getDisplayName).mkString(", ")}")
    }

    private val kickArgs = """(\w+)(?: (.+))?""".r

    def kick(args: String, m: MessageEvent): Unit = if (m.getChannel.isOp(m.getUser)) {
      args match {
        case kickArgs(who: String) =>
          Bukkit.getPlayer(who).kickPlayer(s"Kicked from IRC by ${m.getUser.getNick}.")
          m.respond("Successfully kicked")
        case kickArgs(who: String, msg: String) =>
          Bukkit.getPlayer(who).kickPlayer(s"Kicked from IRC by ${m.getUser.getNick}: $msg.")
        case _ => m.respond("Incorrect syntax, try again.")
      }
    }
    else m.respond("You can't do that!")
  }

}
