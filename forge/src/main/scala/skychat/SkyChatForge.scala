package skychat

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}

@Mod(modid = "skychat", name = "SkyChat", version = "@VERSION@", serverSideOnly = true, modLanguage = "scala")
object SkyChatForge {
  var conf: Configuration = null

  @EventHandler
  def preInit(e: FMLPreInitializationEvent): Unit = {
    conf = new Configuration(e.getSuggestedConfigurationFile)
    SCNet.init()
    SCHandlers.init()
    MinecraftForge.EVENT_BUS.register(SCHandlers)
  }

  @EventHandler
  def init(e: FMLInitializationEvent): Unit = {

  }

  @EventHandler
  def postInit(e: FMLPostInitializationEvent): Unit = {

  }
}
