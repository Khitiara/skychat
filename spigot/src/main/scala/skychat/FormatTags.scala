package skychat

import me.clip.placeholderapi.PlaceholderAPI
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player

import scala.util.Try

object FormatTags {
  def chat(fmt: String, p: Player): String = {
    if (fmt == null) return null
    val mgr = Bukkit.getPluginManager
    val name = p.getName
    val nick = p.getDisplayName
    var prefix, suffix, grp, grpPrefix, grpSuffix = ""
    Try({
      prefix = Formatting.FormatStringAll(SkyChatSpigot.chat.getPlayerPrefix(p))
      suffix = Formatting.FormatStringAll(SkyChatSpigot.chat.getPlayerSuffix(p))
      grp = SkyChatSpigot.chat.getPrimaryGroup(p)
      grpPrefix = Formatting.FormatStringAll(SkyChatSpigot.chat.getGroupPrefix(p.getWorld, grp))
      grpSuffix = Formatting.FormatStringAll(SkyChatSpigot.chat.getGroupSuffix(p.getWorld, grp))
    }).recover {
      case _ => SkyChatSpigot.log.warning("Prefix/Suffix don't exist, setting to \"\"")
    }
    val newFmt = Formatting.FormatStringAll(ChatColor.translateAlternateColorCodes('&', fmt)
      .replace("{nick}", nick)
      .replace("{name}", name)
      .replace("{suffix}", suffix)
      .replace("{prefix}", prefix)
      .replace("{server}", p.getServer.getServerName)
      .replace("{group}", grp)
      .replace("{groupPrefix}", grpPrefix)
      .replace("{groupSuffix}", grpSuffix)
      .replace("{world}", p.getWorld.getName))
    if (mgr.isPluginEnabled("PlaceholderAPI"))
      PlaceholderAPI.setBracketPlaceholders(p, Formatting.FormatStringAll(newFmt))
    else
      newFmt
  }
}
