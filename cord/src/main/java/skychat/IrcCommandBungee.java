package skychat;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;
import java.util.logging.Level;

class IrcCommandBungee extends Command
{

    IrcCommandBungee()
    {
        super("irc", "skychat.irc", "scirc");
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        String subCmd = args[0];
        if ("load".equalsIgnoreCase(subCmd)) {
            try {
                SCIrc.reloadConf();
            } catch (IOException e) {
                SkyChatBungee.getInstance().getLogger().log(Level.SEVERE, "IRC Error", e);
            }
        }
        if ("connect".equalsIgnoreCase(subCmd)) {
            SCIrc.asyncConnect();
        }
        if ("join".equalsIgnoreCase(subCmd)) {
            SCIrc.joinChannels();
        }
        if ("list".equalsIgnoreCase(subCmd)) {
            //TODO: IMPLEMENT
        }
        if("reg".equalsIgnoreCase(subCmd)) {
            String user = args[1];
            SCIrc.registerUser(user);
        }
    }
}
