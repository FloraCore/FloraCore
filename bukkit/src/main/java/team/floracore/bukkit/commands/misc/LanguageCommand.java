package team.floracore.bukkit.commands.misc;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.floracore.api.data.*;
import org.jetbrains.annotations.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.command.*;
import team.floracore.bukkit.inevntory.*;
import team.floracore.bukkit.inevntory.content.*;
import team.floracore.bukkit.locale.message.*;
import team.floracore.bukkit.locale.message.commands.*;
import team.floracore.bukkit.util.itemstack.*;
import team.floracore.common.http.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.sender.*;

import java.io.*;
import java.util.*;

@CommandContainer
@CommandDescription("设置FloraCore的显示语言")
public class LanguageCommand extends FloraCoreBukkitCommand {
    public LanguageCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("language|lang")
    public void language(final @NotNull Player player) {
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
        Component title = TranslationManager.render(MenuMessage.COMMAND_LANGUAGE_TITLE.build(), uuid);
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
            Component dc = TranslationManager.render(MenuMessage.COMMAND_LANGUAGE_CHANGE.build(dpl));
            ItemStackBuilder dib = new ItemStackBuilder(Material.PAPER).setName(Component.text(dpl)
                            .color(NamedTextColor.GREEN))
                    .setLore(Collections.singletonList(dc));
            items[0] = ClickableItem.of(dib.get(), inventoryClickEvent -> {
                String value = defaultLanguage.toLanguageTag();
                getStorageImplementation().insertData(uuid, DataType.FUNCTION, "language", value.replace("-", "_"), 0);
                player.closeInventory();
                MiscCommandMessage.COMMAND_LANGUAGE_CHANGE_SUCCESS.send(s, dpl);
            });
            for (int i = 1; i < items.length; i++) {
                TranslationRepository.LanguageInfo language = finalAvailableTranslations.get(i - 1);
                Locale l = language.locale();
                String pl = TranslationManager.localeDisplayName(l);
                Component c = TranslationManager.render(MenuMessage.COMMAND_LANGUAGE_CHANGE.build(pl), l);
                Component t = Component.text(pl).color(NamedTextColor.GREEN);
                Component progress = AbstractMessage.OPEN_BRACKET.append(Component.text(language.progress() + "%"))
                        .append(AbstractMessage.CLOSE_BRACKET);
                t = t.append(Component.space()).append(progress.color(NamedTextColor.GRAY));
                ItemStackBuilder itemBuilder = new ItemStackBuilder(Material.PAPER).setName(t)
                        .setLore(Collections.singletonList(c));
                items[i] = ClickableItem.of(itemBuilder.get(), inventoryClickEvent -> {
                    String value = language.locale().toLanguageTag();
                    getStorageImplementation().insertData(uuid,
                            DataType.FUNCTION,
                            "language",
                            value.replace("-", "_"),
                            0);
                    player.closeInventory();
                    MiscCommandMessage.COMMAND_LANGUAGE_CHANGE_SUCCESS.send(s, pl);
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
                Component previous = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_PREVIOUS_PAGE.build(),
                        uuid);
                Component turn = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_TURN_TO_PAGE.build(pagination.getPage()),
                        uuid);
                contents.set(5,
                        0,
                        ClickableItem.of(new ItemStackBuilder(Material.ARROW).setName(previous)
                                        .setLore(Collections.singletonList(turn))
                                        .get(),
                                event -> getLanguageGui(player).open(player, pagination.previous().getPage())));
            }
            if (!pagination.isLast()) {
                Component next = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_NEXT_PAGE.build(), uuid);
                Component turn = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_TURN_TO_PAGE.build(pagination.getPage() + 2),
                        uuid);
                contents.set(5,
                        8,
                        ClickableItem.of(new ItemStackBuilder(Material.ARROW).setName(next)
                                        .setLore(Collections.singletonList(turn))
                                        .get(),
                                event -> getLanguageGui(player).open(player, pagination.next().getPage())));
            }
            Component close = TranslationManager.render(MenuMessage.COMMAND_MISC_GUI_CLOSE.build(), uuid);
            contents.set(5,
                    4,
                    ClickableItem.of(new ItemStackBuilder(Material.BARRIER).setName(close).get(),
                            event -> player.closeInventory()));
        });
        return builder.build();
    }
}
