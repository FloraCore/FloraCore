package org.floracore.api.translation;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * 国际化多语言API
 *
 * @author xLikeWATCHDOG
 */
public interface TranslationManager {
	/**
	 * 加载自定义翻译文件目录。
	 * 该功能的主要作用是为依赖于FloraCore的插件提供自定义国际化多语言功能,
	 * 通过调用该方法,插件可以创建自己独立的翻译内容。
	 * 同时,FloraCore的原先翻译内容仍然存在。
	 * <p>
	 * 注意：参数"suppressDuplicatesError"用于控制是否抑制重复错误。
	 * 它是一个布尔类型的参数,用于指定在加载翻译文件时是否抑制重复错误。
	 * 如果该参数为"true",则不会记录警告日志,以避免重复错误；
	 * 如果该参数为"false",则会记录警告日志。
	 *
	 * @param directory               包含翻译文件的目录
	 * @param suppressDuplicatesError 是否抑制重复错误
	 */
	void loadCustomLanguageFile(Path directory, boolean suppressDuplicatesError);

	void loadFromResourceBundle(ResourceBundle bundle, Locale locale);

	/**
	 * 注意：参数"sender",必须为Sender的子类。
	 *
	 * @param sender    玩家
	 * @param component 信息组件
	 */
	void sendMessage(@NotNull Object sender, @NotNull Component component);

	/**
	 * 向控制台发送信息。
	 *
	 * @param component 信息组件
	 */
	void sendConsoleMessage(@NotNull Component component);

	/**
	 * 通过UUID获取目标玩家选择的语言。
	 * 若无选择语言，或不存在这名玩家,则会返回默认的语言。
	 *
	 * @param component 未翻译的信息组件
	 * @param uuid      玩家UUID
	 * @return 已翻译的信息组件
	 */
	Component render(Component component, @NotNull UUID uuid);
}
