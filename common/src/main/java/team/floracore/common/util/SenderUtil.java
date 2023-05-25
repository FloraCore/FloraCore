package team.floracore.common.util;

import org.jetbrains.annotations.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.sender.*;

public final class SenderUtil {
    /**
     * 告诉命令发送者，该命令必须由玩家执行
     *
     * @param sender 命令发送者
     */
    public static void sendMustBe(@NotNull Sender sender, @NotNull Class<?> currentType, @NotNull Class<?> objectives) {
        MiscMessage.COMMAND_INVALID_COMMAND_SENDER.send(sender,
                currentType.getSimpleName(),
                objectives.getSimpleName());
    }

    /**
     * 判断命令发送者是否包含命令，若没有，告知没有权限消息
     *
     * @param sender     命令发送者
     * @param permission 权限节点
     *
     * @return 若没有权限节点，返回true
     */
    public static boolean sendIfNoPermission(Sender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            MiscMessage.COMMAND_NO_PERMISSION.send(sender);
            return true;
        }
        return false;
    }
}
