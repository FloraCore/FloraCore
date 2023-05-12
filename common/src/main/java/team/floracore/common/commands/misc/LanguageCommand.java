package team.floracore.common.commands.misc;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.data.*;
import team.floracore.common.command.*;
import team.floracore.common.http.*;
import team.floracore.common.inevntory.*;
import team.floracore.common.inevntory.content.*;
import team.floracore.common.locale.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.util.*;

import java.io.*;
import java.util.*;

@CommandContainer
@CommandDescription("设置FloraCore的显示语言")
public class LanguageCommand extends AbstractFloraCoreCommand {
    public LanguageCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("language|lang")
    public void language(final @NonNull Player player) {
        getLanguageGui(player).open(player);
    }

    private SmartInventory getLanguageGui(Player player) {
        Sender s = getPlugin().getSenderFactory().wrap(player);
        UUID uuid = player.getUniqueId();
        List<TranslationRepository.LanguageInfo> availableTranslations = new ArrayList<>();
        try {
            availableTranslations = getPlugin().getTranslationRepository().getAvailableLanguages();
        } catch (IOException | UnsuccessfulRequestException e) {
            getPlugin().getLogger().warn("Unable to obtain a list of available translations", e);
        }
        Component title = TranslationManager.render(Message.COMMAND_LANGUAGE_TITLE.build(), uuid);
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.title(title);
        builder.closeable(true);
        builder.size(6, 9);
        List<TranslationRepository.LanguageInfo> finalAvailableTranslations = availableTranslations;
        builder.provider((player1, contents) -> {
            Pagination pagination = contents.pagination();
            ClickableItem[] items = new ClickableItem[finalAvailableTranslations.size() + 1];
            Locale defaultLanguage = TranslationManager.DEFAULT_LOCALE;
            String dpl = TranslationManager.localeDisplayName(defaultLanguage);
            Component dc = TranslationManager.render(Message.COMMAND_LANGUAGE_CHANGE.build(dpl));
            ItemBuilder dib = new ItemBuilder(Material.PAPER).displayName(Component.text(dpl).color(NamedTextColor.GREEN)).lore(dc);
            items[0] = ClickableItem.of(dib.build(), inventoryClickEvent -> {
                String value = defaultLanguage.toLanguageTag();
                getStorageImplementation().insertData(uuid, DataType.FUNCTION, "language", value.replace("-", "_"), 0);
                player.closeInventory();
                Message.COMMAND_LANGUAGE_CHANGE_SUCCESS.send(s, dpl);
            });
            for (int i = 1; i < items.length; i++) {
                TranslationRepository.LanguageInfo language = finalAvailableTranslations.get(i - 1);
                Locale l = language.locale();
                String pl = TranslationManager.localeDisplayName(l);
                Component c = TranslationManager.render(Message.COMMAND_LANGUAGE_CHANGE.build(pl), l);
                ItemBuilder itemBuilder = new ItemBuilder(Material.PAPER).displayName(Component.text(pl).color(NamedTextColor.GREEN)).lore(c);
                items[i] = ClickableItem.of(itemBuilder.build(), inventoryClickEvent -> {
                    String value = language.locale().toLanguageTag();
                    getStorageImplementation().insertData(uuid, DataType.FUNCTION, "language", value.replace("-", "_"), 0);
                    player.closeInventory();
                    Message.COMMAND_LANGUAGE_CHANGE_SUCCESS.send(s, pl);
                });
            }
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
