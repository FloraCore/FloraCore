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
    public static final ConfigKey<List<ChatChannel>> CHAT_CHANNELS = key(c -> {
        List<ChatChannel> ret = new ArrayList<>();
        for (String key : c.getKeys()) {
            String root = "floracore.chatchannels." + key;
            String name = c.getString(root, key);
            boolean enableChatColor = c.getBoolean(root + ".enableColorChar", true);
            List<String> permissions = c.getStringList(root + ".permissions", new ArrayList<>());
            List<String> commands = c.getStringList(root + ".commands", new ArrayList<>());
            List<String> identifiersIn = c.getStringList(root + ".identifiers", new ArrayList<>());
            String[] identifiers = new String[]{};
            if (!identifiersIn.isEmpty()) {
                identifiers = new String[identifiersIn.size()];
                for (int i = 0; i < identifiersIn.size(); i++) {
                    identifiers[i] = identifiersIn.get(i);
                }
            }
            ret.add(new ChatChannel(key, name, enableChatColor, commands, permissions, identifiers));
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
