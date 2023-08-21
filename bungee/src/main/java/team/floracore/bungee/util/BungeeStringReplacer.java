package team.floracore.bungee.util;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
public class BungeeStringReplacer {
	public static List<String> processStringListForPlayer(ProxiedPlayer player, List<String> input) {
		List<String> ret = new ArrayList<>();
		for (String s : input) {
			ret.add(processStringForPlayer(player, s));
		}
		return ret;
	}

	public static String processStringForPlayer(ProxiedPlayer player, String input) {
		UUID uuid = player.getUniqueId();
		return processStringForPlayer(uuid, input);
	}

	public static String processStringForPlayer(UUID uuid, String input) {
		// 匹配以$开头和结尾的内容，括号内为非贪婪匹配
		String regex = "\\$(.*?)\\$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		StringBuffer output = new StringBuffer();
		while (matcher.find()) {
			// 获取xxx的内容
			String match = matcher.group(1);
			// 进行替换的加工操作
			Component component = Component.translatable().key(match).build();
			Component replacement = TranslationManager.render(component, uuid);
			String str = TranslationManager.SERIALIZER.serialize(replacement);
			matcher.appendReplacement(output, str);
		}
		matcher.appendTail(output);
		return output.toString();
	}

	public static String translateColorCodes(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

}
