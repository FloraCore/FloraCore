package team.floracore.bukkit.locale.message.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import team.floracore.common.locale.message.AbstractMessage;
import team.floracore.common.locale.message.MiscMessage;

import java.util.UUID;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;

public interface PlayerCommandMessage extends AbstractMessage {
    Args2<Boolean, String> COMMAND_FLY = (status, target) -> AbstractMessage.prefixed(translatable()
            // {1} 的飞行模式被设置为 {0}
            .key("floracore.command.fly")
            .color(AQUA)
            // 开 / 关
            .args(translatable(status ? "floracore.command.misc.on" : "floracore.command.misc.off")
                            .color(status ? GREEN : RED),
                    text(target).color(RED))
            .append(FULL_STOP));

    Args2<Boolean, String> COMMAND_FLY_FROM = (status, from) -> AbstractMessage.prefixed(translatable()
            // {1} 将您的游戏模式设置为 {0}
            .key("floracore.command.fly.from")
            .color(AQUA)
            // 开 / 关
            .args(translatable(status ? "floracore.command.misc.on" : "floracore.command.misc.off")
                            .color(GREEN),
                    text(from).color(GREEN))
            .append(FULL_STOP));
    Args2<Component, String> COMMAND_GAMEMODE = (mode, target) -> AbstractMessage.prefixed(translatable()
            // 将{1}的游戏模式设置为{0}
            .key("floracore.command.gamemode")
            .color(AQUA)
            .args(mode.color(GREEN),
                    text(target).color(GREEN))
            .append(FULL_STOP));

    Args2<Component, String> COMMAND_GAMEMODE_FROM = (mode, from) -> AbstractMessage.prefixed(translatable()
            // {1} 将您的游戏模式设置为 {0}
            .key("floracore.command.gamemode.from")
            .color(AQUA)
            .args(mode.color(GREEN),
                    text(from).color(GREEN))
            .append(FULL_STOP));

    Args1<String> COMMAND_GAMEMODE_NOSUCH = (mode) -> AbstractMessage.prefixed(translatable()
            // {0} 不是合法的游戏模式
            .key("floracore.command.gamemode.nosuch")
            .color(RED)
            .args(text(mode).color(GREEN))
            .append(FULL_STOP));
    Args0 COMMAND_HAT_ARMOR = () -> AbstractMessage.prefixed(translatable()
            // 你无法将这个物品当做帽子戴上!
            .key("floracore.command.hat.armor").color(RED));

    Args0 COMMAND_HAT_CURSE = () -> AbstractMessage.prefixed(translatable()
            // 你不能移除带有绑定诅咒的帽子!
            .key("floracore.command.hat.curse").color(RED));

    Args0 COMMAND_HAT_EMPTY = () -> AbstractMessage.prefixed(translatable()
            // 你现在没有戴帽子!
            .key("floracore.command.hat.empty").color(RED));

    Args0 COMMAND_HAT_FAIL = () -> AbstractMessage.prefixed(translatable()
            // 你必须把想要戴的帽子拿在手中!
            .key("floracore.command.hat.fail").color(RED));

    Args0 COMMAND_HAT_REMOVED = () -> AbstractMessage.prefixed(translatable()
            // 你的帽子已被移除
            .key("floracore.command.hat.removed")
            .color(AQUA)
            .append(FULL_STOP));

    Args0 COMMAND_HAT_PLACED = () -> AbstractMessage.prefixed(translatable()
            // 你戴上了新帽子
            .key("floracore.command.hat.placed")
            .color(AQUA)
            .append(FULL_STOP));

    Args0 COMMAND_INVSEE_SELF = () -> AbstractMessage.prefixed(translatable()
            // 你只能查看其他玩家的物品栏!
            .key("floracore.command.invsee.self")
            .color(RED));

    Args1<String> COMMAND_INVSEE = (target) -> AbstractMessage.prefixed(translatable()
            // 你打开了 {0} 的物品栏
            .key("floracore.command.invsee")
            .color(AQUA)
            .args(text(target).color(GREEN))
            .append(FULL_STOP));

