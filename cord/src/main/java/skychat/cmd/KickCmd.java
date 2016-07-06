package skychat.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.pircbotx.hooks.types.GenericMessageEvent;
import skychat.SCIrc;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class KickCmd extends IrcCmd
{
    public KickCmd()
    {
        super("kick");
    }

    @Override
    protected void run(GenericMessageEvent event, String[] cmd)
    {
        if (cmd.length < 2) {
            event.respond("WRONG!");
            return;
        }
        String reason = Arrays.stream(cmd).skip(2).collect(Collectors.joining(" "));
        String user = cmd[1];
        if (!SCIrc.channels.stream()
                .map(SCIrc.bot.getUserChannelDao()::getChannel)
                .anyMatch(c -> c.isOp(event.getUser()))) {
            event.respond("DENIED!");
            return;
        }
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(user);
        if (player.getGroups().contains("admin")) {
            event.respond("DENIED!");
            return;
        }
        if (Objects.equals(reason, "")) {
            reason = "Kicked from server.";
        }
        player.disconnect(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', reason)));
        event.respond("Player kicked.");
    }
}
