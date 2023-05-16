package team.floracore.common.commands.socialsystems;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.command.*;
import team.floracore.common.plugin.*;

import java.util.*;

@CommandContainer
@CommandDescription("组队是一个社交系统。玩家可以与其他玩家一起游玩")
public class PartyCommand extends AbstractFloraCoreCommand {
    public PartyCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("party|p <target>")
    public void partyInvite(final @NonNull Player player, final @NonNull @Argument(value = "target", suggestions = "onlinePlayers") String target) {
        partyInviteI(player, target);
    }

    @CommandMethod("party|p invite <target>")
    public void partyInviteI(final @NonNull Player player, final @NonNull @Argument(value = "target", suggestions = "onlinePlayers") String target) {
        // TODO 创建队伍
        // 随机Party UUID
        UUID partyUUID = UUID.randomUUID();
        // 获取队长的UUID
        UUID leader = player.getUniqueId();
        long createTime = System.currentTimeMillis();
        // 创建Chat
        // TODO 搜寻玩家并邀请和发送通知
    }
}