    Args0 COMMAND_TELEPORT_TOP = () -> AbstractMessage.prefixed(translatable()
            // 已传送到顶部
            .key("floracore.command.teleport.top")
            .color(AQUA)).append(FULL_STOP);

    Args1<Component> COMMAND_NICK_SETUP_SKIN = (skin) -> AbstractMessage.prefixed(translatable()
            // 你的皮肤已设置为 {0} !
            .key("floracore.command.nick.setup.skin")
            // {}
            .args(skin.color(GREEN))
            .color(AQUA));

    Args0 COMMAND_UNNICK_SUCCESS = () -> AbstractMessage.prefixed(translatable()
            // 你的昵称已移除！
            .key("floracore.command.unnick.success")
            .color(AQUA));

    Args0 COMMAND_UNNICK_NOT_IN = () -> AbstractMessage.prefixed(translatable()
            // 你当前未处于昵称状态！
            .key("floracore.command.unnick.not-in")
            .color(RED));

    Args0 COMMAND_MISC_NICK_ALREADY_NICKED = () -> translatable()
            // 你已经修改了昵称,若要继续使用该功能,请输入{0}以恢复到平常状态
            .key("floracore.command.misc.nick.already-nicked")
            // {0}
            .args(text("/nick reset", DARK_RED).decoration(BOLD, true)).append(FULL_STOP).color(RED).build();

