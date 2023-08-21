package team.floracore.bungee.command.impl.test;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.CommandSender;
import org.jetbrains.annotations.NotNull;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.command.FloraCoreBungeeCommand;
import team.floracore.common.sender.Sender;

/**
 * 该类请后续开发者不要删除,这是一个参考实现。几乎实现了Cloud命令框架中存在的所有可能。
 * {@see
 * <a href="https://github.com/Incendo/cloud/blob/master/examples/example-bukkit/src/main/java/cloud/commandframework/examples/bukkit/ExamplePlugin.java">官方实现例子</a>}
 * 该类注册了一个test命令,统一需要权限才能使用。
 */
@CommandContainer
@CommandPermission("admin.test")
public class TestCommand extends FloraCoreBungeeCommand {
	public TestCommand(FCBungeePlugin plugin) {
		super(plugin);
	}

	@CommandMethod("test-bc i18n <text>")
	public void i18n(final @NotNull CommandSender sender, @NotNull @Argument("text") String text) {
		Sender s = getPlugin().getSenderFactory().wrap(sender);
		Component component = Component.translatable().key(text).build();
		s.sendMessage(component);
	}
}
