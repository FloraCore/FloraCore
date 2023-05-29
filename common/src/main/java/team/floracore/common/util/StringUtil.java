package team.floracore.common.util;

import com.google.common.collect.*;
import org.jetbrains.annotations.*;

import java.nio.charset.*;
import java.util.*;

public final class StringUtil {
    public static Charset UTF8 = StandardCharsets.UTF_8;

    public @Deprecated StringUtil() {
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

    public static String[] split(String string, String regex) {
        if (string.length() == 0) {
            return new String[0];
        }
        return string.split(regex);
    }

    public static boolean endsWithIgnoreCase(String a, String b) {
        if (a == null || b == null) {
            return a == null && b == null;
        }
        return a.toLowerCase().endsWith(b.toLowerCase());
    }

    /**
     * 合并一些字符串
     *
     * @param mergeIndex 可以合并的字符串的索引
     * @param stringsNum 合并后的字符串数量
     * @param strings    一些字符串
     * @return 合并后的字符串, 合并的用空格分开
     */
    public static String[] mergeStrings(int mergeIndex, int stringsNum, String... strings) {
        if (strings.length < stringsNum) {
            return strings;
        } else if (strings.length == stringsNum) {
            return strings;
        }
        List<String> rl = new ArrayList<>(Arrays.asList(strings).subList(0, mergeIndex));
        StringBuilder merge = new StringBuilder(strings[mergeIndex]);
        //2 3 +
        for (int i = mergeIndex + 1; i <= mergeIndex + strings.length - stringsNum; i++) {
            merge.append(" ").append(strings[i]);
        }
        rl.add(merge.toString());
        rl.addAll(Arrays.asList(strings).subList(mergeIndex + strings.length - stringsNum + 1, strings.length));
        return rl.toArray(new String[rl.size()]);
    }

    public static String mergeStrings(String separator, String[] strings) {
        if (strings.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            sb.append(separator);
            sb.append(strings[i]);
        }
        return sb.toString();
    }

    @SafeVarargs
    public static String replaceStrings(String raw, Map.Entry<String, String>... beforeAndAfter) {
        return replaceStrings(raw, ListUtil.toMap(Lists.newArrayList(beforeAndAfter)));
    }

    public static String replaceStrings(String raw, Map<String, String> beforeAndAfter) {
        String[] ss = {raw};
        for (Map.Entry<String, String> t : beforeAndAfter.entrySet()) {
            List<String> ts = new LinkedList<>();
            for (String s : ss) {
                boolean c = true;
                for (String k : beforeAndAfter.keySet()) {
                    if (Objects.equals(t.getValue(), k)) {
                        break;
                    }
                    if (Objects.equals(s, k)) {
                        c = false;
                        break;
                    }
                }
                if (c) {
                    String[] tts = (" " + s + " ").split(t.getKey());
                    tts[0] = tts[0].substring(1);
                    tts[tts.length - 1] = tts[tts.length - 1].substring(0, tts[tts.length - 1].length() - 1);
                    for (String d : tts) {
                        ts.add(d);
                        ts.add(t.getValue());
                    }
                    ts.remove(ts.size() - 1);
                } else {
                    ts.add(s);
                }
            }
            ss = ts.toArray(ss);
        }
        return mergeStrings(ss);
    }

    public static String mergeStrings(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String codeString(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case '\r':
                    sb.append("\\r");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\0':
                    sb.append("\\0");
                    break;
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\'':
                    sb.append("\\'");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return '\"' + sb.toString() + '\"';
    }

    public static boolean containsIgnoreCase(List<String> list, String str) {
        for (String s : list) {
            if (s.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsAny(String str, List<String> ss) {
        for (String s : ss) {
            if (str.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsAnyIgnoreCase(String str, List<String> ss) {
        str = str.toLowerCase();
        for (String s : ss) {
            if (str.contains(s.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean startsWithAny(String str, Collection<String> ss) {
        for (String s : ss) {
            if (str.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean startsWithAnyIgnoreCase(String str, Collection<String> ss) {
        for (String s : ss) {
            if (StringUtil.startsWithIgnoreCase(str, s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean startsWithIgnoreCase(String a, String b) {
        if (a == null || b == null) {
            return a == null && b == null;
        }
        return a.toLowerCase().startsWith(b.toLowerCase());
    }

    public static int sum(String str, List<String> ss) {
        int num = 0;
        for (String s : ss) {
            num += sum(str, s);
        }
        return num;
    }

    public static int sum(String str, String s) {
        int num = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.substring(i).startsWith(s)) {
                num++;
            }
        }
        return num;
    }

    public static int sumIgnoreCase(String str, List<String> ss) {
        int num = 0;
        for (String s : ss) {
            num += sumIgnoreCase(str, s);
        }
        return num;
    }

    public static int sumIgnoreCase(String str, String s) {
        int num = 0;
        for (int i = 0; i < str.length(); i++) {
            if (startsWithIgnoreCase(str.substring(i), s)) {
                num++;
            }
        }
        return num;
    }

    /**
     * <p>
     * Gets a substring from the specified String avoiding exceptions.
     * </p>
     *
     * <p>
     * A negative start position can be used to start/end <code>n</code>
     * characters from the end of the String.
     * </p>
     *
     * <p>
     * The returned substring starts with the character in the <code>start</code>
     * position and ends before the <code>end</code> position. All position counting is
     * zero-based -- i.e., to start at the beginning of the string use
     * <code>start = 0</code>. Negative start and end positions can be used to
     * specify offsets relative to the end of the String.
     * </p>
     *
     * <p>
     * If <code>start</code> is not strictly to the left of <code>end</code>, ""
     * is returned.
     * </p>
     *
     * <pre>
     * StringUtils.substring(null, *, *)    = null
     * StringUtils.substring("", * ,  *)    = "";
     * StringUtils.substring("abc", 0, 2)   = "ab"
     * StringUtils.substring("abc", 2, 0)   = ""
     * StringUtils.substring("abc", 2, 4)   = "c"
     * StringUtils.substring("abc", 4, 6)   = ""
     * StringUtils.substring("abc", 2, 2)   = ""
     * StringUtils.substring("abc", -2, -1) = "b"
     * StringUtils.substring("abc", -4, 2)  = "ab"
     * </pre>
     *
     * @param str   the String to get the substring from, may be null
     * @param start the position to start from, negative means
     *              count back from the end of the String by this many characters
     * @param end   the position to end at (exclusive), negative means
     *              count back from the end of the String by this many characters
     * @return substring from start position to end positon,
     * <code>null</code> if null String input
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }

        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        // check length next
        if (end > str.length()) {
            end = str.length();
        }

        // if start is greater than end, return ""
        if (start > end) {
            return "";
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }
}
