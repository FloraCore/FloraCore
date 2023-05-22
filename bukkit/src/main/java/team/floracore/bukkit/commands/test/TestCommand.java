package team.floracore.bukkit.commands.test;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import cloud.commandframework.annotations.specifier.*;
import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import net.kyori.adventure.audience.*;
import net.kyori.adventure.bossbar.*;
import net.kyori.adventure.inventory.*;
import net.kyori.adventure.text.*;
import net.luckperms.api.*;
import net.luckperms.api.model.user.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.command.*;

import java.util.*;

/**
 * 该类请后续开发者不要删除，这是一个参考实现。几乎实现了Cloud命令框架中存在的所有可能。
 * {@see <a href="https://github.com/Incendo/cloud/blob/master/examples/example-bukkit/src/main/java/cloud/commandframework/examples/bukkit/ExamplePlugin.java">官方实现例子</a>}
 * 该类注册了一个test命令，统一需要权限才能使用。
 */
@CommandContainer
@CommandPermission("admin.test")
public class TestCommand extends AbstractFloraCoreCommand {
    public TestCommand(FCBukkitPlugin plugin) {
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

    /**
     * 这是含有一个指定参数的子命令。
     * 值得注意的是，我们无需注册test根命令，该命令也能解析。
     */
    @CommandMethod("test c1")
    @CommandDescription("测试c1命令")
    public void test_c1(final @NonNull CommandSender sender) {
        sender.sendMessage("我是c1命令。");
    }

    /**
     * 这是基于test_c1命令的，这里的变化是在c1的基础上，多了一个子命令b1。
     */
    @CommandMethod("test c1 b1")
    @CommandDescription("测试c1命令")
    public void test_c1_b1(final @NonNull CommandSender sender) {
        sender.sendMessage("我是c1中的b1命令。");
    }

    /**
     * 这是需要拥有指定的权限才能使用的命令。
     * 值得注意的是，如果类顶部被指定了权限，那么执行这个命令的条件就是既满足类顶部的权限，也满足方法顶部注解的权限。
     */
    @CommandMethod("test c2")
    @CommandPermission("admin.test1")
    @CommandDescription("测试c2命令")
    public void test_c2(final @NonNull CommandSender sender) {
        sender.sendMessage("我是c2命令。");
    }

    /**
     * 这是命令参数的推荐内容，必须搭配@Argument注解使用，详情使用看test_c2_a1_qu函数。
     */
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

    /**
     * 这个命令，虽然没有指定参数推荐，但是它却指定了参数类型World。
     * 这就意味着，在指定参数类型的情况下，Cloud框架会智能识别这些参数。
     * 值得注意的是，并不是所有的参数类型都能被识别，可以在进一步的测试中进行测试。
     */
    @CommandMethod("test c3 a1 [query]")
    @CommandDescription("测试c3 a1命令")
    public void test_c3_a1_qu(final @NonNull CommandSender sender, final @Argument("query") @Greedy World query) {
        sender.sendMessage("我是c2中的a2命令，你选择的query是" + query);
    }

    @CommandMethod("test c3 a2 [query]")
    @CommandDescription("测试c3 a2命令")
    public void test_c3_a2_qu(final @NonNull CommandSender sender, final @Argument("query") @Greedy Player query) {
        sender.sendMessage("我是c2中的a2命令，你选择的query是" + query);
    }

    /**
     * 这是打开书本的命令测试。
     */
    @CommandMethod("test c4 a1")
    @CommandDescription("测试c4 a1命令")
    public void test_c4_a1(final @NonNull Player player) {
        Component bookTitle = Component.text("Encyclopedia of cats");
        Component bookAuthor = Component.text("kashike");
        Collection<Component> bookPages = new ArrayList<>();
        Component b1 = Component.text("哈哈哈我是测试c4 a1");
        bookPages.add(b1);

        Book myBook = Book.book(bookTitle, bookAuthor, bookPages);
        Audience target = getPlugin().getSenderFactory().getAudiences().player(player);
        target.openBook(myBook);
    }

    /**
     * 这是使玩家顶部召唤BoosBar的命令测试。
     *
     * @param player
     */
    @CommandMethod("test c4 a2")
    @CommandDescription("测试c4 a2命令")
    public void test_c4_a2(final @NonNull Player player) {
        final Component name = Component.text("Awesome BossBar");
        // Creates a red boss bar which has no progress and no notches
        final BossBar emptyBar = BossBar.bossBar(name, 0, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        // Creates a green boss bar which has 50% progress and 10 notches
        final BossBar halfBar = BossBar.bossBar(name, 0.5f, BossBar.Color.GREEN, BossBar.Overlay.NOTCHED_10);
        // etc..
        final BossBar fullBar = BossBar.bossBar(name, 1, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_20);

        Audience target = getPlugin().getSenderFactory().getAudiences().player(player);
        // Send a bossbar to your audience
        target.showBossBar(fullBar);
    }

    /**
     * 这是含有参数列表的参数例示。
     */
    @CommandMethod("test c5 a1 [query]")
    @CommandDescription("测试c5 a1命令")
    public void test_c5_a1_qu(final @NonNull CommandSender sender, final @Argument("query") String[] query) {
        sender.sendMessage("我是c2中的a2命令，你选择的query是" + Arrays.toString(query));
    }

    /**
     * 这是含有参数列表的参数例示。
     * 使用@Greedy同样能实现上述效果，在任何情况下，都推荐使用这种方式。
     */
    @CommandMethod("test c5 a2 [query]")
    @CommandDescription("测试c5 a2命令")
    public void test_c5_a2_qu(final @NonNull CommandSender sender, final @Argument("query") @Greedy String query) {
        sender.sendMessage("我是c2中的a2命令，你选择的query是" + query);
    }

    @CommandMethod("test lp a1")
    @CommandDescription("测试lp a1命令")
    public void luckPermsTest(final @NonNull Player player) {
        UUID uuid = player.getUniqueId();
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user != null) {
            String pPrefix = user.getCachedData().getMetaData().getPrefix();
            player.sendMessage(pPrefix);
        }
    }
}
