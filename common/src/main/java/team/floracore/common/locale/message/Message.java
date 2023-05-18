package team.floracore.common.locale.message;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.event.*;
import org.bukkit.*;
import org.floracore.api.*;
import org.floracore.api.data.*;
import team.floracore.common.util.*;

import java.time.*;
import java.util.*;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

public interface Message extends AbstractMessage {
    Args2<Boolean, String> COMMAND_FLY = (status, target) -> AbstractMessage.prefixed(translatable()
            // {1} 的飞行模式被设置为 {0}
            .key("floracore.command.fly").color(AQUA)
            // 开 / 关
            .args(translatable(status ? "floracore.command.misc.on" : "floracore.command.misc.off").color(GREEN), text(target).color(RED)).append(FULL_STOP));

    Args2<Boolean, String> COMMAND_FLY_FROM = (status, from) -> AbstractMessage.prefixed(translatable()
            // {1} 将您的游戏模式设置为 {0}
            .key("floracore.command.fly.from").color(AQUA)
            // 开 / 关
            .args(translatable(status ? "floracore.command.misc.on" : "floracore.command.misc.off").color(GREEN), text(from).color(GREEN)).append(FULL_STOP));
    Args2<Component, String> COMMAND_GAMEMODE = (mode, target) -> AbstractMessage.prefixed(translatable()
            // 将{1}的游戏模式设置为{0}
            .key("floracore.command.gamemode").color(AQUA).args(mode.color(GREEN), text(target).color(GREEN)).append(FULL_STOP));

    Args2<Component, String> COMMAND_GAMEMODE_FROM = (mode, from) -> AbstractMessage.prefixed(translatable()
            // {1} 将您的游戏模式设置为 {0}
            .key("floracore.command.gamemode.from").color(AQUA).args(mode.color(GREEN), text(from).color(GREEN)).append(FULL_STOP));

    Args1<String> COMMAND_GAMEMODE_NOSUCH = (mode) -> AbstractMessage.prefixed(translatable()
            // {0} 不是合法的游戏模式
            .key("floracore.command.gamemode.nosuch").color(RED).args(text(mode).color(GREEN)).append(FULL_STOP));

    Args1<String> COMMAND_WEATHER_NOSUCH = (weather) -> AbstractMessage.prefixed(translatable()
            // {0} 不是合法的天气类型
            .key("floracore.command.weather.nosuch").color(RED).args(text(weather).color(GREEN)).append(FULL_STOP));

    Args2<World, Component> COMMAND_WEATHER_NORMAL = (world, weather) -> AbstractMessage.prefixed(translatable()
            // 你将 {0} 的天气设为 {1}
            .key("floracore.command.weather.normal").color(AQUA).args(text(world.getName()).color(GREEN), weather.color(GREEN)).append(FULL_STOP));

    Args3<World, Component, String> COMMAND_WEATHER_TIME = (world, weather, time) -> AbstractMessage.prefixed(translatable()
            // 你将 {0} 的天气设为 {1}，持续 {2} 秒
            .key("floracore.command.weather.time").color(AQUA).args(text(world.getName()).color(GREEN), weather.color(GREEN), text(time).color(GREEN)).append(FULL_STOP));

    Args1<Long> DURATION_FORMAT = (ticks) -> translatable()
            // {0} 或 {1}（或 {2} ）
            .key("floracore.duration.format").color(RED).args(text(DescParseTickFormat.format24(ticks)).color(GREEN), text(DescParseTickFormat.format12(ticks)).color(GREEN), text(DescParseTickFormat.formatTicks(ticks)).color(GREEN)).build();

    Args2<World, Component> COMMAND_TIME_WORLD_CURRENT = (world, time) -> AbstractMessage.prefixed(translatable()
            // 当前 {0} 的时间是 {1}
            .key("floracore.command.time.world.current").color(AQUA).args(text(world.getName()).color(GREEN), time));

    Args2<String, Component> COMMAND_TIME_SET = (world, time) -> AbstractMessage.prefixed(translatable()
            // {0} 的时间被设置为 {1}
            .key("floracore.command.time.set").color(AQUA).args(text(world).color(GREEN), time));

