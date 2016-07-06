package skychat.cmd;

import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;
import skychat.SCIrc;

public abstract class IrcCmd
{
    private final String name;

    public IrcCmd(String name)
    {
        this.name = name;
        SCIrc.commands.add(this);
    }

    public Listener setup()
    {
        return new ListenerAdapter()
        {
            @Override
            public void onGenericMessage(GenericMessageEvent event) throws Exception
            {
                if (event.getMessage().startsWith("?")) {
                    String[] cmd = event.getMessage().substring(1).split(" ");
                    if (name.equalsIgnoreCase(cmd[0])) {
                        run(event, cmd);
                    }
                }
            }
        };
    }

    protected abstract void run(GenericMessageEvent event, String[] cmd);
}
