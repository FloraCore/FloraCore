package team.floracore.bukkit.util;

import me.clip.placeholderapi.*;
import net.kyori.adventure.text.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import team.floracore.common.locale.translation.*;

import java.util.*;
import java.util.regex.*;

/**
 * Bukkit字符串替换
 *
 * @author xLikeWATCHDOG
 * @date 2023/5/29 20:12
 */
public class BukkitStringReplacer {
    public static List<String> processStringListForPlayer(Player player, List<String> input) {
        List<String> ret = new ArrayList<>();
        for (String s : input) {
            ret.add(processStringForPlayer(player, s));
        }
        return ret;
    }

    public static String processStringForPlayer(Player player, String input) {
        UUID uuid = player.getUniqueId();
        // 匹配以$开头和结尾的内容，括号内为非贪婪匹配
        String regex = "\\$(.*?)\\$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        StringBuffer output = new StringBuffer();
        while (matcher.find()) {
            // 获取xxx的内容
            String match = matcher.group(1);
            // 进行替换的加工操作，这里使用了"Replaced(" + match + ")"作为替换字符串
            Component component = Component.translatable().key(match).build();
            Component replacement = TranslationManager.render(component, uuid);
            String str = TranslationManager.SERIALIZER.serialize(replacement);
            matcher.appendReplacement(output, str);
        }
        matcher.appendTail(output);
        String result = output.toString();

        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            result = PlaceholderAPI.setPlaceholders(player, result);
        }

        result = translateColorCodes(result);
        return result;
    }

    public static String translateColorCodes(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
