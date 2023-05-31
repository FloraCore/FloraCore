package team.floracore.bukkit.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import team.floracore.common.locale.translation.TranslationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bukkit字符串替换
 *
 * @author xLikeWATCHDOG
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
