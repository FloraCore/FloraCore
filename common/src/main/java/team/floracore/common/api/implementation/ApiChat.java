package team.floracore.common.api.implementation;

import lombok.Getter;
import org.floracore.api.model.data.chat.ChatManager;
import team.floracore.common.plugin.FloraCorePlugin;

@Getter
public class ApiChat implements ChatManager {
	private final FloraCorePlugin plugin;

	public ApiChat(FloraCorePlugin plugin) {
		this.plugin = plugin;
	}

}
