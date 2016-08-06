package skychat

import java.io.File
import java.util
import java.util.UUID

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.event.player.{PlayerJoinEvent, PlayerQuitEvent}
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.scheduler.BukkitTask
import org.bukkit.{Bukkit, OfflinePlayer}

import scala.collection.JavaConverters._
import scala.collection.mutable

object PlayerData extends Listener {

  case class Player(id: UUID, replyTarget: Option[UUID], online: Boolean = false) extends ConfigurationSerializable {
    def spigotPlayer = Bukkit.getOfflinePlayer(id)

    override def serialize(): util.Map[String, AnyRef] = Map[String, AnyRef]("id" -> id.toString, "reply" -> replyTarget.toString).asJava
  }

  object Player {
    def deserialize(m: util.Map[String, AnyRef]) = Player(UUID.fromString(m.get("id").asInstanceOf[String]), m.asScala.get("reply").map(b => UUID.fromString(b.asInstanceOf[String])))

    def update(p: OfflinePlayer): Player = {
      val player: Player = players.getOrElse(p.getUniqueId, Player(p.getUniqueId, null)).copy(online = p.isOnline)
      players(p.getUniqueId) = player
      player
    }
  }

  var cfg: YamlConfiguration = _

  var players: mutable.Map[UUID, Player] = _

  val file: File = new File(SkyChat.inst.getDataFolder, "players.yml")

  def init(): Unit = {
    SkyChat.inst.saveResource("players.yml", false)
    cfg = YamlConfiguration.loadConfiguration(file)
    players = cfg.getValues(false).asScala.map {
      case (k: String, p: Player) => UUID.fromString(k) -> p
    }
  }

  def getPlayer(id: UUID) = players.get(id)
  def getPlayer(p: OfflinePlayer) = getPlayer(p.getUniqueId)

  def update(p: Player): Player = {
    players(p.id) = p
    p
  }

  @EventHandler
  def onJoin(e: PlayerJoinEvent): Unit = {
    Player.update(e.getPlayer)
    SaveTask.queue()
  }

  @EventHandler
  def onLeave(e: PlayerQuitEvent): Unit = {
    Player.update(e.getPlayer)
    SaveTask.queue()
  }

  object SaveTask extends Runnable {
    def queue(): Unit = {
      save = save.orElse(Some(Bukkit.getScheduler.runTask(SkyChat.inst, this)))
    }

    var save: Option[BukkitTask] = None

    override def run(): Unit = {
      cfg.set("players", players.map {
        case (k, v) => k.toString -> v
      }.asJava)
      cfg.save(file)
      save = None
    }
  }
}
