package skychat.listener;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import skychat.SCIrc;
import skychat.SkyChatBungee;

@SuppressWarnings("ConstantConditions") public class Chan extends ListenerAdapter
{
    @Override
    public void onAction(ActionEvent event) throws Exception
    {
        if (event.getChannel() == null) {
            return;
        }
        String fmt = SCIrc.conf.getString("format.action")
                .replace("{nick}", event.getUser().getNick())
                .replace("{action}", event.getAction())
                .replace("{server}", "IRC")
                .replace("{world}", "IRC");
        SkyChatBungee.getInstance().dispatch(fmt);
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception
    {
        if (event.getChannel() == null) {
            return;
        }
        String fmt = SCIrc.conf.getString("format.msg")
                .replace("{nick}", event.getUser().getNick())
                .replace("{action}", event.getMessage())
                .replace("{server}", "IRC")
                .replace("{world}", "IRC");
        SkyChatBungee.getInstance().dispatch(fmt);
    }
}
