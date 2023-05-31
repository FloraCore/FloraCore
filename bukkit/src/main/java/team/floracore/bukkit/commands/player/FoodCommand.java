package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.common.sender.Sender;
import team.floracore.common.util.SenderUtil;

/**
 * Food命令
 */
@CommandPermission("floracore.command.food")
@CommandDescription("设置玩家的饥饿度")
public class FoodCommand extends FloraCoreBukkitCommand {
    public FoodCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("food|hunger get [target]")
    @CommandPermission("floracore.command.food.get")
    @CommandDescription("获取一名玩家的饥饿值")
    public void get(@NotNull CommandSender s, @Nullable @Argument("target") Player target) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (target == null) { // 目标为空,则目标为自己
            // 目标为自己时,发送者必须是玩家
            if (!(s instanceof Player)) { // 不是玩家
                SenderUtil.sendMustBe(sender, s.getClass(), Player.class); // 告知不予执行
                return;
            }
            Player player = (Player) s;
            PlayerCommandMessage.COMMAND_FOOD_GET_SELF_NUTRITION.send(sender, player.getFoodLevel()); // 返回饥饿值
            PlayerCommandMessage.COMMAND_FOOD_GET_SELF_SATURATION.send(sender, player.getSaturation()); // 返回饱和度
        } else { // 指定了目标玩家
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.food.get.other")) { // 玩家没有特定权限
                return;
            }
            PlayerCommandMessage.COMMAND_FOOD_GET_OTHER_NUTRITION.send(sender,
                                                                       target.getName(),
                                                                       target.getFoodLevel()); // 返回饥饿值
            PlayerCommandMessage.COMMAND_FOOD_GET_OTHER_SATURATION.send(sender,
                                                                        target.getName(),
                                                                        target.getSaturation()); // 返回饱和度
        }
    }

    @CommandMethod("food|hunger setnutrition <value> [target]")
    @CommandPermission("floracore.command.food.set.nutrition")
    @CommandDescription("设置一名玩家的饥饿值")
    public void setNutrition(@NotNull CommandSender s,
                             @Argument("value") int value,
                             @Nullable @Argument("target") Player target,
                             @Nullable @Flag("silent") Boolean silent) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (value < 0 || value > 20) {
            PlayerCommandMessage.COMMAND_FOOD_SET_INVALID_VALUE.send(sender);
            return;
        }
        if (target == null) { // 目标为空,则目标为自己
            // 目标为自己时,发送者必须是玩家
            if (!(s instanceof Player)) { // 不是玩家
                SenderUtil.sendMustBe(sender, s.getClass(), Player.class); // 告知不予执行
                return;
            }
            Player player = (Player) s;
            player.setFoodLevel(value); // 设置饥饿值
            PlayerCommandMessage.COMMAND_FOOD_SET_SELF_NUTRITION.send(sender, value); // 告知设置成功
        } else { // 指定了目标玩家
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.food.set.nutrition.other")) { // 发送者没有特定权限
                return;
            }
            target.setFoodLevel(value);
            PlayerCommandMessage.COMMAND_FOOD_SET_OTHER_NUTRITION.send(sender, target.getName(), value); // 告知设置成功
            if (silent == null || !silent) { // 非静音模式
                PlayerCommandMessage.COMMAND_FOOD_SET_FROM_NUTRITION.send(getPlugin().getSenderFactory().wrap(target),
                                                                          s.getName(),
                                                                          value); // 告知被设置
            }
        }
    }

    @CommandMethod("food|hunger setsaturation <value> [target]")
    @CommandPermission("floracore.command.food.set.saturation")
    @CommandDescription("设置一名玩家的饱和度")
    public void setSaturation(@NotNull CommandSender s,
                              @Argument("value") float value,
                              @Nullable @Argument("target") Player target,
                              @Nullable @Flag("silent") Boolean silent) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (value < 0.0F || value > 20.0F) {
            PlayerCommandMessage.COMMAND_FOOD_SET_INVALID_VALUE.send(sender);
            return;
        }
        if (target == null) { // 目标为空,则目标为自己
            // 目标为自己时,发送者必须是玩家
            if (!(s instanceof Player)) { // 不是玩家
                SenderUtil.sendMustBe(sender, s.getClass(), Player.class); // 告知不予执行
                return;
            }
            Player player = (Player) s;
            player.setSaturation(value); // 设置饱和度
            PlayerCommandMessage.COMMAND_FOOD_SET_SELF_SATURATION.send(sender, value); // 告知设置成功
        } else { // 指定了目标玩家
            if (SenderUtil.sendIfNoPermission(sender, "floracore.command.food.set.saturation.other")) { // 发送者没有特定权限
                return;
            }
            target.setSaturation(value);
            PlayerCommandMessage.COMMAND_FOOD_SET_OTHER_SATURATION.send(sender, target.getName(), value); // 告知设置成功
            if (silent == null || !silent) { // 非静音模式
                PlayerCommandMessage.COMMAND_FOOD_SET_FROM_SATURATION.send(getPlugin().getSenderFactory().wrap(target),
                                                                           s.getName(),
                                                                           value); // 告知被设置
            }
        }
    }
}
