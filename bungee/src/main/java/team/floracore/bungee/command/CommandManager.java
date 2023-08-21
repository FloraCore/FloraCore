package team.floracore.bungee.command;

import cloud.commandframework.CommandTree;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bungee.BungeeCommandManager;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.execution.FilteringCommandSuggestionProcessor;
import cloud.commandframework.meta.CommandMeta;
import net.md_5.bungee.api.CommandSender;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.command.impl.misc.FloraCoreCommand;
import team.floracore.bungee.command.impl.socialsystems.AdminCommand;
import team.floracore.bungee.command.impl.socialsystems.BuilderCommand;
import team.floracore.bungee.command.impl.socialsystems.ChatCommand;
import team.floracore.bungee.command.impl.socialsystems.FriendCommand;
import team.floracore.bungee.command.impl.socialsystems.GuildCommand;
import team.floracore.bungee.command.impl.socialsystems.PartyCommand;
import team.floracore.bungee.command.impl.socialsystems.StaffCommand;
import team.floracore.bungee.command.impl.test.TestCommand;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.sender.Sender;

import java.util.function.Function;

/**
 * 命令管理器。
 */
public class CommandManager {
    private final FCBungeePlugin plugin;
    private final AnnotationParser<CommandSender> annotationParser;
    private BungeeCommandManager<CommandSender> manager;

    public CommandManager(FCBungeePlugin plugin) {
        this.plugin = plugin;
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction = CommandExecutionCoordinator.simpleCoordinator();
        final Function<CommandSender, CommandSender> mapperFunction = Function.identity();
        try {
            this.manager = new BungeeCommandManager<>(
                    plugin.getBootstrap().getLoader(),
                    executionCoordinatorFunction,
                    mapperFunction,
                    mapperFunction);
        } catch (final Exception e) {
            plugin.getLogger().severe("Failed to initialize the command this.manager");
            plugin.getProxy().stop();
        }
        // Use contains to filter suggestions instead of default startsWith
        this.manager.commandSuggestionProcessor(new FilteringCommandSuggestionProcessor<>(
                FilteringCommandSuggestionProcessor.Filter.<CommandSender>contains(true).andTrimBeforeLastSpace()));

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
        // misc
        this.annotationParser.parse(new FloraCoreCommand(plugin));

        // social systems
        this.annotationParser.parse(new AdminCommand(plugin));
        this.annotationParser.parse(new BuilderCommand(plugin));
        this.annotationParser.parse(new ChatCommand(plugin));
        this.annotationParser.parse(new FriendCommand(plugin));
        this.annotationParser.parse(new GuildCommand(plugin));
        this.annotationParser.parse(new PartyCommand(plugin));
        this.annotationParser.parse(new StaffCommand(plugin));

        // test
        this.annotationParser.parse(new TestCommand(plugin));
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    public BungeeCommandManager<CommandSender> getManager() {
        return manager;
    }

    public AnnotationParser<CommandSender> getAnnotationParser() {
        return annotationParser;
    }
}
