package team.floracore.bukkit.command;

import cloud.commandframework.CommandTree;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.execution.FilteringCommandSuggestionProcessor;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.impl.misc.FloraCoreCommand;
import team.floracore.bukkit.command.impl.misc.LanguageCommand;
import team.floracore.bukkit.command.impl.player.AirCommand;
import team.floracore.bukkit.command.impl.player.EnderChestCommand;
import team.floracore.bukkit.command.impl.player.FeedCommand;
import team.floracore.bukkit.command.impl.player.FireTickCommand;
import team.floracore.bukkit.command.impl.player.FlyCommand;
import team.floracore.bukkit.command.impl.player.FoodCommand;
import team.floracore.bukkit.command.impl.player.GameModeCommand;
import team.floracore.bukkit.command.impl.player.HasPermissionCommand;
import team.floracore.bukkit.command.impl.player.HatCommand;
import team.floracore.bukkit.command.impl.player.HealCommand;
import team.floracore.bukkit.command.impl.player.InvSeeCommand;
import team.floracore.bukkit.command.impl.player.OPListCommand;
import team.floracore.bukkit.command.impl.player.PingCommand;
import team.floracore.bukkit.command.impl.player.RealNameCommand;
import team.floracore.bukkit.command.impl.player.SpeedCommand;
import team.floracore.bukkit.command.impl.player.SuicideCommand;
import team.floracore.bukkit.command.impl.player.teleport.TeleportCommand;
import team.floracore.bukkit.command.impl.player.teleport.TopCommand;
import team.floracore.bukkit.command.impl.server.BroadCastCommand;
import team.floracore.bukkit.command.impl.server.BungeeCommand;
import team.floracore.bukkit.command.impl.server.KickFromBungeeCommand;
import team.floracore.bukkit.command.impl.test.TestCommand;
import team.floracore.bukkit.command.impl.world.TimeCommand;
import team.floracore.bukkit.command.impl.world.WeatherCommand;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.sender.Sender;

import java.util.function.Function;

/**
 * 命令管理器。
 */
public class CommandManager {
    private final FCBukkitPlugin plugin;
    private final AnnotationParser<CommandSender> annotationParser;
    private BukkitCommandManager<CommandSender> manager;

    public CommandManager(FCBukkitPlugin plugin) {
        this.plugin = plugin;
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction = CommandExecutionCoordinator.simpleCoordinator();
        final Function<CommandSender, CommandSender> mapperFunction = Function.identity();
        try {
            this.manager = new PaperCommandManager<>(
                    plugin.getBootstrap().getLoader(),
                    executionCoordinatorFunction,
                    mapperFunction,
                    mapperFunction);
        } catch (final Exception e) {
            plugin.getLogger().severe("Failed to initialize the command this.manager");
            Bukkit.getServer().getPluginManager().disablePlugin(plugin.getBootstrap().getLoader());
        }
        // Use contains to filter suggestions instead of default startsWith
        this.manager.commandSuggestionProcessor(new FilteringCommandSuggestionProcessor<>(
                FilteringCommandSuggestionProcessor.Filter.<CommandSender>contains(true).andTrimBeforeLastSpace()));
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
                // This will allow you to
                // decorate commands with
                // descriptions
                .with(CommandMeta.DESCRIPTION,
                        p.get(StandardParameters.DESCRIPTION,
                                "NONE"))
                .build();
        this.annotationParser = new AnnotationParser<>(
                this.manager,
                CommandSender.class,
                commandMetaFunction);

        // 命令语法错误自定义
        this.manager.registerExceptionHandler(InvalidSyntaxException.class, (context, exception) -> {
            Sender sender = plugin.getSenderFactory().wrap(context);
            MiscMessage.COMMAND_INVALID_COMMAND_SYNTAX.send(sender, "/" + exception.getCorrectSyntax());
        });

        // 无权限
        this.manager.registerExceptionHandler(NoPermissionException.class, (context, exception) -> {
            Sender sender = plugin.getSenderFactory().wrap(context);
            MiscMessage.COMMAND_NO_PERMISSION.send(sender);
        });

        // 类型错误
        this.manager.registerExceptionHandler(InvalidCommandSenderException.class, (context, exception) -> {
            Sender sender = plugin.getSenderFactory().wrap(context);
            MiscMessage.COMMAND_INVALID_COMMAND_SENDER.send(sender,
                    context.getClass().getSimpleName(),
                    exception.getRequiredSender().getSimpleName());
        });

        // Create the commands
        this.constructCommands();
    }

    public void constructCommands() {
        // test
        this.annotationParser.parse(new TestCommand(plugin));

        // misc
        this.annotationParser.parse(new FloraCoreCommand(plugin));
        this.annotationParser.parse(new LanguageCommand(plugin));

        // player
        // teleport
        this.annotationParser.parse(new TeleportCommand(plugin));
        this.annotationParser.parse(new AirCommand(plugin));
        this.annotationParser.parse(new EnderChestCommand(plugin));
        this.annotationParser.parse(new FeedCommand(plugin));
        this.annotationParser.parse(new FireTickCommand(plugin));
        this.annotationParser.parse(new FlyCommand(plugin));
        this.annotationParser.parse(new FoodCommand(plugin));
        this.annotationParser.parse(new GameModeCommand(plugin));
        // this.annotationParser.parse(new GiveCommand(plugin));
        this.annotationParser.parse(new HasPermissionCommand(plugin));
        this.annotationParser.parse(new HatCommand(plugin));
        this.annotationParser.parse(new HealCommand(plugin));
        this.annotationParser.parse(new InvSeeCommand(plugin));
        // this.annotationParser.parse(new MaxHealthCommand(plugin));

        // (目前已经将Rank设置和Skin设置移除)
        this.annotationParser.parse(new OPListCommand(plugin));
        this.annotationParser.parse(new PingCommand(plugin));
        this.annotationParser.parse(new RealNameCommand(plugin));
        // this.annotationParser.parse(new ReportCommand(plugin));
        this.annotationParser.parse(new SpeedCommand(plugin));
        this.annotationParser.parse(new SuicideCommand(plugin));
        this.annotationParser.parse(new TopCommand(plugin));

        // server
        this.annotationParser.parse(new BroadCastCommand(plugin));
        this.annotationParser.parse(new BungeeCommand(plugin));
        this.annotationParser.parse(new KickFromBungeeCommand(plugin));

        // world
        this.annotationParser.parse(new TimeCommand(plugin));
        this.annotationParser.parse(new WeatherCommand(plugin));

        // item
        // this.annotationParser.parse(new ItemFlagCommand(plugin)); // TODO 未测试
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
}
