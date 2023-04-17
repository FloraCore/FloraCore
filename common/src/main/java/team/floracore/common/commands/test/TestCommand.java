package team.floracore.common.commands.test;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import org.bukkit.command.*;
import team.floracore.common.plugin.*;

@CommandContainer
public class TestCommand {
    private final FloraCorePlugin plugin;

    public TestCommand(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * This one gets parsed automatically!
     *
     * @param sender the sender
     */
    @CommandMethod("container")
    public void containerCommand(final CommandSender sender) {
        sender.sendMessage("This is sent from a container!!");
    }
}
