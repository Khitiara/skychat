package skychat

import org.bukkit.configuration.Configuration
import org.pircbotx.cap.SASLCapHandler
import org.pircbotx.{PircBotX, Configuration => IrcConf}

object SkyChatIrc {
  var conf: Configuration = _

  def init(configuration: Configuration) = conf = configuration

  var bot: PircBotX = _

  def buildBot(): Unit = {
    var config = new IrcConf.Builder()
      .setName(conf.getString("nick"))
      .setRealName(conf.getString("name"))
      .setAutoNickChange(true)
      .addAutoJoinChannel(conf.getString("chan"))
    if (conf.getBoolean("sasl.enabled")) {
      config = config.addCapHandler(new SASLCapHandler(
        conf.getString("sasl.name")
        , conf.getString("sasl.pass")))
    }
    bot = new PircBotX(config.buildForServer(conf.getString("host"), conf.getInt("port")))
    bot.startBot()
  }
}
