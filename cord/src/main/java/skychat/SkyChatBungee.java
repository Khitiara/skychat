package skychat;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class SkyChatBungee extends Plugin
{
    @Override
    public void onEnable()
    {
        getProxy().registerChannel("SkyChat");
    }

    @Override
    public void onDisable()
    {
        getProxy().unregisterChannel("SkyChat");
    }

    @EventHandler
    public void messageFromServer(PluginMessageEvent event) {

    }
}