    Args2<String, Component> COMMAND_TIME_ADD = (world, time) -> AbstractMessage.prefixed(translatable()
            // {0} 的时间已被 {1} 快进
            .key("floracore.command.time.add").color(AQUA).args(text(world).color(GREEN), time));

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
            .key("floracore.command.hat.removed").color(AQUA).append(FULL_STOP));

    Args0 COMMAND_HAT_PLACED = () -> AbstractMessage.prefixed(translatable()
            // 你戴上了新帽子
            .key("floracore.command.hat.placed").color(AQUA).append(FULL_STOP));

    Args0 COMMAND_INVSEE_SELF = () -> AbstractMessage.prefixed(translatable()
            // 你只能查看其他玩家的物品栏!
            .key("floracore.command.invsee.self").color(RED));

    Args1<String> COMMAND_INVSEE = (target) -> AbstractMessage.prefixed(translatable()
            // 你打开了 {0} 的物品栏
            .key("floracore.command.invsee").color(AQUA).args(text(target).color(GREEN)).append(FULL_STOP));

    Args1<String> DATA_NONE = target -> AbstractMessage.prefixed(translatable()
            // {0} 无记录的数据
            .key("floracore.command.generic.data.none").color(AQUA).args(text(target)).append(FULL_STOP));

    Args1<String> DATA_HEADER = target -> AbstractMessage.prefixed(translatable()
            // {0} 的数据信息:
            .key("floracore.command.generic.data.info.title").color(AQUA).args(text(target)));

    Args4<String, String, String, Long> DATA_ENTRY = (type, key, value, expiry) -> {
        Instant instant = Instant.ofEpochMilli(expiry);
        Instant now = Instant.now();
        Duration timeElapsed = Duration.between(now, instant);
        return AbstractMessage.prefixed(text().append(text(type, GREEN)).append(space()).append(text("->", AQUA)).append(space()).append(text(key, AQUA)).append(text(" - ", WHITE)).append(text().color(WHITE).append(text('\'')).append(text(value)).append(text('\''))).apply(builder -> {
            if (expiry > 0) {
                builder.append(space());
                builder.append(text().color(DARK_GRAY).append(OPEN_BRACKET).append(translatable()
                        // 过期时间
                        .key("floracore.command.generic.info.expires-in").color(GRAY).append(space()).append(text().color(AQUA).append(DurationFormatter.CONCISE.format(timeElapsed)))).append(CLOSE_BRACKET));
            }
        }));
    };

    Args2<Component, String> SERVER_DATA_ENTRY = (key, value) -> AbstractMessage.prefixed(text().append(key.color(GREEN)).append(space()).append(text("->", AQUA)).append(space()).append(text(value, WHITE)).apply(builder -> {
    }));
    Args2<Component, Component> SERVER_DATA_ENTRY_1 = (key, value) -> AbstractMessage.prefixed(text().append(key.color(GREEN)).append(space()).append(text("->", AQUA)).append(space()).append(value.color(WHITE)).apply(builder -> {
    }));

    Args3<String, String, String> SET_DATA_SUCCESS = (key, value, target) -> AbstractMessage.prefixed(translatable()
            // 成功将 {2} 的数据键 {0} 设置为 {1}
            .key("floracore.command.generic.data.set").color(GREEN).args(text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')), text().color(WHITE).append(text('\'')).append(AbstractMessage.formatColoredValue(value)).append(text('\'')), text().color(AQUA).append(text(target))).append(FULL_STOP));

    Args4<String, String, String, Duration> SET_DATA_TEMP_SUCCESS = (key, value, target, duration) -> AbstractMessage.prefixed(translatable()
            // 成功中将 {2} 的数据键 {0} 设置为 {1}, 有效期\: {3}
            .key("floracore.command.generic.data.set-temp").color(GREEN).args(text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')), text().color(WHITE).append(text('\'')).append(AbstractMessage.formatColoredValue(value)).append(text('\'')), text().color(AQUA).append(text(target)), text().color(AQUA).append(DurationFormatter.LONG.format(duration))).append(FULL_STOP));

    Args2<String, String> DOESNT_HAVE_DATA = (target, key) -> AbstractMessage.prefixed(translatable()
            // {0} 没有设置数据键 {1}
            .key("floracore.command.generic.data.doesnt-have").color(RED).args(text().color(AQUA).append(text(target)), text().color(WHITE).append(text('\'')).append(text(key)).append(text('\''))).append(FULL_STOP));

    Args2<String, String> UNSET_DATA_SUCCESS = (key, target) -> AbstractMessage.prefixed(translatable()
            // 成功中为 {1} 取消设置数据键 {0}
            .key("floracore.command.generic.data.unset").color(GREEN).args(text().color(WHITE).append(text('\'')).append(text(key)).append(text('\'')), text().color(AQUA).append(text(target))).append(FULL_STOP));

    Args2<String, DataType> DATA_CLEAR_SUCCESS = (target, type) -> AbstractMessage.prefixed(translatable()
            // {0} 的数据({1})已被清除
            .key("floracore.command.generic.data.clear").color(GREEN)
            // target
            .args(text().color(AQUA).append(text(target)),
                    // type
                    text().color(WHITE).append(OPEN_BRACKET).append(text(type == null ? "*" : type.getName())).append(CLOSE_BRACKET))
            // .
            .append(FULL_STOP));

    Args0 COMMAND_TELEPORT_TOP = () -> AbstractMessage.prefixed(translatable()
            // 已传送到顶部
            .key("floracore.command.teleport.top").color(AQUA)).append(FULL_STOP);

    Args0 COMMAND_NICK_SETUP_SKIN = () -> AbstractMessage.prefixed(translatable()
            // 你已拥有皮肤了!
            .key("floracore.command.nick.setup.skin").color(AQUA));

    Args0 COMMAND_UNNICK_SUCCESS = () -> AbstractMessage.prefixed(translatable()
            // 你的昵称已移除！
            .key("floracore.command.unnick.success").color(AQUA));

    Args0 COMMAND_UNNICK_NOT_IN = () -> AbstractMessage.prefixed(translatable()
            // 你当前未处于昵称状态！
            .key("floracore.command.unnick.not-in").color(RED));

    Args1<Integer> COMMAND_AIR_GET_SELF_MAX = value -> AbstractMessage.prefixed(translatable().key("floracore.command.air.get.self.max").color(AQUA).args(text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_GET_SELF_REMAINING = value -> AbstractMessage.prefixed(translatable().key("floracore.command.air.get.self.remaining").color(AQUA).args(text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_GET_OTHER_MAX = (target, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.air.get.other.max").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_GET_OTHER_REMAINING = (target, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.air.get.other.remaining").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_SET_SELF_MAX = value -> AbstractMessage.prefixed(translatable().key("floracore.command.air.set.self.max").color(AQUA).args(text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_SET_SELF_REMAINING = value -> AbstractMessage.prefixed(translatable().key("floracore.command.air.set.self.remaining").color(AQUA).args(text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_OTHER_MAX = (target, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.air.set.other.max").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_OTHER_REMAINING = (target, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.air.set.other.remaining").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_FROM_MAX = (from, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.air.set.from.max").color(AQUA).args(text(from).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_FROM_REMAINING = (from, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.air.set.from.remaining").color(AQUA).args(text(from).color(GREEN), text(value)).color(GREEN));

    Args0 COMMAND_MISC_NICK_ACTION_BAR = () -> translatable()
            // 当前已 {0}
            .key("floracore.command.misc.nick.action-bar").color(WHITE)
            // 匿名
            .args(translatable("floracore.command.misc.nick.action-bar.nick").color(RED)).build();

    Args1<String> COMMAND_MISC_NICK_RANK_UNKNOWN = (rank) -> AbstractMessage.prefixed(translatable()
            // {0} {1} 不存在!
            .key("floracore.command.misc.nick.rank.unknown")
            // {0}
            .args(translatable("floracore.command.misc.nick.book.rank-page.rank"), text(rank).color(DARK_RED).decoration(BOLD, true)).color(RED));

    Args1<String> COMMAND_MISC_NICK_RANK_NO_PERMISSION = (rank) -> AbstractMessage.prefixed(translatable()
            // 你没有 {0} {1} 的使用权限!
            .key("floracore.command.misc.nick.rank.no-permission")
            // {0}
            .args(translatable("floracore.command.misc.nick.book.rank-page.rank"), text(rank).color(DARK_RED).decoration(BOLD, true)).color(RED));

    Args1<String> COMMAND_MISC_CHAT_DOES_NOT_EXIST = (type) -> AbstractMessage.prefixed(translatable()
            // 不存在 {0} 这个聊天频道!
            .key("floracore.command.misc.chat.does-not-exist")
            // {0}
            .args(text(type, DARK_RED)).color(RED));

    Args1<Component> COMMAND_MISC_CHAT_SUCCESS = (type) -> AbstractMessage.prefixed(translatable()
            // 成功切换到 {0} 聊天频道!
            .key("floracore.command.misc.chat.success")
            // {0}
            .args(type).color(YELLOW));

    Args1<String> COMMAND_MISC_CHAT_IS_IN = (type) -> AbstractMessage.prefixed(translatable()
            // 你当前正处于 {0} 聊天频道中!
            .key("floracore.command.misc.chat.is-in")
            // {0}
            .args(text(type, DARK_RED)).color(RED));

    Args2<String, String> COMMAND_REALNAME_SUCCESS = (name, realName) -> AbstractMessage.prefixed(translatable()
            // 玩家 {0} 的真实昵称为 {1}
            .key("floracore.command.realname.success")
            // {}
            .args(text(name).color(GREEN), text(realName).color(GREEN)).color(AQUA));


    Args1<Integer> COMMAND_AIR_GET_MAX_SELF = value -> AbstractMessage.prefixed(translatable().key("floracore.command.air.get.max.self").color(AQUA).args(text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_GET_REMAINING_SELF = value -> AbstractMessage.prefixed(translatable().key("floracore.command.air.get.remaining.self").color(AQUA).args(text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_GET_MAX_OTHER = (target, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.air.get.max.other").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_GET_REMAINING_OTHER = (target, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.air.get.remaining.other").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_SET_MAX_SELF = value -> AbstractMessage.prefixed(translatable().key("floracore.command.air.set.max.self").color(AQUA).args(text(value)).color(GREEN));

    Args1<Integer> COMMAND_AIR_SET_REMAINING_SELF = value -> AbstractMessage.prefixed(translatable().key("floracore.command.air.set.remaining.self").color(AQUA).args(text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_MAX_OTHER = (target, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.air.set.max.other").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_REMAINING_OTHER = (target, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.air.set.remaining.other").color(AQUA).args(text(target).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_MAX_FROM = (from, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.air.set.max.from").color(AQUA).args(text(from).color(GREEN), text(value)).color(GREEN));

    Args2<String, Integer> COMMAND_AIR_SET_REMAINING_FROM = (from, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.air.set.remaining.from").color(AQUA).args(text(from).color(GREEN), text(value)).color(GREEN));

    Args0 COMMAND_ENDERCHEST_NOT_PLAYER = () -> AbstractMessage.prefixed(translatable().key("floracore.command.enderchest.not-player").color(RED));

    Args0 COMMAND_ENDERCHEST_OPEN_SELF = () -> AbstractMessage.prefixed(translatable().key("floracore.command.enderchest.open.self").color(AQUA));

    Args1<String> COMMAND_ENDERCHEST_OPEN_OTHER = target -> AbstractMessage.prefixed(translatable().key("floracore.command.enderchest.open.other").color(AQUA).args(text(target).color(GREEN)));

    Args2<String, String> COMMAND_ENDERCHEST_OPEN_FOR = (target, for_) -> AbstractMessage.prefixed(translatable().key("floracore.command.enderchest.open.for").color(AQUA).args(text(target).color(GREEN), text(for_).color(GREEN)));

    Args2<String, String> COMMAND_ENDERCHEST_OPEN_FROM = (from, target) -> AbstractMessage.prefixed(translatable().key("floracore.command.enderchest.open.from").color(AQUA).args(text(from).color(GREEN), text(target).color(GREEN)));

    Args0 COMMAND_ENDERCHEST_READONLY_TO = () -> AbstractMessage.prefixed(translatable().key("floracore.command.enderchest.readonly.to").color(YELLOW));

    Args0 COMMAND_ENDERCHEST_READONLY_FROM = () -> AbstractMessage.prefixed(translatable().key("floracore.command.enderchest.readonly.from").color(RED));

    Args1<Integer> COMMAND_FOOD_GET_SELF_NUTRITION = value -> AbstractMessage.prefixed(translatable().key("floracore.command.food.get.self.nutrition").color(AQUA).args(text(value).color(GREEN)));

    Args1<Float> COMMAND_FOOD_GET_SELF_SATURATION = value -> AbstractMessage.prefixed(translatable().key("floracore.command.food.get.self.saturation").color(AQUA).args(text(value).color(GREEN)));

    Args2<String, Integer> COMMAND_FOOD_GET_OTHER_NUTRITION = (target, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.food.get.other.nutrition").color(AQUA).args(text(target).color(GREEN), text(value).color(GREEN)));

    Args2<String, Float> COMMAND_FOOD_GET_OTHER_SATURATION = (target, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.food.get.other.saturation").color(AQUA).args(text(target).color(GREEN), text(value).color(GREEN)));

    Args1<Integer> COMMAND_FOOD_SET_SELF_NUTRITION = value -> AbstractMessage.prefixed(translatable().key("floracore.command.food.set.self.nutrition").color(AQUA).args(text(value).color(GREEN)));

    Args1<Float> COMMAND_FOOD_SET_SELF_SATURATION = value -> AbstractMessage.prefixed(translatable().key("floracore.command.food.set.self.saturation").color(AQUA).args(text(value).color(GREEN)));

    Args2<String, Integer> COMMAND_FOOD_SET_OTHER_NUTRITION = (target, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.food.set.other.nutrition").color(AQUA).args(text(target).color(GREEN), text(value).color(GREEN)));

    Args2<String, Float> COMMAND_FOOD_SET_OTHER_SATURATION = (target, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.food.set.other.saturation").color(AQUA).args(text(target).color(GREEN), text(value).color(GREEN)));

    Args2<String, Integer> COMMAND_FOOD_SET_FROM_NUTRITION = (from, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.food.set.from.nutrition").color(AQUA).args(text(from).color(GREEN), text(value).color(GREEN)));

    Args2<String, Float> COMMAND_FOOD_SET_FROM_SATURATION = (from, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.food.set.from.saturation").color(AQUA).args(text(from).color(GREEN), text(value).color(GREEN)));

    Args0 COMMAND_FOOD_SET_INVALIDVALUE = () -> AbstractMessage.prefixed(translatable().key("floracore.command.food.set.invalidvalue").color(RED));

    Args2<String, String> COMMAND_HASPERMISSION_YES = (target, permission) -> AbstractMessage.prefixed(translatable().key("floracore.command.haspermission.yes").color(AQUA).args(text(target).color(GREEN), text(permission)).color(GREEN));

    Args2<String, String> COMMAND_HASPERMISSION_NO = (target, permission) -> AbstractMessage.prefixed(translatable().key("floracore.command.haspermission.no").color(RED).args(text(target).color(GREEN), text(permission)).color(GREEN));

    Args0 COMMAND_FEED_SELF = () -> AbstractMessage.prefixed(translatable().key("floracore.command.feed.self").color(AQUA));

    Args1<String> COMMAND_FEED_OTHER = target -> AbstractMessage.prefixed(translatable().key("floracore.command.feed.other").color(AQUA).args(text(target).color(GREEN)));

    Args1<String> COMMAND_FEED_FROM = from -> AbstractMessage.prefixed(translatable().key("floracore.command.feed.from").color(AQUA).args(text(from).color(GREEN)));

    Args0 COMMAND_HEAL_SELF = () -> AbstractMessage.prefixed(translatable().key("floracore.command.heal.self").color(AQUA));

    Args1<String> COMMAND_HEAL_OTHER = target -> AbstractMessage.prefixed(translatable().key("floracore.command.heal.other").color(AQUA).args(text(target).color(GREEN)));

    Args1<String> COMMAND_HEAL_FROM = from -> AbstractMessage.prefixed(translatable().key("floracore.command.heal.from").color(AQUA).args(text(from).color(GREEN)));

    Args1<Integer> COMMAND_FIRETICK_SELF = time -> AbstractMessage.prefixed(translatable().key("floracore.command.firetick.self").color(AQUA).args(text(time).color(GREEN)));

    Args2<String, Integer> COMMAND_FIRETICK_OTHER = (target, time) -> AbstractMessage.prefixed(translatable().key("floracore.command.firetick.other").color(AQUA).args(text(target).color(GREEN), text(time).color(GREEN)));

    Args2<String, Integer> COMMAND_FIRETICK_FROM = (from, time) -> AbstractMessage.prefixed(translatable().key("floracore.command.firetick.from").color(AQUA).args(text(from).color(GREEN), text(time).color(GREEN)));

    Args1<Integer> COMMAND_OPLIST_HEADER = count -> AbstractMessage.prefixed(translatable().key("floracore.command.oplist.header").color(AQUA).args(text(count).color(GREEN)));

    Args0 COMMAND_OPLIST_HEADER_NONE = () -> AbstractMessage.prefixed(translatable().key("floracore.command.oplist.header.none").color(AQUA));

    Args3<String, UUID, Boolean> COMMAND_OPLIST_ENTRY = (name, uuid, online) -> AbstractMessage.prefixed(translatable().key("floracore.command.oplist.entry").color(AQUA).args(text(name).color(GREEN), text(uuid.toString()).color(GREEN), translatable(online ? "floracore.command.misc.online" : "floracore.command.misc.offline").color(online ? GREEN : RED)));

    Args1<Integer> COMMAND_PING_SELF = ping -> AbstractMessage.prefixed(translatable().key("floracore.command.ping.self").color(AQUA).args(text(ping).color(ping > 250 ? DARK_RED : ping > 200 ? RED : ping > 150 ? GOLD : ping > 100 ? YELLOW : ping > 50 ? GREEN : ping > 0 ? DARK_GREEN : WHITE)));

    Args2<String, Integer> COMMAND_PING_OTHER = (target, ping) -> AbstractMessage.prefixed(translatable().key("floracore.command.ping.other").color(AQUA).args(text(target).color(GREEN), text(ping).color(ping > 250 ? DARK_RED : ping > 200 ? RED : ping > 150 ? GOLD : ping > 100 ? YELLOW : ping > 50 ? GREEN : ping > 0 ? DARK_GREEN : WHITE)));

    Args1<Double> COMMAND_MAXHEALTH_GET_SELF = value -> AbstractMessage.prefixed(translatable().key("floracore.command.maxhealth.get.self").color(AQUA).args(text(value).color(GREEN)));

    Args2<String, Double> COMMAND_MAXHEALTH_GET_OTHER = (target, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.maxhealth.get.other").color(AQUA).args(text(target).color(GREEN), text(value).color(GREEN)));

    Args1<Double> COMMAND_MAXHEALTH_SET_SELF = value -> AbstractMessage.prefixed(translatable().key("floracore.command.maxhealth.set.self").color(AQUA).args(text(value).color(GREEN)));

    Args2<String, Double> COMMAND_MAXHEALTH_SET_OTHER = (target, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.maxhealth.set.other").color(AQUA).args(text(target).color(GREEN), text(value).color(GREEN)));

    Args2<String, Double> COMMAND_MAXHEALTH_SET_FROM = (from, value) -> AbstractMessage.prefixed(translatable().key("floracore.command.maxhealth.set.from").color(AQUA).args(text(from).color(GREEN), text(value).color(GREEN)));

    Args0 COMMAND_MISC_SPEED_FLY = () -> translatable("floracore.command.misc.speed.fly");
    Args0 COMMAND_MISC_SPEED_WALK = () -> translatable("floracore.command.misc.speed.walk");

    Args1<String> COMMAND_SPEED_NO_SUCH = (type) -> AbstractMessage.prefixed(translatable()
            // {0} 不是合法的速度类型
            .key("floracore.command.speed.nosuch")
            // {0}
            .args(text(type)).color(RED));

    Args3<String, Component, String> COMMAND_SPEED_OTHER = (sender, type, speed) -> AbstractMessage.prefixed(translatable()
            // {0} 将您的 {1} 速度设为 {2}
            .key("floracore.command.speed.other")
            // {}
            .args(text(sender).color(GREEN), type.color(YELLOW), text(speed).color(DARK_GREEN)).color(AQUA));

    Args3<String, Component, String> COMMAND_SPEED = (target, type, speed) -> AbstractMessage.prefixed(translatable()
            // {0} 将您的 {1} 速度设为 {2}
            .key("floracore.command.speed")
            // {}
            .args(text(target).color(GREEN), type.color(YELLOW), text(speed).color(DARK_GREEN)).color(AQUA));

    Args0 COMMAND_SUICIDE = () -> AbstractMessage.prefixed(translatable()
            // 你已自杀
            .key("floracore.command.suicide").color(AQUA));

    Args1<String> COMMAND_SUICIDE_BROADCAST = (target) -> AbstractMessage.prefixed(translatable()
            // {0} 自杀了!
            .key("floracore.command.suicide.broadcast")
            // {}
            .args(text(target).color(GREEN)).color(AQUA));

    Args0 COMMAND_ITEMFLAG_NOITEM_SELF = () -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.noitem.self").color(RED));

    Args1<String> COMMAND_ITEMFLAG_NOITEM_OTHER = target -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.noitem.other").color(RED).args(text(target).color(GREEN)));

    Args0 COMMAND_ITEMFLAG_UNSUPPORTEDITEM = () -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.unsupporteditem").color(RED));

    Args1<String> COMMAND_ITEMFLAG_ALREADYHAS_SELF = flag -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.alreadyhas.self").color(RED).args(text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_ALREADYHAS_OTHER = (target, flag) -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.alreadyhas.other").color(RED).args(text(target).color(GREEN), text(flag).color(GREEN)));

    Args1<String> COMMAND_ITEMFLAG_HASNO_SELF = flag -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.hasno.self").color(RED).args(text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_HASNO_OTHER = (target, flag) -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.hasno.other").color(RED).args(text(target).color(GREEN), text(flag).color(GREEN)));

    Args1<String> COMMAND_ITEMFLAG_ADD_SELF = flag -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.add.self").color(AQUA).args(text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_ADD_OTHER = (target, flag) -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.add.other").color(AQUA).args(text(target).color(GREEN), text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_ADD_FROM = (from, flag) -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.add.from").color(AQUA).args(text(from).color(GREEN), text(flag).color(GREEN)));

    Args1<String> COMMAND_ITEMFLAG_REMOVE_SELF = flag -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.remove.self").color(AQUA).args(text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_REMOVE_OTHER = (target, flag) -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.remove.other").color(AQUA).args(text(target).color(GREEN), text(flag).color(GREEN)));

    Args2<String, String> COMMAND_ITEMFLAG_REMOVE_FROM = (from, flag) -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.remove.from").color(AQUA).args(text(from).color(GREEN), text(flag).color(GREEN)));

    Args0 COMMAND_ITEMFLAG_CLEAR_SELF = () -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.clear.self").color(AQUA));

    Args1<String> COMMAND_ITEMFLAG_CLEAR_OTHER = target -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.clear.other").color(AQUA).args(text(target).color(GREEN)));

    Args1<String> COMMAND_ITEMFLAG_CLEAR_FROM = from -> AbstractMessage.prefixed(translatable().key("floracore.command.itemflag.clear.from").color(AQUA).args(text(from).color(GREEN)));

    Args0 COMMAND_ITEMNAME_NOITEM_SELF = () -> AbstractMessage.prefixed(translatable().key("floracore.command.itemname.noitem.self").color(RED));

    Args1<String> COMMAND_ITEMNAME_NOITEM_OTHER = player -> AbstractMessage.prefixed(translatable().key("floracore.command.itemname.noitem.other").color(RED).args(text(player).color(GREEN)));

    Args0 COMMAND_ITEMNAME_UNSUPPORTEDITEM = () -> AbstractMessage.prefixed(translatable().key("floracore.command.itemname.unsupporteditem").color(RED));

    Args1<String> COMMAND_ITEMNAME_SET_SELF = name -> AbstractMessage.prefixed(translatable().key("floracore.command.itemname.set.self").color(AQUA).args(text(name).color(GREEN)));

    Args2<String, String> COMMAND_ITEMNAME_SET_OTHER = (player, name) -> AbstractMessage.prefixed(translatable().key("floracore.command.itemname.set.other").color(AQUA).args(text(player).color(GREEN), text(name).color(GREEN)));

    Args2<String, String> COMMAND_ITEMNAME_SET_FROM = (from, name) -> AbstractMessage.prefixed(translatable().key("floracore.command.itemname.set.from").color(AQUA).args(text(from).color(GREEN), text(name).color(GREEN)));

    Args0 COMMAND_ITEMNAME_RESET_SELF = () -> AbstractMessage.prefixed(translatable().key("floracore.command.itemname.reset.self").color(AQUA));

    Args1<String> COMMAND_ITEMNAME_RESET_OTHER = player -> AbstractMessage.prefixed(translatable().key("floracore.command.itemname.reset.other").color(AQUA).args(text(player).color(GREEN)));

    Args1<String> COMMAND_ITEMNAME_RESET_FROM = from -> AbstractMessage.prefixed(translatable().key("floracore.command.itemname.reset.from").color(AQUA).args(text(from).color(GREEN)));

    Args1<String> COMMAND_GIVE_ITEM_NOSUCH = itemKey -> AbstractMessage.prefixed(translatable().key("floracore.command.give.item.nosuch").color(RED).args(text(itemKey).color(YELLOW)));

    Args1<String> COMMAND_GIVE_ITEM_NODATA = itemKey -> AbstractMessage.prefixed(translatable().key("floracore.command.give.item.nodata").color(RED).args(text(itemKey).color(YELLOW)));

    Args0 COMMAND_GIVE_ITEM_NBTSYTAXEXCEPTION = () -> AbstractMessage.prefixed(translatable().key("floracore.command.give.item.nbtsyntaxexception").color(RED));

    Args2<String, String> COMMAND_GIVE_ITEM_GIVEN = (item, player) -> AbstractMessage.prefixed(translatable().key("floracore.command.give.item.given").color(AQUA).args(
            text(item).color(WHITE),
            text(player).color(GREEN)
    ));

    Args1<String> COMMAND_BROADCAST = contents -> text().append(MiscMessage.PREFIX_BROADCAST).append(space()).append(AbstractMessage.formatColoredValue(contents)).build();

    Args1<String> COMMAND_MISC_REPORT_NOTICE_ACCEPTED = target -> AbstractMessage.prefixed(translatable().key("floracore.command.misc.report.notice.accepted").color(AQUA).args(text(target).color(RED)));

    Args1<String> COMMAND_MISC_REPORT_NOTICE_PROCESSED = (target) -> AbstractMessage.prefixed(translatable().key("floracore.command.misc.report.notice.processed").color(AQUA).args(text(target).color(RED)));

    Args2<String, String> COMMAND_MISC_REPORT_NOTICE_STAFF_ACCEPTED = (reporter, target) -> AbstractMessage.prefixed(translatable().key("floracore.command.misc.report.notice.staff.accepted").color(AQUA).args(text(reporter).color(GREEN), text(target).color(RED)));

    Args2<String, String> COMMAND_MISC_REPORT_NOTICE_STAFF_PROCESSED = (reporter, target) -> AbstractMessage.prefixed(translatable().key("floracore.command.misc.report.notice.staff.processed").color(AQUA).args(text(reporter).color(GREEN), text(target).color(RED)));

    Args0 COMMAND_MISC_REPORT_THANKS = () -> AbstractMessage.prefixed(translatable().key("floracore.command.misc.report.thanks").color(AQUA));

    Args7<String, String, String, String, String, Boolean, Boolean> COMMAND_MISC_REPORT_BROADCAST = (player, target, playerServer, targetServer, reason, playerOnlineStatus, targetOnlineStatus) -> {
        Component infoLine = text()
                // 玩家 {0} 所在的服务器: {1} {2}
                .append(translatable().key("floracore.command.misc.report.broadcast.hover.line.1").color(AQUA)
                        // {}
                        .args(text(player).color(GREEN), text(playerServer).color(YELLOW),
                                OPEN_BRACKET.append(translatable(playerOnlineStatus ? "floracore.command.misc.online" : "floracore.command.misc.offline")).append(CLOSE_BRACKET).color(playerOnlineStatus ? GREEN : RED))).append(newline())
                .append(translatable().key("floracore.command.misc.report.broadcast.hover.line.1").color(AQUA)
                        .args(text(target).color(GREEN), text(targetServer).color(YELLOW),
                                OPEN_BRACKET.append(translatable(targetOnlineStatus ? "floracore.command.misc.online" : "floracore.command.misc.offline")).append(CLOSE_BRACKET).color(targetOnlineStatus ? GREEN : RED)))
                .build();
        if (targetOnlineStatus) {
            infoLine = infoLine.append(newline()).append(ARROW).append(space()).append(MiscMessage.CLICK_TP.decoration(UNDERLINED, true));
        }
        HoverEvent<Component> hoverEvent = HoverEvent.showText(infoLine);
        ClickEvent clickEvent = ClickEvent.runCommand("/report-tp " + target);
        Component i = AbstractMessage.prefixed(translatable().key("floracore.command.misc.report.broadcast").color(AQUA)
                        // {}
                        .args(text(player).color(GREEN), text(target).color(RED), text(reason).color(YELLOW)))
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
            .key("floracore.command.report.success").color(AQUA).args(text(target, RED), text(reason, YELLOW)));

    Args0 COMMAND_REPORT_REPEAT = () -> AbstractMessage.prefixed(translatable()
            // 你已经举报过这名玩家了!
            .key("floracore.command.report.repeat").color(RED));

    Args1<String> COMMAND_REPORT_TP_SUCCESS = id -> AbstractMessage.prefixed(translatable()
            // 已将你传送至玩家 {0} 的旁边!
            .key("floracore.command.report.tp.success").color(AQUA).args(text(id, GREEN)));

    Args0 COMMAND_REPORT_TP_TRANSMITTING = () -> AbstractMessage.prefixed(translatable()
            // 传送中...
            .key("floracore.command.report.tp.transmitting").color(AQUA));

    Args0 COMMAND_REPORT_NOT_PERMISSION = () -> AbstractMessage.prefixed(translatable()
            // 你不能举报这名玩家!
            .key("floracore.command.report.no-permission").color(RED));

    Args0 COMMAND_REPORT_SELF = () -> AbstractMessage.prefixed(translatable()
            // 你不能举报你自己!
            .key("floracore.command.report.self").color(RED));

    Args0 COMMAND_REPORT_ABNORMAL = () -> AbstractMessage.prefixed(translatable()
            // 这名玩家的数据异常!
            .key("floracore.command.report.abnormal").color(RED));

    Args1<String> COMMAND_LANGUAGE_CHANGE_SUCCESS = (language) -> translatable()
            // 你已成功将你的显示语言更改为 {0} !
            .key("floracore.command.language.change.success").args(text(language, GREEN)).color(AQUA).build();

    Args1<String> COMMAND_MISC_PARTY_INVITE_EXPIRED = (target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                translatable("floracore.command.misc.party.invite.expired", YELLOW).args(text(target, GRAY)),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_INVITE_NO_PERMISSION = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你没有发送组队邀请的权限!
                        .key("floracore.command.misc.party.invite.no-permission").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_INVITE_SELF = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你不能向自己发送组队邀请!
                        .key("floracore.command.misc.party.invite.self").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_INVITE_HAS_BEEN_INVITED = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 这名玩家已经被邀请到组队中了!
                        .key("floracore.command.misc.party.invite.has-been-invited").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_ALREADY_IN_THE_TEAM = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你已经在组队里了，必须离开当前组队！
                        .key("floracore.command.misc.party.already-in-the-team").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_ALREADY_JOINED_THE_TEAM = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你已经加入了这个队伍，不能重复加入!
                        .key("floracore.command.misc.party.already_joined_the_team").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_INVALID = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 这个组队不存在或已被解散!
                        .key("floracore.command.misc.party.invalid").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_NOT_INVITED = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你没有被邀请加入到这个组队中!
                        .key("floracore.command.misc.party.not-invited").color(RED).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args2<String, String> COMMAND_MISC_PARTY_INVITE = (sender, target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // {0} 已邀请 {1} 到组队中!他们有 {2} 秒时间接受邀请
                        .key("floracore.command.misc.party.invite").color(YELLOW)
                        // {}
                        .args(text(sender, GRAY), text(target, GRAY), text(60, RED)).append(AbstractMessage.FULL_STOP).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args2<String, UUID> COMMAND_MISC_PARTY_INVITE_ACCEPT = (sender, partyUUID) -> {
        ClickEvent clickEvent = ClickEvent.runCommand("/party accept " + partyUUID.toString());
        Component click = MiscMessage.CLICK_JOIN;
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // {0} 已经邀请你加入他们的组队。你有 {1} 秒的时间来接受。{2} 这里加入!
                        .key("floracore.command.misc.party.invite.accept").color(YELLOW)
                        // {}
                        .args(text(sender, GRAY), text(60, RED), click).clickEvent(clickEvent).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args0 COMMAND_MISC_PARTY_NOT_IN = () -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(), translatable()
                        // 你当前不在组队中
                        .key("floracore.command.misc.party.not-in").color(RED).append(AbstractMessage.FULL_STOP).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args1<String> COMMAND_MISC_PARTY_DISBAND = (sender) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                translatable()
                        // {0} 解散了组队!
                        .key("floracore.command.misc.party.disband").color(YELLOW)
                        // {}
                        .args(text(sender, GRAY)).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args1<String> COMMAND_MISC_PARTY_KICK = (target) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                translatable()
                        // {0} 被移出了组队
                        .key("floracore.command.misc.party.kick").color(YELLOW)
                        // {}
                        .args(text(target, GRAY)).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args1<String> COMMAND_MISC_PARTY_JOIN = (sender) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        return join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                translatable()
                        // {0} 加入了组队
                        .key("floracore.command.misc.party.join").append(AbstractMessage.FULL_STOP).color(YELLOW)
                        // {}
                        .args(text(sender, GRAY)).build(),
                MiscMessage.PARTY_HORIZONTAL_LINE.build());
    };

    Args2<String, String> COMMAND_MISC_PARTY_CHAT = (sender, message) -> text()
            .append(MiscMessage.PREFIX_PARTY).append(space())
            .append(text(sender, GRAY))
            .append(AbstractMessage.COLON.color(WHITE))
            .append(space())
            .append(text(message, WHITE))
            .build();
    Args2<String, String> COMMAND_MISC_STAFF_CHAT = (sender, message) -> text()
            .append(MiscMessage.PREFIX_STAFF).append(space())
            .append(text(sender, GRAY))
            .append(AbstractMessage.COLON.color(WHITE))
            .append(space())
            .append(text(message, WHITE))
            .build();

    Args2<String, String> COMMAND_MISC_BLOGGER_CHAT = (sender, message) -> text()
            .append(MiscMessage.PREFIX_BLOGGER).append(space())
            .append(text(sender, GRAY))
            .append(AbstractMessage.COLON.color(WHITE))
            .append(space())
            .append(text(message, WHITE))
            .build();

    Args2<String, String> COMMAND_MISC_BUILDER_CHAT = (sender, message) -> text()
            .append(MiscMessage.PREFIX_BUILDER).append(space())
            .append(text(sender, GRAY))
            .append(AbstractMessage.COLON.color(WHITE))
            .append(space())
            .append(text(message, WHITE))
            .build();

    Args2<String, String> COMMAND_MISC_ADMIN_CHAT = (sender, message) -> text()
            .append(MiscMessage.PREFIX_ADMIN).append(space())
            .append(text(sender, GRAY))
            .append(AbstractMessage.COLON.color(WHITE))
            .append(space())
            .append(text(message, WHITE))
            .build();
    Args3<UUID, List<UUID>, List<UUID>> COMMAND_MISC_PARTY_LIST = (leader, moderators, members) -> {
        JoinConfiguration joinConfig = JoinConfiguration.builder().separator(newline()).build();
        Component title = translatable("floracore.command.misc.party.list")
                .args(AbstractMessage.OPEN_BRACKET.append(text(members.size())).append(AbstractMessage.CLOSE_BRACKET)).color(GOLD);
        String leaderName = FloraCoreProvider.get().getPlayerAPI().getPlayerRecordName(leader);
        boolean leaderOnline = FloraCoreProvider.get().getPlayerAPI().isOnline(leader);
        Component leaderComponent = translatable("floracore.command.misc.party.leader")
                // {}
                .args(text(leaderName, GRAY).append(space()).append(AbstractMessage.CIRCLE.color(leaderOnline ? GREEN : RED))).color(YELLOW);
        Component c = join(joinConfig, MiscMessage.PARTY_HORIZONTAL_LINE.build(),
                title,
                space(),
                leaderComponent);
        members.remove(leader);
        if (!moderators.isEmpty()) {
            Component mc = Component.empty();
            for (UUID moderator : moderators) {
                String moderatorName = FloraCoreProvider.get().getPlayerAPI().getPlayerRecordName(moderator);
                boolean moderatorOnline = FloraCoreProvider.get().getPlayerAPI().isOnline(moderator);
                mc = mc.append(text(moderatorName, GRAY).append(space()).append(AbstractMessage.CIRCLE.color(moderatorOnline ? GREEN : RED))).append(space());
                members.remove(moderator);
            }
            c = join(joinConfig, c, space(),
                    translatable().key("floracore.command.misc.party.moderators")
                            // {}
                            .args(mc).color(YELLOW).build());
        }
        if (!members.isEmpty()) {
            Component mc = Component.empty();
            for (UUID member : members) {
                String memberName = FloraCoreProvider.get().getPlayerAPI().getPlayerRecordName(member);
                boolean memberOnline = FloraCoreProvider.get().getPlayerAPI().isOnline(member);
                mc = mc.append(text(memberName, GRAY).append(space()).append(AbstractMessage.CIRCLE.color(memberOnline ? GREEN : RED))).append(space());
            }
            c = join(joinConfig, c, space(),
                    translatable().key("floracore.command.misc.party.members")
                            // {}
                            .args(mc).color(YELLOW).build());
        }
        c = join(joinConfig, c, MiscMessage.PARTY_HORIZONTAL_LINE.build());
        return c;
    };
}
