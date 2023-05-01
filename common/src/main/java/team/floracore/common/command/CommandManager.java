package team.floracore.common.command;

import cloud.commandframework.*;
import cloud.commandframework.annotations.*;
import cloud.commandframework.arguments.parser.*;
import cloud.commandframework.bukkit.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.meta.*;
import cloud.commandframework.paper.*;
import org.bukkit.command.*;
import team.floracore.common.commands.misc.FloraCoreCommand;
import team.floracore.common.commands.player.*;
import team.floracore.common.commands.server.*;
import team.floracore.common.commands.test.*;
import team.floracore.common.commands.world.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

import java.util.function.*;

/**
 * 命令管理器。
 */
public class CommandManager {
    private final FloraCorePlugin plugin;
    private final AnnotationParser<CommandSender> annotationParser;
    private BukkitCommandManager<CommandSender> manager;

    public CommandManager(FloraCorePlugin plugin) {
        this.plugin = plugin;
        // 异步
        // final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction = AsynchronousCommandExecutionCoordinator.<CommandSender>builder().build();
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction = CommandExecutionCoordinator.simpleCoordinator();
        final Function<CommandSender, CommandSender> mapperFunction = Function.identity();
        try {
            this.manager = new PaperCommandManager<>(
                    /* Owning plugin */ plugin.getBootstrap().getPlugin(),
                    /* Coordinator function */ executionCoordinatorFunction,
                    /* Command Sender -> C */ mapperFunction,
                    /* C -> Command Sender */ mapperFunction);
        } catch (final Exception e) {
            plugin.getLogger().severe("Failed to initialize the command this.manager");
            /* Disable the plugin */
            plugin.getBootstrap().getServer().getPluginManager().disablePlugin(plugin.getBootstrap().getPlugin());
        }
        // Use contains to filter suggestions instead of default startsWith
        this.manager.commandSuggestionProcessor(new FilteringCommandSuggestionProcessor<>(FilteringCommandSuggestionProcessor.Filter.<CommandSender>contains(true).andTrimBeforeLastSpace()));
        // Register Brigadier mappings
        //
        if (this.manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.manager.registerBrigadier();
        }
        // Register asynchronous completions
        if (this.manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            ((PaperCommandManager<CommandSender>) this.manager).registerAsynchronousCompletions();
        }
        // Create the annotation parser. This allows you to define commands using methods annotated with
        // @CommandMethod
        final Function<ParserParameters, CommandMeta> commandMetaFunction = p -> CommandMeta.simple()
                // This will allow you to decorate commands with descriptions
                .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "无描述")).build();
        this.annotationParser = new AnnotationParser<>(
                /* Manager */ this.manager,
                /* Command sender type */ CommandSender.class,
                /* Mapper for command meta instances */ commandMetaFunction);

        // 命令语法错误自定义
        this.manager.registerExceptionHandler(InvalidSyntaxException.class, (context, exception) -> {
            Sender sender = plugin.getSenderFactory().wrap(context);
            Message.COMMAND_INVALID_COMMAND_SYNTAX.send(sender, "/" + exception.getCorrectSyntax());
        });

        // 无权限
        this.manager.registerExceptionHandler(NoPermissionException.class, (context, exception) -> {
            Sender sender = plugin.getSenderFactory().wrap(context);
            Message.COMMAND_NO_PERMISSION.send(sender);
        });

        // 类型错误
        this.manager.registerExceptionHandler(InvalidCommandSenderException.class, (context, exception) -> {
            Sender sender = plugin.getSenderFactory().wrap(context);
            Message.COMMAND_INVALID_COMMAND_SENDER.send(sender, context.getClass().getSimpleName(), exception.getRequiredSender().getSimpleName());
        });

        // Create the commands
        this.constructCommands();
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    public BukkitCommandManager<CommandSender> getManager() {
        return manager;
    }

    public AnnotationParser<CommandSender> getAnnotationParser() {
        return annotationParser;
    }

    public void constructCommands() {
        // test
        this.annotationParser.parse(new TestCommand(plugin));

        // misc
        this.annotationParser.parse(new FloraCoreCommand(plugin));

        // player
        this.annotationParser.parse(new AirCommand(plugin));
        this.annotationParser.parse(new EnderChestCommand(plugin)); // TODO 命令异常 命令事件逻辑异常 与FireTick命令冲突
        this.annotationParser.parse(new FeedCommand(plugin)); // TODO 未测试命令
        this.annotationParser.parse(new FireTickCommand(plugin)); // TODO 未测试命令 可以使用，但是经过测试发现，会在火焰消失后发送 floracore.command.enderchest.readonly.from
        this.annotationParser.parse(new FlyCommand(plugin));
        this.annotationParser.parse(new FoodCommand(plugin)); // TODO 未测试命令
        this.annotationParser.parse(new GameModeCommand(plugin));
        this.annotationParser.parse(new GiveCommand(plugin));
        this.annotationParser.parse(new HasPermissionCommand(plugin));
        this.annotationParser.parse(new HatCommand(plugin));
        this.annotationParser.parse(new HealCommand(plugin)); // TODO 命令异常 java.lang.NoSuchMethodException: org.bukkit.entity.Player.getMaxHealth()
        this.annotationParser.parse(new InvSeeCommand(plugin));
        this.annotationParser.parse(new MaxHealthCommand(plugin)); // TODO 待测试命令
        this.annotationParser.parse(new NickCommand(plugin)); // TODO 命令测试中 未测试：Rank设置 未完善：Skin设置
        this.annotationParser.parse(new OPListCommand(plugin));
        this.annotationParser.parse(new PingCommand(plugin));
        this.annotationParser.parse(new RealNameCommand(plugin));
        this.annotationParser.parse(new SpeedCommand(plugin)); // TODO 命令异常
        this.annotationParser.parse(new SuicideCommand(plugin));
        this.annotationParser.parse(new TopCommand(plugin));

        // server
        this.annotationParser.parse(new BroadCastCommand(plugin));

        // world
        this.annotationParser.parse(new TimeCommand(plugin));
        this.annotationParser.parse(new WeatherCommand(plugin));
    }
}
