package skychat

import net.minecraft.entity.player.EntityPlayerMP
import skychat.text.ChatColor

object Formatting {
  def chat(s: String, p: EntityPlayerMP): String = ChatColor.translateAlternateColorCodes('&',
    s.replace("{name}", p.getDisplayNameString)
      .replace("{world}", p.getServerWorld.getWorldInfo.getWorldName)
      .replace("{server}", p.getServer.getFolderName))

}
