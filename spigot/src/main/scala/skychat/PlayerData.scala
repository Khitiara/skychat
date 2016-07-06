package skychat

import java.io.File
import java.util.UUID

import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.{FileConfiguration, YamlConfiguration}
import org.bukkit.entity.Player

import scala.collection.mutable
import scala.collection.convert.decorateAsScala._

object PlayerData {


  private var file: File = null
  private var data: FileConfiguration = null
  var players: mutable.Buffer[SCPlayer] = mutable.Buffer()
  var online: mutable.Buffer[SCPlayer] = mutable.Buffer()

  def init(): Unit = {
    file = new File(SkyChatSpigot.inst.getDataFolder, "Players.yml")
    if (!file.isFile)
      SkyChatSpigot.inst.saveResource("Players.yml", true)
    data = YamlConfiguration.loadConfiguration(file)
  }

  def save(): Unit = {

  }

  def load(): Unit = {
    val section: ConfigurationSection = data.getConfigurationSection("players")
    players ++= section.getValues(false).asScala.map {
      case (s: String, section: ConfigurationSection) =>
        val id = UUID.fromString(s)
        val name = section.getString("name")
        val nick = section.getString("nick", "")
        val reply = Option(section.getString("reply")).map(UUID.fromString)
        SCPlayer(id, name, nick, replyPlayer = reply)
    }
  }

  def getPlayer(p: Player): SCPlayer =
    getPlayer(p.getUniqueId)

  def getPlayer(id: UUID): SCPlayer =
    players.find(_.id == id).orNull

  def getOnlinePlayer(name: String) =
    online.find(_.name == name).orNull

  def getOnlinePlayer(id: UUID) =
    online.find(_.id == id).orNull

  def getPlayer(name: String) =
    players.find(_.name == name).orNull

  def markOnline(): Unit = {
    online.clear()
    online ++= Bukkit.getOnlinePlayers.asScala.map(p => {
      getPlayer(p).setOnline(true)
    })
  }
}
