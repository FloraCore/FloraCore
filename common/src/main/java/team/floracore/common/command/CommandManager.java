package team.floracore.common.command;

import cloud.commandframework.*;
import cloud.commandframework.annotations.*;
import cloud.commandframework.arguments.parser.*;
import cloud.commandframework.bukkit.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.meta.*;
import cloud.commandframework.paper.*;
import org.bukkit.command.*;
import team.floracore.common.commands.test.*;
import team.floracore.common.plugin.*;

import java.util.function.*;

public class CommandManager {
    private final FloraCorePlugin plugin;
    private final AnnotationParser<CommandSender> annotationParser;
    private BukkitCommandManager<CommandSender> manager;

    public CommandManager(FloraCorePlugin plugin) {
        this.plugin = plugin;
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction = AsynchronousCommandExecutionCoordinator.<CommandSender>builder().build();
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
        /*this.manager.registerExceptionHandler(
                InvalidSyntaxException.class,
                (context, exception) -> context.sendMessage("你可能: " + exception.getCorrectSyntax())
        );*/

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
        this.annotationParser.parse(new TestCommand(plugin));
    }
}
