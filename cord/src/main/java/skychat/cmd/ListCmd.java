package skychat.cmd;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.stream.Collectors;

public class ListCmd extends IrcCmd
{
    public ListCmd()
    {
        super("list");
    }

    @Override
    protected void run(GenericMessageEvent event, String[] cmd)
    {
        ProxyServer proxy = ProxyServer.getInstance();
        String names = proxy.getPlayers().stream()
                .map(ProxiedPlayer::getDisplayName)
                .collect(Collectors.joining(", "));
        event.respondWith(String.format("Online [%s]: %s", proxy.getOnlineCount(), names));
    }
}
