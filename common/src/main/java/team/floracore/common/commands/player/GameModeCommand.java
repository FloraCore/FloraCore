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

    @CommandMethod("gm|gamemode <mode>")
    public void self(@NotNull Player s, @NotNull @Argument(value = "mode", suggestions = "gamemodes") String mode) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        GameMode gameMode;
        try {
            gameMode = parseGamemode(mode); // 解析游戏模式
        } catch (NoSuchGameModeException ex) {
            Message.COMMAND_GAMEMODE_NOSUCH.send(sender, mode);
            return;
        }
        switch (gameMode) {
            case SURVIVAL:
                if (setGameModeIfPermissionOrSenMessage(sender, GameMode.SURVIVAL, s, "floracore.command.gamemode.creative")) {
                    Message.COMMAND_GAMEMODE_SELF.send(sender, Message.COMMAND_MISC_GAMEMODE_SURVIVAL.build());
                }
                break;
            case CREATIVE:
                if (setGameModeIfPermissionOrSenMessage(sender, GameMode.CREATIVE, s, "floracore.command.gamemode.creative")) {
                    Message.COMMAND_GAMEMODE_SELF.send(sender, Message.COMMAND_MISC_GAMEMODE_CREATIVE.build());
                }
                break;
            case ADVENTURE:
                if (setGameModeIfPermissionOrSenMessage(sender, GameMode.ADVENTURE, s, "floracore.command.gamemode.adventure")) {
                    Message.COMMAND_GAMEMODE_SELF.send(sender, Message.COMMAND_MISC_GAMEMODE_ADVENTURE.build());
                }
                break;
            case SPECTATOR:
                if (setGameModeIfPermissionOrSenMessage(sender, GameMode.SPECTATOR, s, "floracore.command.gamemode.spectator")) {
                    Message.COMMAND_GAMEMODE_SELF.send(sender, Message.COMMAND_MISC_GAMEMODE_SPECTATOR.build());
                }
                break;
        }
    }

    @CommandMethod("gm|gamemode <target> <mode> [silent]")
    public void other(
            @NotNull Player s,
            @NotNull @Argument("target") Player target,
            @NotNull @Argument(value = "mode", suggestions = "gamemodes") String mode,
            @Nullable @Argument("silent") Boolean silent
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        GameMode gameMode;
        try {
            gameMode = parseGamemode(mode); // 解析游戏模式
        } catch (NoSuchGameModeException ex) {
            Message.COMMAND_GAMEMODE_NOSUCH.send(sender, mode);
            return;
        }
        switch (gameMode) { // 解析游戏模式
            case SURVIVAL:
                if (setGameModeIfPermissionOrSenMessage(sender, GameMode.SURVIVAL, target, "floracore.command.gamemode.other.creative")) {
                    Message.COMMAND_GAMEMODE_OTHER.send(sender, target.getName(), Message.COMMAND_MISC_GAMEMODE_SURVIVAL.build());
                    if (silent == null || !silent) { // 若非静音模式，则发送消息
                        Message.COMMAND_GAMEMODE_FROM.send(sender, s.getName(), Message.COMMAND_MISC_GAMEMODE_SURVIVAL.build());
                    }
                }
                break;
            case CREATIVE:
                if (setGameModeIfPermissionOrSenMessage(sender, GameMode.CREATIVE, target, "floracore.command.gamemode.other.creative")) {
                    Message.COMMAND_GAMEMODE_OTHER.send(sender, target.getName(), Message.COMMAND_MISC_GAMEMODE_CREATIVE.build());
                    if (silent == null || !silent) { // 若非静音模式，则发送消息
                        Message.COMMAND_GAMEMODE_FROM.send(sender, s.getName(), Message.COMMAND_MISC_GAMEMODE_CREATIVE.build());
                    }
                }
                break;
            case ADVENTURE:
                if (setGameModeIfPermissionOrSenMessage(sender, GameMode.ADVENTURE, target, "floracore.command.gamemode.other.adventure")) {
                    Message.COMMAND_GAMEMODE_OTHER.send(sender, target.getName(), Message.COMMAND_MISC_GAMEMODE_ADVENTURE.build());
                    if (silent == null || !silent) { // 若非静音模式，则发送消息
                        Message.COMMAND_GAMEMODE_FROM.send(sender, s.getName(), Message.COMMAND_MISC_GAMEMODE_ADVENTURE.build());
                    }
                }
                break;
            case SPECTATOR:
                if (setGameModeIfPermissionOrSenMessage(sender, GameMode.SPECTATOR, target, "floracore.command.gamemode.other.spectator")) {
                    Message.COMMAND_GAMEMODE_OTHER.send(sender, target.getName(), Message.COMMAND_MISC_GAMEMODE_SPECTATOR.build());
                    if (silent == null || !silent) { // 若非静音模式，则发送消息
                        Message.COMMAND_GAMEMODE_FROM.send(sender, s.getName(), Message.COMMAND_MISC_GAMEMODE_SPECTATOR.build());
                    }
                }
                break;
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
    private boolean setGameModeIfPermissionOrSenMessage(
            @NotNull Sender messageReceiver,
            @NotNull GameMode gameMode,
            @NotNull Player target,
            @NotNull String permission
    ) {
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
        return new ArrayList<>(Arrays.asList("0", "survival", "s", "1", "creative", "c", "2", "adventure", "a", "3", "spectator", "sp"));
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
