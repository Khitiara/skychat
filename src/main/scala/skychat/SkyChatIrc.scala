package skychat

import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.pircbotx.cap.SASLCapHandler
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.events._
import org.pircbotx.{PircBotX, User, Configuration => IrcConf}

import scala.collection.JavaConverters._

object SkyChatIrc extends ListenerAdapter {
  def broadcast(fmt: String): Unit = conf.getStringList("channels").asScala.foreach(bot.send().message(_, fmt))

  var conf: ConfigurationSection = _

  def init(configuration: ConfigurationSection) = conf = configuration

  var bot: PircBotX = _

  private var cmds: PartialFunction[String, (String, User) => Unit] = PartialFunction.empty

  def listen(f: PartialFunction[String, (String, User) => Unit]) = cmds = cmds.orElse(f)

  def buildBot(): Unit = {
    var config = new IrcConf.Builder()
      .setName(conf.getString("nick"))
      .setRealName(conf.getString("name"))
      .setAutoNickChange(true)
      .addAutoJoinChannels(conf.getStringList("channels"))
      .addListener(this)
    if (conf.getBoolean("sasl.enabled")) {
      config = config.addCapHandler(new SASLCapHandler(
        conf.getString("sasl.user")
        , conf.getString("sasl.pass")))
    }
    bot = new PircBotX(config.buildForServer(conf.getString("host"), conf.getInt("port")))
    bot.startBot()
  }

  val cmdPat = """\?([a-cA-C]+)(.*)""".r

  override def onMessage(event: MessageEvent): Unit = event.getMessage match {
    case cmdPat(cmd, args) => cmds.runWith(_ (args, event.getUser))(cmd)
    case msg =>
      Bukkit.broadcastMessage(TextFormat.ircChat(Map(
        "name" -> event.getUser.getNick,
        "msg" -> msg
      )))
  }

  override def onAction(event: ActionEvent): Unit =
    Bukkit.broadcastMessage(TextFormat.ircAction(Map(
      "name" -> event.getUser.getNick,
      "msg" -> event.getMessage
    )))

  override def onPart(event: PartEvent): Unit =
    Bukkit.broadcastMessage(s"${event.getUser.getNick} left IRC.")

  override def onQuit(event: QuitEvent): Unit =
    Bukkit.broadcastMessage(s"${event.getUser.getNick} left IRC.")

  override def onJoin(event: JoinEvent): Unit =
    Bukkit.broadcastMessage(s"${event.getUser.getNick} joined IRC.")
}
