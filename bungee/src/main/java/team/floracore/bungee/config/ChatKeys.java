package team.floracore.bungee.config;

import team.floracore.bungee.chat.ChatModel;
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
	public static final ConfigKey<List<ChatModel>> CHAT_MODELS = key(c -> {
		List<ChatModel> ret = new ArrayList<>();
		List<String> channels = c.getStringList("enabled-channel-list", new ArrayList<>());
		for (String channel : channels) {
			String prefix = c.getString("channels." + channel + ".prefix", "null");
			String permission = c.getString("channels." + channel + ".permission", "floracore.socialsystems.custom");
			List<String> identifiers = c.getStringList("channels." + channel + ".identifiers", new ArrayList<>());
			ChatModel chatModel = new ChatModel(channel, prefix, permission, identifiers);
			ret.add(chatModel);
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
