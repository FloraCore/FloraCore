package team.floracore.common.commands.test;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import cloud.commandframework.annotations.specifier.*;
import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import org.bukkit.command.*;
import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.command.*;
import team.floracore.common.plugin.*;

import java.util.*;

/**
 * 该类请后续开发者不要删除，这是一个参考实现。几乎实现了Cloud命令框架中存在的所有可能。
 * {@see <a href="https://github.com/Incendo/cloud/blob/master/examples/example-bukkit/src/main/java/cloud/commandframework/examples/bukkit/ExamplePlugin.java">官方实现例子</a>}
 * 该类注册了一个test命令，统一需要权限才能使用。
 */
@CommandContainer
@CommandPermission("admin.test")
public class TestCommand extends AbstractFloraCoreCommand {
    public TestCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    /**
     * 创建test根命令。
     * 其中对下面各个注解进行解释。
     * <p>
     * 所有命令方法都必须使用注解@CommandMethod。注解的值存在一定的结构，使用以下语法：
     * - 命令名：name
     * - 必需参数：<name> （可选）
     * - 可选参数：[name] （可选）
     * <p>
     * 可以放在命令方法上的注解@CommandDescription，可以对该命令进行描述。（可选）
     * <p>
     * 可以放在命令方法或包含命令方法的类的注解@CommandPermission，可以指定使用命令所需的权限。（可选）
     * 值得注意的是，该注解可以放在本类上。
     *
     * @param sender 该部分会自动传入，为了保证安全性，可以加上{@link org.checkerframework.checker.nullness.qual.NonNull}
     */
    @CommandMethod("test")
    @CommandDescription("测试根命令")
    public void test(final @NonNull CommandSender sender) {
        sender.sendMessage("这是一个测试命令。");
    }

    @CommandMethod("test c1")
    @CommandDescription("测试c1命令")
    public void test_c1(final @NonNull CommandSender sender) {
        sender.sendMessage("我是c1命令。");
    }

    @CommandMethod("test c1 b1")
    @CommandDescription("测试c1命令")
    public void test_c1_b1(final @NonNull CommandSender sender) {
        sender.sendMessage("我是c1中的b1命令。");
    }

    @CommandMethod("test c2")
    @CommandPermission("admin.test1")
    @CommandDescription("测试c2命令")
    public void test_c2(final @NonNull CommandSender sender) {
        sender.sendMessage("我是c2命令。");
    }

    @Suggestions("test_c2_a1_sug")
    public @NonNull List<String> test_c2_a1_sug(final @NonNull CommandContext<CommandSender> ctx, final @NonNull String input) {
        List<String> ret = new ArrayList<>();
        ret.add("测试命令1");
        ret.add("测试命令2");
        return ret;
    }

    /**
     * 在这个命令中，唯一不同的，是调用了@Suggestions，因此当我们使用这个命令的时候，使用tab是可以获取推荐参数的。
     *
     * @param query 此为参数，参数即由[]或<>包裹的内容。
     */
    @CommandMethod("test c2 a1 [query]")
    @CommandDescription("测试c2命令")
    public void test_c2_a1_qu(final @NonNull CommandSender sender, final @Argument(value = "query", suggestions = "test_c2_a1_sug") @Greedy String query) {
        sender.sendMessage("我是c2中的a1命令，你选择的query是" + query);
    }

    @CommandMethod("test c2 a2 [query]")
    @CommandDescription("测试c2 a2命令")
    public void test_c2_a2_qu(final @NonNull CommandSender sender, final @Argument(value = "query", suggestions = "onlinePlayers") @Greedy String query) {
        sender.sendMessage("我是c2中的a2命令，你选择的query是" + query);
    }
}
