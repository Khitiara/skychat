package skychat;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Level;

public class SkyChatBungee extends Plugin implements Listener
{
    private static SkyChatBungee instance;

    public static SkyChatBungee getInstance()
    {
        return instance;
    }

    @Override
    public void onEnable()
    {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }
        getProxy().getConfig();
        instance = this;
        try {
            File ircConf = new File(getDataFolder(), "irc.yml");
            if (!ircConf.exists()) {
                Files.copy(getResourceAsStream("irc.yml"), ircConf.toPath());
            }
            SCIrc.init(ircConf);
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Error loading configs", ex);
        }
        getProxy().registerChannel("SkyChat");
        PluginManager mgr = getProxy().getPluginManager();
        mgr.registerListener(this, this);
        mgr.registerCommand(this, new IrcCommandBungee());
        try {
            SCIrc.reloadConf();
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "IRC Error", ex);
        }
    }

    @Override
    public void onDisable()
    {
        SCIrc.quit();
        getProxy().unregisterChannel("SkyChat");
    }

    @EventHandler
    public void messageFromServer(PluginMessageEvent event)
    {
        if (!Objects.equals(event.getTag(), "SkyChat")) {
            return;
        }
        if (event.getSender() instanceof Server) {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String tag = in.readUTF();
            Server server = (Server) event.getSender();
            if ("CHAT".equalsIgnoreCase(tag)) {
                handleChat(server, in);
            }
            if ("SYNC".equalsIgnoreCase(tag)) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("SYNC");
                out.writeUTF(in.readUTF());
                boolean reply = in.readBoolean();
                out.writeBoolean(reply);
                if (reply) {
                    out.writeUTF(in.readUTF());
                }
            }
            if ("AFK".equalsIgnoreCase(tag)) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("AFK");
                out.writeUTF(in.readUTF());
                out.writeBoolean(in.readBoolean());
            }
            if("MSG".equalsIgnoreCase(tag)) {
                String tag1 = in.readUTF();
            }
        }
    }

    private void handleChat(Server server, ByteArrayDataInput in)
    {
        SCMsg msg = SCMsg.loadFrom(in);
        for (ServerInfo s : getProxy().getServers().values()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("CHAT");
            msg.writeTo(out);
            if (s.getPlayers().size() > 0 && s != server.getInfo()) {
                s.sendData("SkyChat", out.toByteArray());
            }
        }
        SCIrc.dispatch(msg);
    }

    public void dispatch(String msg)
    {
        SCMsg m = new SCMsg(msg);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("CHAT");
        m.writeTo(out);
        getProxy().getServers().values().stream()
                .filter(serverInfo -> serverInfo.getPlayers().size() > 0)
                .forEach(serverInfo -> serverInfo.sendData("RoboChat", out.toByteArray()));
    }
}
