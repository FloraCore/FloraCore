package team.floracore.common.util;

import org.jetbrains.annotations.*;

import java.util.*;

public final class StringUtil {
    private StringUtil() {
    }

    public static @NotNull String parseColour(@NotNull String string) {
        Objects.requireNonNull(string);
        return string.replace("&", "§").replace("§§", "&");
    }

    /**
     * 将字符串中的所有§颜色代码去除
     *
     * @param string 字符串
     * @return 去除后的颜色代码
     */
    public static @NotNull String removeColourCode(@NotNull String string) {
        Objects.requireNonNull(string);
        return string.replaceAll("§.", ""); // 正则替换
    }
}
