package team.floracore.common.util;

import org.jetbrains.annotations.*;

import java.util.*;

public final class StringUtil {
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

    public static String joinList(List<String> list, int number) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(list.size(), number); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(list.get(i));
        }
        if (list.size() > number) {
            sb.append(", ...");
        }
        return sb.toString();
    }
}
