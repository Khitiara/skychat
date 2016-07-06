package skychat.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;

@SuppressWarnings("ConstantConditions") public class JoinPart extends ListenerAdapter
{
    @Override
    public void onJoin(JoinEvent event) throws Exception
    {
        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(String.format(
                "[IRC] %s joined %s.", event.getUser().getNick(), event.getChannel().getName()
        )));
    }

    @Override
    public void onPart(PartEvent event) throws Exception
    {
        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(String.format("[IRC] %s parted %s.", event.getUser().getNick(), event.getChannel().getName())));
    }

    @Override
    public void onQuit(QuitEvent event) throws Exception
    {
        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(String.format("[IRC] %s quit.", event.getUser().getNick())));
    }
}
