package robochat

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}

@Mod(modid = "robochat", name = "RoboChat", version = "@VERSION@", serverSideOnly = true, modLanguage = "scala")
object RoboChat {
  @EventHandler
  def preInit(e: FMLPreInitializationEvent): Unit = {
    RCNet.init()
    RCHandlers.init()
  }

  @EventHandler
  def init(e: FMLInitializationEvent): Unit = {

  }

  @EventHandler
  def postInit(e: FMLPostInitializationEvent): Unit = {

  }
}
