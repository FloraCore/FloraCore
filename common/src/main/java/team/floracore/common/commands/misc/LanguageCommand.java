package team.floracore.common.commands.misc;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import net.kyori.adventure.text.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.command.*;
import team.floracore.common.inevntory.*;
import team.floracore.common.inevntory.content.*;
import team.floracore.common.locale.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.plugin.*;
import team.floracore.common.util.*;

import java.util.*;

@CommandContainer
@CommandDescription("设置FloraCore的显示语言")
public class LanguageCommand extends AbstractFloraCoreCommand {
    public LanguageCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("language")
    public void language(final @NonNull Player player) {
        getLanguageGui(player).open(player);
    }

    private SmartInventory getLanguageGui(Player player) {
        UUID uuid = player.getUniqueId();
        Component title = TranslationManager.render(Message.COMMAND_REPORTS_GUI_MAIN_TITLE.build(), uuid);
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.title(title);
        builder.closeable(true);
        builder.size(6, 9);
        builder.provider((player1, contents) -> {
            Pagination pagination = contents.pagination();
            ClickableItem[] items = new ClickableItem[54];
            for (int i = 0; i < items.length; i++)
                items[i] = ClickableItem.empty(new ItemStack(Material.STONE, i));
            pagination.setItems(items);
            pagination.setItemsPerPage(16);
            int i = 8;
            for (ClickableItem pageItem : pagination.getPageItems()) {
                i = i + 2;
                if (i % 9 == 0) {
                    i++;
                }
                contents.set(SmartInventory.getInventoryRow(i), SmartInventory.getInventoryColumn(i) + 1, pageItem);
            }
            if (!pagination.isFirst()) {
                Component previous = TranslationManager.render(Message.COMMAND_MISC_GUI_PREVIOUS_PAGE.build(), uuid);
                Component turn = TranslationManager.render(Message.COMMAND_MISC_GUI_TURN_TO_PAGE.build(pagination.getPage()), uuid);
                contents.set(5, 0, ClickableItem.of(new ItemBuilder(Material.ARROW).displayName(previous).lore(turn).build(), event -> getLanguageGui(player).open(player, pagination.previous().getPage())));
            }
            if (!pagination.isLast()) {
                Component next = TranslationManager.render(Message.COMMAND_MISC_GUI_NEXT_PAGE.build(), uuid);
                Component turn = TranslationManager.render(Message.COMMAND_MISC_GUI_TURN_TO_PAGE.build(pagination.getPage() + 2), uuid);
                contents.set(5, 8, ClickableItem.of(new ItemBuilder(Material.ARROW).displayName(next).lore(turn).build(), event -> getLanguageGui(player).open(player, pagination.next().getPage())));
            }
            Component close = TranslationManager.render(Message.COMMAND_MISC_GUI_CLOSE.build(), uuid);
            contents.set(5, 4, ClickableItem.of(new ItemBuilder(Material.BARRIER).displayName(close).build(), event -> player.closeInventory()));
        });
        return builder.build();
    }
}
