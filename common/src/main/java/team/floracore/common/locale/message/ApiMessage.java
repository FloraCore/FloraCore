package team.floracore.common.locale.message;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

/**
 * API消息
 *
 * @author xLikeWATCHDOG
 */
public interface ApiMessage extends AbstractMessage {
	Args0 API_PLAYER_RANK_CONSUMER_NOT_FOUND = () -> AbstractMessage.prefixed(translatable()
			// 未找到合适的Rank设置器,请联系管理员
			.key("floracore.api.player.rank.consumer.not-found")
			.color(RED)
			.append(FULL_STOP));
}
