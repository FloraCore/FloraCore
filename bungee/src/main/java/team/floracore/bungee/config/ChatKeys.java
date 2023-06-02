package team.floracore.bungee.config;

import org.floracore.api.bungee.chat.ChatChannel;
import team.floracore.common.config.generic.KeyedConfiguration;
import team.floracore.common.config.generic.key.ConfigKey;
import team.floracore.common.config.generic.key.SimpleConfigKey;

import java.util.ArrayList;
import java.util.List;

import static team.floracore.common.config.generic.key.ConfigKeyFactory.key;

/**
 * All of the {@link ConfigKey}s used by FloraCore.
 *
 * <p>The {@link #getKeys()} method and associated behaviour allows this class
 * to function a bit like an enum, but with generics.</p>
 */
public class ChatKeys {
    public static final ConfigKey<List<ChatChannel>> CHAT_MODELS = key(c -> {
        List<ChatChannel> ret = new ArrayList<>();
        for (String name : c.getKeys()) {
            String root = "floracore.chatchannels." + name + ".";
            boolean enableChatColor = c.getBoolean(root + "enableColorChar", true);
            List<String> permissions = c.getStringList(root + "permissions", new ArrayList<>());

        }
        return ret;
    });

    /**
     * A list of the keys defined in this class.
     */
    private static final List<SimpleConfigKey<?>> KEYS = KeyedConfiguration.initialise(ChatKeys.class);

    private ChatKeys() {
    }

    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }
}