    Args1<Integer> COMMAND_AIR_GET_SELF_MAX = value -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.air.get.self.max").color(AQUA).args(text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_GET_SELF_REMAINING = value -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.air.get.self.remaining").color(AQUA).args(text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_GET_OTHER_MAX = (target, value) -> AbstractMessage.prefixed(translatable().key(
                    "floracore.command.air.get.other.max")
            .color(AQUA)
            .args(text(target).color(GREEN),
                    text(value))
            .color(GREEN));

    Args2<String, Integer> COMMAND_AIR_GET_OTHER_REMAINING =
            (target, value) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.air.get.other" +
                                    ".remaining")
                    .color(AQUA)
                    .args(text(target).color(GREEN),
                            text(value))
                    .color(GREEN));

    Args1<Integer> COMMAND_AIR_SET_SELF_MAX = value -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.air.set.self.max").color(AQUA).args(text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_SET_SELF_REMAINING = value -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.air.set.self.remaining").color(AQUA).args(text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_OTHER_MAX = (target, value) -> AbstractMessage.prefixed(translatable().key(
                    "floracore.command.air.set.other.max")
            .color(AQUA)
            .args(text(target).color(GREEN),
                    text(value))
            .color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_OTHER_REMAINING =
            (target, value) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.air.set.other" +
                                    ".remaining")
                    .color(AQUA)
                    .args(text(target).color(GREEN),
                            text(value))
                    .color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_FROM_MAX = (from, value) -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.air.set.from.max").color(AQUA).args(text(from).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_FROM_REMAINING =
            (from, value) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.air.set.from.remaining")
                    .color(AQUA)
                    .args(text(from).color(GREEN),
                            text(value))
                    .color(GREEN));

    Args0 COMMAND_MISC_NICK_ACTION_BAR = () -> translatable()
            // 当前已 {0}
            .key("floracore.command.misc.nick.action-bar").color(WHITE)
            // 匿名
            .args(translatable("floracore.command.misc.nick.action-bar.nick").color(RED)).build();

    Args1<String> COMMAND_MISC_NICK_RANK_UNKNOWN = (rank) -> AbstractMessage.prefixed(translatable()
            // {0} {1} 不存在!
            .key("floracore.command.misc.nick.rank.unknown")
            // {0}
            .args(translatable(
                            "floracore.command.misc.nick.book.rank-page.rank"),
                    text(rank).color(DARK_RED)
                            .decoration(BOLD, true))
            .color(RED));

    Args1<String> COMMAND_MISC_NICK_RANK_NO_PERMISSION = (rank) -> AbstractMessage.prefixed(translatable()
            // 你没有 {0} {1} 的使用权限!
            .key("floracore.command.misc.nick.rank.no-permission")
            // {0}
            .args(translatable(
                            "floracore.command.misc.nick.book.rank-page.rank"),
                    text(rank).color(DARK_RED)
                            .decoration(BOLD, true))
            .color(RED));

    Args2<String, String> COMMAND_REALNAME_SUCCESS = (name, realName) -> AbstractMessage.prefixed(translatable()
            // 玩家 {0} 的真实昵称为 {1}
            .key("floracore.command.realname.success")
            // {}
            .args(text(name).color(GREEN),
                    text(realName).color(GREEN))
            .color(AQUA));

    Args1<Integer> COMMAND_AIR_GET_MAX_SELF = value -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.air.get.max.self").color(AQUA).args(text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_GET_REMAINING_SELF = value -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.air.get.remaining.self").color(AQUA).args(text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_GET_MAX_OTHER = (target, value) -> AbstractMessage.prefixed(translatable().key(
                    "floracore.command.air.get.max.other")
            .color(AQUA)
            .args(text(target).color(GREEN),
                    text(value))
            .color(GREEN));

    Args2<String, Integer> COMMAND_AIR_GET_REMAINING_OTHER =
            (target, value) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.air.get.remaining" +
                                    ".other")
                    .color(AQUA)
                    .args(text(target).color(GREEN),
                            text(value))
                    .color(GREEN));

    Args1<Integer> COMMAND_AIR_SET_MAX_SELF = value -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.air.set.max.self").color(AQUA).args(text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_SET_REMAINING_SELF = value -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.air.set.remaining.self").color(AQUA).args(text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_MAX_OTHER = (target, value) -> AbstractMessage.prefixed(translatable().key(
                    "floracore.command.air.set.max.other")
            .color(AQUA)
            .args(text(target).color(GREEN),
                    text(value))
            .color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_REMAINING_OTHER =
            (target, value) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.air.set.remaining" +
                                    ".other")
                    .color(AQUA)
                    .args(text(target).color(GREEN),
                            text(value))
                    .color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_MAX_FROM = (from, value) -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.air.set.max.from").color(AQUA).args(text(from).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_REMAINING_FROM =
            (from, value) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.air.set.remaining.from")
                    .color(AQUA)
                    .args(text(from).color(GREEN),
                            text(value))
                    .color(GREEN));

    Args0 COMMAND_ENDERCHEST_NOT_PLAYER = () -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.enderchest.not-player").color(RED));

    Args0 COMMAND_ENDERCHEST_OPEN_SELF = () -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.enderchest.open.self").color(AQUA));

    Args1<String> COMMAND_ENDERCHEST_OPEN_OTHER = target -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.enderchest.open.other").color(AQUA).args(text(target).color(GREEN)));

    Args2<String, String> COMMAND_ENDERCHEST_OPEN_FOR = (target, for_) -> AbstractMessage.prefixed(translatable().key(
                    "floracore.command.enderchest.open.for")
            .color(AQUA)
            .args(text(target).color(GREEN),
                    text(for_).color(GREEN)));

    Args2<String, String> COMMAND_ENDERCHEST_OPEN_FROM = (from, target) -> AbstractMessage.prefixed(translatable().key(
                    "floracore.command.enderchest.open.from")
            .color(AQUA)
            .args(text(from).color(GREEN),
                    text(target).color(GREEN)));

    Args0 COMMAND_ENDERCHEST_READONLY_TO = () -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.enderchest.readonly.to").color(YELLOW));

    Args0 COMMAND_ENDERCHEST_READONLY_FROM = () -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.enderchest.readonly.from").color(RED));

    Args1<Integer> COMMAND_FOOD_GET_SELF_NUTRITION = value -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.food.get.self.nutrition").color(AQUA).args(text(value).color(GREEN)));

    Args1<Float> COMMAND_FOOD_GET_SELF_SATURATION = value -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.food.get.self.saturation").color(AQUA).args(text(value).color(GREEN)));

    Args2<String, Integer> COMMAND_FOOD_GET_OTHER_NUTRITION =
            (target, value) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.food.get.other" +
                                    ".nutrition")
                    .color(AQUA)
                    .args(text(target).color(GREEN),
                            text(value).color(GREEN)));

    Args2<String, Float> COMMAND_FOOD_GET_OTHER_SATURATION =
            (target, value) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.food.get.other" +
                                    ".saturation")
                    .color(AQUA)
                    .args(text(target).color(GREEN),
                            text(value).color(GREEN)));

    Args1<Integer> COMMAND_FOOD_SET_SELF_NUTRITION = value -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.food.set.self.nutrition").color(AQUA).args(text(value).color(GREEN)));

    Args1<Float> COMMAND_FOOD_SET_SELF_SATURATION = value -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.food.set.self.saturation").color(AQUA).args(text(value).color(GREEN)));

    Args2<String, Integer> COMMAND_FOOD_SET_OTHER_NUTRITION =
            (target, value) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.food.set.other" +
                                    ".nutrition")
                    .color(AQUA)
                    .args(text(target).color(GREEN),
                            text(value).color(GREEN)));

    Args2<String, Float> COMMAND_FOOD_SET_OTHER_SATURATION =
            (target, value) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.food.set.other" +
                                    ".saturation")
                    .color(AQUA)
                    .args(text(target).color(GREEN),
                            text(value).color(GREEN)));

    Args2<String, Integer> COMMAND_FOOD_SET_FROM_NUTRITION =
            (from, value) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.food.set.from" +
                                    ".nutrition")
                    .color(AQUA)
                    .args(text(from).color(GREEN),
                            text(value).color(GREEN)));

    Args2<String, Float> COMMAND_FOOD_SET_FROM_SATURATION =
            (from, value) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.food.set.from" +
                                    ".saturation")
                    .color(AQUA)
                    .args(text(from).color(GREEN),
                            text(value).color(GREEN)));

    Args0 COMMAND_FOOD_SET_INVALID_VALUE = () -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.food.set.invalid-value").color(RED));

    Args2<String, String> COMMAND_HASPERMISSION_YES =
            (target, permission) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.haspermission" +
                                    ".yes")
                    .color(AQUA)
                    .args(text(target).color(GREEN),
                            text(permission))
                    .color(GREEN));

    Args2<String, String> COMMAND_HASPERMISSION_NO =
            (target, permission) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.haspermission" +
                                    ".no")
                    .color(RED)
                    .args(text(target).color(GREEN),
                            text(permission))
                    .color(GREEN));

    Args0 COMMAND_FEED_SELF = () -> AbstractMessage.prefixed(translatable().key("floracore.command.feed.self")
            .color(AQUA));

    Args1<String> COMMAND_FEED_OTHER = target -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.feed.other").color(AQUA).args(text(target).color(GREEN)));

    Args1<String> COMMAND_FEED_FROM = from -> AbstractMessage.prefixed(translatable().key("floracore.command.feed" +
                    ".from")
            .color(AQUA)
            .args(text(from).color(GREEN)));

    Args0 COMMAND_HEAL_SELF = () -> AbstractMessage.prefixed(translatable().key("floracore.command.heal.self")
            .color(AQUA));

    Args1<String> COMMAND_HEAL_OTHER = target -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.heal.other").color(AQUA).args(text(target).color(GREEN)));

    Args1<String> COMMAND_HEAL_FROM = from -> AbstractMessage.prefixed(translatable().key("floracore.command.heal" +
                    ".from")
            .color(AQUA)
            .args(text(from).color(GREEN)));

    Args1<Integer> COMMAND_FIRETICK_SELF = time -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.firetick.self").color(AQUA).args(text(time).color(GREEN)));

    Args2<String, Integer> COMMAND_FIRETICK_OTHER = (target, time) -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.firetick.other").color(AQUA).args(text(target).color(GREEN), text(time).color(GREEN)));

    Args2<String, Integer> COMMAND_FIRETICK_FROM = (from, time) -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.firetick.from").color(AQUA).args(text(from).color(GREEN), text(time).color(GREEN)));

    Args1<Integer> COMMAND_OPLIST_HEADER = count -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.oplist.header").color(AQUA).args(text(count).color(GREEN)));

    Args0 COMMAND_OPLIST_HEADER_NONE = () -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.oplist.header.none").color(AQUA));

    Args3<String, UUID, Boolean> COMMAND_OPLIST_ENTRY =
            (name, uuid, online) -> AbstractMessage.prefixed(translatable()
                    .key("floracore.command.oplist.entry")
                    .color(AQUA)
                    .args(text(name).color(GREEN),
                            text(uuid.toString()).color(GREEN),
                            translatable(online ? "floracore.command.misc.online" : "floracore.command.misc.offline")
                                    .color(online ? GREEN : RED)));

    Args1<Integer> COMMAND_PING_SELF = ping -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.ping.self")
            .color(AQUA)
            .args(text(ping)
                    .color(ping > 250 ?
                            DARK_RED :
                            ping > 200 ?
                                    RED :
                                    ping > 150 ? GOLD : ping > 100 ? YELLOW : ping > 50 ? GREEN : ping > 0 ?
                                            DARK_GREEN : WHITE)));

    Args2<String, Integer> COMMAND_PING_OTHER = (target, ping) -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.ping.other")
            .color(AQUA)
            .args(text(target).color(GREEN),
                    text(ping)
                            .color(ping > 250 ? DARK_RED : ping > 200 ? RED : ping > 150 ? GOLD : ping > 100 ? YELLOW
                                    : ping > 50 ? GREEN : ping > 0 ? DARK_GREEN : WHITE)));

    Args1<Double> COMMAND_MAXHEALTH_GET_SELF = value -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.maxhealth.get.self").color(AQUA).args(text(value).color(GREEN)));

    Args2<String, Double> COMMAND_MAXHEALTH_GET_OTHER = (target, value) -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.maxhealth.get.other")
            .color(AQUA)
            .args(text(target).color(GREEN),
                    text(value).color(GREEN)));

    Args1<Double> COMMAND_MAXHEALTH_SET_SELF = value -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.maxhealth.set.self").color(AQUA).args(text(value).color(GREEN)));

    Args2<String, Double> COMMAND_MAXHEALTH_SET_OTHER = (target, value) -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.maxhealth.set.other")
            .color(AQUA)
            .args(text(target).color(GREEN),
                    text(value).color(GREEN)));

    Args2<String, Double> COMMAND_MAXHEALTH_SET_FROM = (from, value) -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.maxhealth.set.from")
            .color(AQUA)
            .args(text(from).color(GREEN),
                    text(value).color(GREEN)));

    Args0 COMMAND_MISC_SPEED_FLY = () -> translatable("floracore.command.misc.speed.fly");
    Args0 COMMAND_MISC_SPEED_WALK = () -> translatable("floracore.command.misc.speed.walk");

    Args1<String> COMMAND_SPEED_NO_SUCH = (type) -> AbstractMessage.prefixed(translatable()
            // {0} 不是合法的速度类型
            .key("floracore.command.speed.nosuch")
            // {0}
            .args(text(type)).color(RED));

    Args3<String, Component, String> COMMAND_SPEED_OTHER = (sender, type, speed) -> AbstractMessage.prefixed(
            translatable()
                    // {0} 将您的 {1} 速度设为 {2}
                    .key("floracore.command.speed.other")
                    // {}
                    .args(text(sender).color(GREEN), type.color(YELLOW), text(speed).color(DARK_GREEN)).color(AQUA));

    Args3<String, Component, String> COMMAND_SPEED = (target, type, speed) -> AbstractMessage.prefixed(translatable()
            // {0} 将您的 {1} 速度设为 {2}
            .key("floracore.command.speed")
            // {}
            .args(text(target).color(GREEN),
                    type.color(YELLOW),
                    text(speed).color(DARK_GREEN))
            .color(AQUA));

    Args0 COMMAND_SUICIDE = () -> AbstractMessage.prefixed(translatable()
            // 你已自杀
            .key("floracore.command.suicide").color(AQUA));

    Args1<String> COMMAND_SUICIDE_BROADCAST = (target) -> AbstractMessage.prefixed(translatable()
            // {0} 自杀了!
            .key("floracore.command.suicide.broadcast")
            // {}
            .args(text(target).color(GREEN)).color(AQUA));

    Args0 COMMAND_ITEMFLAG_NOITEM_SELF = () -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.itemflag.noitem.self").color(RED));

    Args1<String> COMMAND_ITEMFLAG_NOITEM_OTHER = target -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.itemflag.noitem.other").color(RED).args(text(target).color(GREEN)));

    Args0 COMMAND_ITEMFLAG_UNSUPPORTED_ITEM = () -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.itemflag.unsupported-item").color(RED));

    Args1<String> COMMAND_ITEMFLAG_ALREADY_HAS_SELF = flag -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.itemflag.already-has.self").color(RED).args(text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_ALREADY_HAS_OTHER =
            (target, flag) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.itemflag.already-has" +
                                    ".other")
                    .color(RED)
                    .args(text(target).color(GREEN),
                            text(flag).color(GREEN)));

    Args1<String> COMMAND_ITEMFLAG_HAS_NO_SELF = flag -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.itemflag.has-no.self").color(RED).args(text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_HAS_NO_OTHER =
            (target, flag) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.itemflag.has-no.other")
                    .color(RED)
                    .args(text(target).color(GREEN),
                            text(flag).color(GREEN)));

    Args1<String> COMMAND_ITEMFLAG_ADD_SELF = flag -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.itemflag.add.self").color(AQUA).args(text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_ADD_OTHER = (target, flag) -> AbstractMessage.prefixed(translatable().key(
                    "floracore.command.itemflag.add.other")
            .color(AQUA)
            .args(text(target).color(GREEN),
                    text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_ADD_FROM = (from, flag) -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.itemflag.add.from").color(AQUA).args(text(from).color(GREEN), text(flag).color(GREEN)));

    Args1<String> COMMAND_ITEMFLAG_REMOVE_SELF = flag -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.itemflag.remove.self").color(AQUA).args(text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_REMOVE_OTHER =
            (target, flag) -> AbstractMessage.prefixed(translatable().key(
                            "floracore.command.itemflag.remove.other")
                    .color(AQUA)
                    .args(text(target).color(GREEN),
                            text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_REMOVE_FROM = (from, flag) -> AbstractMessage.prefixed(translatable().key(
                    "floracore.command.itemflag.remove.from")
            .color(AQUA)
            .args(text(from).color(GREEN),
                    text(flag).color(GREEN)));

    Args0 COMMAND_ITEMFLAG_CLEAR_SELF = () -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.itemflag.clear.self").color(AQUA));

    Args1<String> COMMAND_ITEMFLAG_CLEAR_OTHER = target -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.itemflag.clear.other").color(AQUA).args(text(target).color(GREEN)));

    Args1<String> COMMAND_ITEMFLAG_CLEAR_FROM = from -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.itemflag.clear.from").color(AQUA).args(text(from).color(GREEN)));

    Args0 COMMAND_ITEMNAME_NOITEM_SELF = () -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.itemname.noitem.self").color(RED));

    Args1<String> COMMAND_ITEMNAME_NOITEM_OTHER = player -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.itemname.noitem.other").color(RED).args(text(player).color(GREEN)));

    Args0 COMMAND_ITEMNAME_UNSUPPORTED_ITEM = () -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.itemname.unsupported-item").color(RED));

    Args1<String> COMMAND_ITEMNAME_SET_SELF = name -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.itemname.set.self").color(AQUA).args(text(name).color(GREEN)));

    Args2<String, String> COMMAND_ITEMNAME_SET_OTHER = (player, name) -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.itemname.set.other")
            .color(AQUA)
            .args(text(player).color(GREEN),
                    text(name).color(GREEN)));

    Args2<String, String> COMMAND_ITEMNAME_SET_FROM = (from, name) -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.itemname.set.from")
            .color(AQUA)
            .args(text(from).color(GREEN), text(name).color(GREEN)));

    Args0 COMMAND_ITEMNAME_RESET_SELF = () -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.itemname.reset.self").color(AQUA));

    Args1<String> COMMAND_ITEMNAME_RESET_OTHER = player -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.itemname.reset.other").color(AQUA).args(text(player).color(GREEN)));

    Args1<String> COMMAND_ITEMNAME_RESET_FROM = from -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.itemname.reset.from").color(AQUA).args(text(from).color(GREEN)));

    Args1<String> COMMAND_GIVE_ITEM_NOSUCH = itemKey -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.give.item.nosuch").color(RED).args(text(itemKey).color(YELLOW)));

    Args1<String> COMMAND_GIVE_ITEM_NODATA = itemKey -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.give.item.nodata").color(RED).args(text(itemKey).color(YELLOW)));

    Args0 COMMAND_GIVE_ITEM_NBT_SYNTAX_EXCEPTION = () -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.give.item.nbt-syntax-exception").color(RED));

    Args2<String, String> COMMAND_GIVE_ITEM_GIVEN = (item, player) -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.give.item.given").color(AQUA).args(
                    text(item).color(WHITE),
                    text(player).color(GREEN)
            ));

    Args1<String> COMMAND_MISC_REPORT_NOTICE_ACCEPTED = target -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.misc.report.notice.accepted").color(AQUA).args(text(target).color(RED)));

    Args1<String> COMMAND_MISC_REPORT_NOTICE_PROCESSED = (target) -> AbstractMessage.prefixed(translatable()
            .key("floracore.command.misc.report.notice.processed").color(AQUA).args(text(target).color(RED)));

    Args2<String, String> COMMAND_MISC_REPORT_NOTICE_STAFF_ACCEPTED = (reporter, target) -> AbstractMessage.prefixed(
            // 玩家 {0} 对玩家 {1} 的举报已被受理
            translatable().key("floracore.command.misc.report.notice.staff.accepted")
                    .color(AQUA)
                    .args(text(reporter).color(GREEN), text(target).color(RED)));

    Args2<String, String> COMMAND_MISC_REPORT_NOTICE_STAFF_PROCESSED = (reporter, target) -> AbstractMessage.prefixed(
            // 玩家 {0} 对玩家 {1} 的举报已被处理
            translatable().key("floracore.command.misc.report.notice.staff.processed")
                    .color(AQUA)
                    .args(text(reporter).color(GREEN), text(target).color(RED)));

    Args0 COMMAND_MISC_REPORT_THANKS = () -> AbstractMessage.prefixed(translatable().key(
            "floracore.command.misc.report.thanks").color(AQUA));

    Args7<String, String, String, String, String, Boolean, Boolean> COMMAND_MISC_REPORT_BROADCAST = (player, target,
                                                                                                     playerServer,
                                                                                                     targetServer,
                                                                                                     reason,
                                                                                                     playerOnlineStatus, targetOnlineStatus) -> {
        Component infoLine = text()
                // 玩家 {0} 所在的服务器: {1} {2}
                .append(translatable().key("floracore.command.misc.report.broadcast.hover.line.1").color(AQUA)
                        // {}
                        .args(text(player).color(GREEN), text(playerServer).color(YELLOW),
                                OPEN_BRACKET.append(translatable(playerOnlineStatus ? "floracore" +
                                                ".command" +
                                                ".misc.online" : "floracore.command.misc.offline"))
                                        .append(CLOSE_BRACKET)
                                        .color(playerOnlineStatus ? GREEN : RED))).append(newline())
                .append(translatable().key("floracore.command.misc.report.broadcast.hover.line.1").color(AQUA)
                        .args(text(target).color(GREEN), text(targetServer).color(YELLOW),
                                OPEN_BRACKET.append(translatable(targetOnlineStatus ? "floracore" +
                                                ".command" +
                                                ".misc.online" : "floracore.command.misc.offline"))
                                        .append(CLOSE_BRACKET)
                                        .color(targetOnlineStatus ? GREEN : RED)))
                .build();
        if (targetOnlineStatus) {
            infoLine = infoLine.append(newline())
                    .append(ARROW)
                    .append(space())
                    .append(MiscMessage.CLICK_TP.decoration(UNDERLINED, true));
        }
        HoverEvent<Component> hoverEvent = HoverEvent.showText(infoLine);
        ClickEvent clickEvent = ClickEvent.runCommand("/report-tp " + target);
        Component i = AbstractMessage.prefixed(translatable().key("floracore.command.misc.report.broadcast")
                        .color(AQUA)
                        // {}
                        .args(text(player).color(GREEN),
                                text(target).color(RED),
                                text(reason).color(YELLOW)))
                // hoverEvent
                .hoverEvent(hoverEvent);
        if (targetOnlineStatus) {
            i = i.clickEvent(clickEvent);
        }
        // 玩家 {0} 以 {2} 的理由举报了玩家 {1}
        return i;
    };

    Args2<String, String> COMMAND_REPORT_SUCCESS = (target, reason) -> AbstractMessage.prefixed(translatable()
            // 你以"{1}"的理由举报了玩家 {0} ,请您耐心等待工作人员处理!
            .key("floracore.command.report.success")
            .color(AQUA)
            .args(text(target, RED),
                    text(reason, YELLOW)));

    Args0 COMMAND_REPORT_REPEAT = () -> AbstractMessage.prefixed(translatable()
            // 你已经举报过这名玩家了!
            .key("floracore.command.report.repeat")
            .color(RED));

    Args1<String> COMMAND_REPORT_TP_SUCCESS = id -> AbstractMessage.prefixed(translatable()
            // 已将你传送至玩家 {0} 的旁边!
            .key("floracore.command.report.tp.success")
            .color(AQUA)
            .args(text(id, GREEN)));

    Args0 COMMAND_REPORT_TP_TRANSMITTING = () -> AbstractMessage.prefixed(translatable()
            // 传送中...
            .key("floracore.command.report.tp.transmitting")
            .color(AQUA));

    Args0 COMMAND_REPORT_NOT_PERMISSION = () -> AbstractMessage.prefixed(translatable()
            // 你不能举报这名玩家!
            .key("floracore.command.report.no-permission")
            .color(RED));

    Args0 COMMAND_REPORT_SELF = () -> AbstractMessage.prefixed(translatable()
            // 你不能举报你自己!
            .key("floracore.command.report.self")
            .color(RED));

    Args0 COMMAND_REPORT_ABNORMAL = () -> AbstractMessage.prefixed(translatable()
            // 这名玩家的数据异常!
            .key("floracore.command.report.abnormal")
            .color(RED));

    Args0 COMMAND_MISC_NICK_NAME_ILLEGAL_SPACE = () -> AbstractMessage.prefixed(translatable()
            // 请确保你输入的昵称内不含空格
            .key("floracore.command.misc.nick.name.illegal.space")
            .append(FULL_STOP)
            .color(RED));

    Args0 COMMAND_MISC_NICK_NAME_ILLEGAL_LENGTH = () -> AbstractMessage.prefixed(translatable()
            // 请确保昵称长度在3到16个字符之间
            .key("floracore.command.misc.nick.name.illegal.length")
            .append(FULL_STOP)
            .color(RED));

    Args0 COMMAND_MISC_NICK_NAME_ILLEGAL_CHARACTER = () -> AbstractMessage.prefixed(translatable()
            // 你输入的昵称不符合Minecraft的命名规则
            .key("floracore.command.misc.nick.name.illegal.character")
            .append(FULL_STOP)
            .color(RED));
}
