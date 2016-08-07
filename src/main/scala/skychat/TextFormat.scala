package skychat

import org.bukkit.configuration.Configuration
import skychat.TextFormat.Template.Part

import scala.collection.mutable

object TextFormat {

  private var conf: Configuration = _

  def init(config: Configuration): Unit = conf = config

  private val templates = mutable.Map[String, Template]()

  private def getFmt(key: String) = templates.getOrElseUpdate(key, Template(conf.getString(key)))

  def ingameChat = getFmt("format.ingame.chat")
  def ingameAction = getFmt("format.ingame.action")
  def ingameMsgRx = getFmt("format.msg.rx.game")
  def ingameMsgEcho = getFmt("format.msg.echo.game")

  def ircChat = getFmt("format.irc.chat")
  def ircAction = getFmt("format.irc.action")
//  def ircMsgRx = getFmt("format.msg.rx.irc")
//  def ircMsgEcho = getFmt("format.msg.echo.irc")

  final class Template private(parts: Seq[Part]) {
    def unparse(vars: Map[String, Any]): String = parts.map(_.unparse(vars)).mkString
    def apply(vars: Map[String, Any]): String = unparse(vars)
  }

  object Template {
    def apply(template: String): Template = {
      val parts = mutable.Buffer[Part]()
      val current = mutable.StringBuilder.newBuilder
      var i: Int = 0
      var v: Boolean = false
      while (i < template.length) {
        template.charAt(i) match {
          case '/' =>
            i += 1
            current += template(i)
          case '{' =>
            if (v)
              throw new Exception("Invalid format: no nesting!")
            else {
              parts += new S(current.mkString)
              current.clear()
              v = true
            }
          case '}' =>
            if (v) {
              parts += new V(current.mkString)
              current.clear()
              v = false
            } else
              throw new Exception("Invalid format: no var block to close!")
          case c => current += c
        }
        i += 1
      }
      if (v) throw new Exception("Invalid format: unclosed block!")
      if (current.nonEmpty) parts += new S(current.mkString)
      new Template(parts)
    }

    private trait Part {
      def unparse(vars: Map[String, Any]): String
    }

    private class S(v: String) extends Part {
      override def unparse(vars: Map[String, Any]): String = v
    }

    private class V(n: String) extends Part {
      override def unparse(vars: Map[String, Any]): String = vars(n).toString
    }

  }

}
