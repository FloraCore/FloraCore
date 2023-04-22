package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.exception.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

import java.util.*;

public class GameModeCommand extends AbstractFloraCoreCommand {
    public GameModeCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("gm|gamemode <mode> [target]")
    @CommandPermission("floracore.command.gamemode")
    @CommandDescription("设置游戏模式")
    public void gamemode(final @NotNull CommandSender s, final @NotNull @Argument(value = "mode", suggestions = "gamemodes") String mode, final @Nullable @Argument("target") Player target, final @Nullable @Flag("silent") Boolean silent) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        GameMode gameMode;
        try {
            gameMode = parseGamemode(mode); // 解析游戏模式
        } catch (NoSuchGameModeException ex) {
            Message.COMMAND_GAMEMODE_NOSUCH.send(sender, mode);
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
                // 控制台时，将只允许设置别人的游戏模式。
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
                    if (setGameModeIfPermissionOrSenMessage(sender, GameMode.SURVIVAL, t, "floracore.command.gamemode.creative")) {
                        Message.COMMAND_GAMEMODE.send(sender, Message.COMMAND_MISC_GAMEMODE_SURVIVAL.build(), t.getDisplayName());
                        if (!own) {
                            if (silent == null || !silent) { // 若非静音模式，则发送消息
                                Message.COMMAND_GAMEMODE_FROM.send(ts, Message.COMMAND_MISC_GAMEMODE_SURVIVAL.build(), sender.getDisplayName());
                            }
                        }
                    }
                    break;
                case CREATIVE:
                    if (setGameModeIfPermissionOrSenMessage(sender, GameMode.CREATIVE, t, "floracore.command.gamemode.creative")) {
                        Message.COMMAND_GAMEMODE.send(sender, Message.COMMAND_MISC_GAMEMODE_CREATIVE.build(), t.getDisplayName());
                        if (!own) {
                            if (silent == null || !silent) {
                                Message.COMMAND_GAMEMODE_FROM.send(ts, Message.COMMAND_MISC_GAMEMODE_CREATIVE.build(), sender.getDisplayName());
                            }
                        }
                    }
                    break;
                case ADVENTURE:
                    if (setGameModeIfPermissionOrSenMessage(sender, GameMode.ADVENTURE, t, "floracore.command.gamemode.adventure")) {
                        Message.COMMAND_GAMEMODE.send(sender, Message.COMMAND_MISC_GAMEMODE_ADVENTURE.build(), t.getDisplayName());
                        if (!own) {
                            if (silent == null || !silent) {
                                Message.COMMAND_GAMEMODE_FROM.send(ts, Message.COMMAND_MISC_GAMEMODE_ADVENTURE.build(), sender.getDisplayName());
                            }
                        }
                    }
                    break;
                case SPECTATOR:
                    if (setGameModeIfPermissionOrSenMessage(sender, GameMode.SPECTATOR, t, "floracore.command.gamemode.spectator")) {
                        Message.COMMAND_GAMEMODE.send(sender, Message.COMMAND_MISC_GAMEMODE_SPECTATOR.build(), t.getDisplayName());
                        if (!own) {
                            if (silent == null || !silent) {
                                Message.COMMAND_GAMEMODE_FROM.send(ts, Message.COMMAND_MISC_GAMEMODE_SPECTATOR.build(), sender.getDisplayName());
                            }
                        }
                    }
                    break;
            }
        } else {
            Message.COMMAND_INVALID_COMMAND_SENDER.send(sender, CommandSender.class.getSimpleName(), Player.class.getSimpleName());
        }
    }

    /**
     * 若`messageReceiver`拥有`permission`权限，将`target`的游戏模式设置为`gameMode`，否则发送没有权限消息给`messageReceiver`
     *
     * @param messageReceiver 消息接收者
     * @param gameMode        游戏模式
     * @param target          需要设置游戏模式的目标玩家
     * @param permission      权限节点
     * @return 是否拥有权限并成功设置
     */
    private boolean setGameModeIfPermissionOrSenMessage(@NotNull Sender messageReceiver, @NotNull GameMode gameMode, @NotNull Player target, @NotNull String permission) {
        if (messageReceiver.hasPermission(permission)) {
            target.setGameMode(gameMode);
            return true;
        } else {
            Message.COMMAND_NO_PERMISSION.send(messageReceiver);
            return false;
        }
    }

    @Suggestions("gamemodes")
    public List<String> getGameModes(final @NotNull CommandContext<CommandSender> sender, final @NotNull String input) {
        return new ArrayList<>(Arrays.asList("survival", "creative", "adventure", "spectator"));
    }

    private @NotNull GameMode parseGamemode(@NotNull String text) throws NoSuchGameModeException {
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
                throw new NoSuchGameModeException(text);
        }
    }
}
