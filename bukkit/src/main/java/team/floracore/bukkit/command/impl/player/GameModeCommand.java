package team.floracore.bukkit.command.impl.player;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.common.exception.NoSuchTypeException;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.sender.Sender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GameMode命令
 */
@CommandDescription("floracore.command.description.gamemode")
@CommandPermission("floracore.command.gamemode")
public class GameModeCommand extends FloraCoreBukkitCommand {
    public GameModeCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("gm|gamemode <mode> [target]")
    @CommandDescription("floracore.command.description.gamemode")
    public void gamemode(final @NotNull CommandSender s,
                         final @NotNull @Argument(value = "mode", suggestions = "gamemodes") String mode,
                         final @Nullable @Argument("target") Player target,
                         final @Nullable @Flag("silent") Boolean silent) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        GameMode gameMode;
        try {
            gameMode = parseGamemode(mode); // 解析游戏模式
        } catch (NoSuchTypeException ex) {
            PlayerCommandMessage.COMMAND_GAMEMODE_NOSUCH.send(sender, mode);
            return;
        }
        boolean own = target == null;
        boolean execute = true;
        Player t = null;
        if (own) {
            // 判断是不是玩家
            if (!sender.isConsole()) {
                t = (Player) s;
            } else {
                // 控制台时,将只允许设置别人的游戏模式。
                execute = false;
            }
        } else {
            t = target;
        }
        if (execute) {
            Sender ts = null;
            if (!own && (silent == null || !silent)) {
                ts = getPlugin().getSenderFactory().wrap(target);
            }
            switch (gameMode) {
                case SURVIVAL:
                    if (setGameModeIfPermissionOrSendMessage(sender,
                            GameMode.SURVIVAL,
                            t,
                            "floracore.command.gamemode.survival")) {
                        PlayerCommandMessage.COMMAND_GAMEMODE.send(sender,
                                MiscMessage.COMMAND_MISC_GAMEMODE_SURVIVAL.build(),
                                t.getDisplayName());
                        if (!own) {
                            if (silent == null || !silent) { // 若非静音模式,则发送消息
                                PlayerCommandMessage.COMMAND_GAMEMODE_FROM.send(ts,
                                        MiscMessage.COMMAND_MISC_GAMEMODE_SURVIVAL.build(),
                                        sender.getDisplayName());
                            }
                        }
                    }
                    break;
                case CREATIVE:
                    if (setGameModeIfPermissionOrSendMessage(sender,
                            GameMode.CREATIVE,
                            t,
                            "floracore.command.gamemode.creative")) {
                        PlayerCommandMessage.COMMAND_GAMEMODE.send(sender,
                                MiscMessage.COMMAND_MISC_GAMEMODE_CREATIVE.build(),
                                t.getDisplayName());
                        if (!own) {
                            if (silent == null || !silent) {
                                PlayerCommandMessage.COMMAND_GAMEMODE_FROM.send(ts,
                                        MiscMessage.COMMAND_MISC_GAMEMODE_CREATIVE.build(),
                                        sender.getDisplayName());
                            }
                        }
                    }
                    break;
                case ADVENTURE:
                    if (setGameModeIfPermissionOrSendMessage(sender,
                            GameMode.ADVENTURE,
                            t,
                            "floracore.command.gamemode.adventure")) {
                        PlayerCommandMessage.COMMAND_GAMEMODE.send(sender,
                                MiscMessage.COMMAND_MISC_GAMEMODE_ADVENTURE.build(),
                                t.getDisplayName());
                        if (!own) {
                            if (silent == null || !silent) {
                                PlayerCommandMessage.COMMAND_GAMEMODE_FROM.send(ts,
                                        MiscMessage.COMMAND_MISC_GAMEMODE_ADVENTURE.build(),
                                        sender.getDisplayName());
                            }
                        }
                    }
                    break;
                case SPECTATOR:
                    if (setGameModeIfPermissionOrSendMessage(sender,
                            GameMode.SPECTATOR,
                            t,
                            "floracore.command.gamemode.spectator")) {
                        PlayerCommandMessage.COMMAND_GAMEMODE.send(sender,
                                MiscMessage.COMMAND_MISC_GAMEMODE_SPECTATOR.build(),
                                t.getDisplayName());
                        if (!own) {
                            if (silent == null || !silent) {
                                PlayerCommandMessage.COMMAND_GAMEMODE_FROM.send(ts,
                                        MiscMessage.COMMAND_MISC_GAMEMODE_SPECTATOR.build(),
                                        sender.getDisplayName());
                            }
                        }
                    }
                    break;
            }
        } else {
            MiscMessage.COMMAND_INVALID_COMMAND_SENDER.send(sender,
                    CommandSender.class.getSimpleName(),
                    Player.class.getSimpleName());
        }
    }

    private @NotNull GameMode parseGamemode(@NotNull String text) throws NoSuchTypeException {
        switch (text.toLowerCase()) {
            case "0":
            case "survival":
            case "s":
                return GameMode.SURVIVAL;
            case "1":
            case "creative":
            case "c":
                return GameMode.CREATIVE;
            case "2":
            case "adventure":
            case "a":
                return GameMode.ADVENTURE;
            case "3":
            case "spectator":
            case "sp":
                return GameMode.SPECTATOR;
            default:
                throw new NoSuchTypeException(text);
        }
    }

    /**
     * 若`messageReceiver`拥有`permission`权限,将`target`的游戏模式设置为`gameMode`,否则发送没有权限消息给`messageReceiver`
     *
     * @param messageReceiver 消息接收者
     * @param gameMode        游戏模式
     * @param target          需要设置游戏模式的目标玩家
     * @param permission      权限节点
     * @return 是否拥有权限并成功设置
     */
    private boolean setGameModeIfPermissionOrSendMessage(@NotNull Sender messageReceiver,
                                                         @NotNull GameMode gameMode,
                                                         @NotNull Player target,
                                                         @NotNull String permission) {
        if (messageReceiver.hasPermission(permission)) {
            target.setGameMode(gameMode);
            return true;
        } else {
            MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(messageReceiver);
            return false;
        }
    }

    @Suggestions("gamemodes")
    public List<String> getGameModes(final @NotNull CommandContext<CommandSender> sender,
                                     final @NotNull String input) {
        return new ArrayList<>(Arrays.asList("survival", "creative", "adventure", "spectator"));
    }
}
