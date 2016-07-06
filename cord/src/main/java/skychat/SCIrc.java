package skychat;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.SASLCapHandler;
import org.pircbotx.exception.IrcException;
import skychat.cmd.IrcCmd;
import skychat.cmd.KickCmd;
import skychat.cmd.ListCmd;
import skychat.listener.Chan;
import skychat.listener.JoinPart;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SCIrc
{
    public static Configuration conf;
    private static File confFile;
    private static ConfigurationProvider provider;
    private static org.pircbotx.Configuration botConf;
    public static PircBotX bot;
    public static List<String> channels;
    public static List<IrcCmd> commands;

    static void init(File conf)
    {
        SCIrc.confFile = conf;
        provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    }

    static void reloadConf() throws IOException
    {
        conf = provider.load(confFile);
        if (bot != null) {
            quit();
        }
        Builder builder = new Builder();
        builder.setName(conf.getString("nick"));
        builder.setName(conf.getString("user"));
        builder.setRealName(conf.getString("name"));
        builder.setCapEnabled(true);
        if (conf.getBoolean("sasl.enabled")) {
            String saslUser = conf.getString("sasl.user");
            String saslPass = conf.getString("sasl.pass");
            builder.addCapHandler(new SASLCapHandler(saslUser, saslPass));
        }
        Configuration server = conf.getSection("server");
        String host = server.getString("host");
        int port = server.getInt("port");
        builder.addServer(host, port);
        channels = conf.getStringList("channels").stream()
                .map(s -> "#" + s)
                .collect(Collectors.toList());
        builder.addAutoJoinChannels(channels);

        addListeners(builder);

        botConf = builder.buildConfiguration();
        bot = new PircBotX(botConf);

        if (conf.getBoolean("auto-connect")) {
            asyncConnect();
        }
    }

    private static void addListeners(Builder builder)
    {
        builder.addListener(new Chan())
                .addListener(new JoinPart())
                .addListener(new ListCmd().setup())
                .addListener(new KickCmd().setup());
    }

    static void asyncConnect()
    {
        ProxyServer.getInstance().getScheduler().runAsync(SkyChatBungee.getInstance(), () -> {
            try {
                if (!bot.isConnected()) {
                    SkyChatBungee.getInstance().getLogger().info("Connecting to IRC...");
                    bot.startBot();
                }
            } catch (IrcException | IOException e) {
                SkyChatBungee.getInstance().getLogger().log(Level.SEVERE, "Problem connecting to "
                        + botConf.getServers().get(0).getHostname(), e);
            }
        });
    }

    static void joinChannels()
    {
        for (String channel : channels) {
            bot.send().joinChannel(channel);
        }
    }

    static void registerUser(String user)
    {
        for (String channel : channels) {
            bot.getUserChannelDao().getChannel(channel).send().message("!vop add " + user);
        }
    }

    static void quit()
    {
        bot.stopBotReconnect();
        bot.send().quitServer();
        bot = null;
    }

    static void dispatch(SCMsg msg)
    {
        for (String channel : channels) {
            bot.getUserChannelDao().getChannel(channel).send().message(msg.msgFull);
        }
    }
}