package team.floracore.bungee.messaging.chat;

import java.util.List;

/**
 * 自定义聊天频道模型
 *
 * @author xLikeWATCHDOG
 */
public class ChatModel {
    public final transient String name;
    public final String prefix;
    public final String permission;
    public final List<String> identifiers;

    public ChatModel(String name, String prefix, String permission, List<String> identifiers) {
        this.name = name;
        this.prefix = prefix;
        this.permission = permission;
        this.identifiers = identifiers;
    }
}
