package org.floracore.api.messenger.message.type;

import org.floracore.api.messenger.message.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * 通过Bukkit向BungeeCord发送名字修改信息。
 */
public interface ChangeNameMessage extends Message {
    /**
     * 获取改变名字的玩家的UUID
     *
     * @return 改变名字的玩家的UUID
     */
    @NotNull UUID getChanger();

    /**
     * 新名字,不能为null
     *
     * @return 新名字, 不为null
     */
    @NotNull String getName();
}
