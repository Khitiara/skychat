package skychat.listener;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import skychat.SCIrc;
import skychat.SkyChatBungee;

@SuppressWarnings("ConstantConditions") public class Chat extends ListenerAdapter
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
}
