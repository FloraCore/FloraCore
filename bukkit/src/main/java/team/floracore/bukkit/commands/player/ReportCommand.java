package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import de.myzelyam.api.vanish.VanishAPI;
import me.huanmeng.opensource.bukkit.gui.GuiButton;
import me.huanmeng.opensource.bukkit.gui.button.Button;
import me.huanmeng.opensource.bukkit.gui.impl.GuiCustom;
import me.huanmeng.opensource.bukkit.gui.impl.GuiPage;
import me.huanmeng.opensource.bukkit.gui.impl.page.PageSettings;
import me.huanmeng.opensource.bukkit.gui.slot.Slot;
import me.huanmeng.opensource.bukkit.gui.slot.Slots;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.floracore.api.bukkit.messenger.message.type.NoticeMessage;
import org.floracore.api.commands.report.ReportStatus;
import org.floracore.api.data.DataType;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.MenuMessage;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.bukkit.util.itemstack.ItemStackBuilder;
import team.floracore.common.locale.message.AbstractMessage;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.locale.translation.TranslationManager;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.misc.floracore.tables.DATA;
import team.floracore.common.storage.misc.floracore.tables.REPORT;
import team.floracore.common.util.DurationFormatter;
import team.floracore.common.util.StringUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.space;

/**
 * Report命令
 */
@CommandDescription("floracore.command.description.report")
@CommandPermission("floracore.command.report")
public class ReportCommand extends FloraCoreBukkitCommand {
    private final List<REPORT> reports = new ArrayList<>();

    public ReportCommand(FCBukkitPlugin plugin) {
        super(plugin);
        reports.addAll(getStorageImplementation().getReports());
        plugin.getBootstrap().getScheduler().asyncRepeating(() -> {
            reports.clear();
            reports.addAll(getStorageImplementation().getReports());
        }, 3, TimeUnit.SECONDS);
    }

    @CommandMethod("report-tp <target>")
    @CommandDescription("floracore.command.description.report.tp")
    @CommandPermission("floracore.command.report.staff")
    public void reportTeleport(final @NotNull Player sender, final @Argument("target") String target) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        PlayerCommandMessage.COMMAND_REPORT_TP_TRANSMITTING.send(s);
        UUID u = sender.getUniqueId();
        UUID ut = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
        if (ut == null) {
            MiscMessage.PLAYER_NOT_FOUND.send(s, target);
            return;
        }
        String server;
        DATA data = getStorageImplementation().getSpecifiedData(ut, DataType.FUNCTION, "server-status");
        if (data != null) {
            server = data.getValue();
        } else {
            MiscMessage.PLAYER_NOT_FOUND.send(s, target);
            return;
        }
        if (getPlugin().getServerName().equalsIgnoreCase(server)) {
            Player t = Bukkit.getPlayer(target);
            if (t != null) {
                if (getPlugin().isPluginInstalled("PremiumVanish")) {
                    if (!VanishAPI.isInvisible(sender)) {
                        VanishAPI.hidePlayer(sender);
                    }
                }
                sender.teleport(t.getLocation());
                PlayerCommandMessage.COMMAND_REPORT_TP_SUCCESS.send(s, target);
            } else {
                MiscMessage.PLAYER_NOT_FOUND.send(s, target);
            }
        } else {
            getPlugin().getBukkitMessagingFactory().pushTeleport(u, ut, server);
            getPlugin().getBungeeUtil().connect(sender, server);
        }
    }

    @CommandMethod("report <target> <reason>")
    @CommandDescription("floracore.command.description.report.target")
    public void report(final @NotNull Player sender,
                       final @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target,
                       final @NotNull @Argument("reason") @Greedy String reason) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Player t = Bukkit.getPlayer(target);
        UUID reportedUser;
        if (t != null) {
            if (t.getUniqueId() == sender.getUniqueId()) {
                PlayerCommandMessage.COMMAND_REPORT_SELF.send(s);
                return;
            }
            reportedUser = t.getUniqueId();
        } else {
            UUID ut = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
            if (ut == null) {
                MiscMessage.PLAYER_NOT_FOUND.send(s, target);
                return;
            }
            if (hasPermission(ut, "floracore.command.report.bypass")) {
                PlayerCommandMessage.COMMAND_REPORT_NOT_PERMISSION.send(s);
                return;
            }
            reportedUser = ut;
        }
        final String reporterServer = getPlugin().getServerName();
        DATA data = getStorageImplementation().getSpecifiedData(reportedUser, DataType.FUNCTION, "server-status");
        final String reportedUserServer;
        if (data != null) {
            reportedUserServer = data.getValue();
        } else {
            PlayerCommandMessage.COMMAND_REPORT_ABNORMAL.send(s);
            return;
        }
        REPORT report = getStorageImplementation().getUnprocessedReports(reportedUser);
        if (report != null) {
            if (report.getReporters().contains(s.getUniqueId())) {
                PlayerCommandMessage.COMMAND_REPORT_REPEAT.send(s);
                return;
            }
        }
        PlayerCommandMessage.COMMAND_REPORT_SUCCESS.send(s, target, reason);
        createReport(s.getUniqueId(), reportedUser, reporterServer, reportedUserServer, reason);
    }

    private void createReport(UUID reporter,
                              UUID reportedUser,
                              String reporterServer,
                              String reportedUserServer,
                              String reason) {
        getAsyncExecutor().execute(() -> {
            UUID uuid = UUID.randomUUID();
            long time = System.currentTimeMillis();
            getStorageImplementation().addReport(uuid, reporter, reportedUser, reason, time);
            getPlugin().getBukkitMessagingFactory()
                    .pushReport(reporter, reportedUser, reporterServer, reportedUserServer, reason);
        });
    }

    @CommandMethod("reports")
    @CommandDescription("floracore.command.description.reports")
    @CommandPermission("floracore.command.report.staff")
    public void reports(final @NotNull Player player) {
        Sender s = getPlugin().getSenderFactory().wrap(player);
        getReportsMainGui(player, false).openGui();
    }

    private GuiPage getReportsMainGui(Player player, boolean conclusion) {
        UUID uuid = player.getUniqueId();
        Component title;
        List<REPORT> filteredReports;
        if (conclusion) {
            filteredReports = reports.stream()
                    .filter(report -> report.getStatus() == ReportStatus.ENDED)
                    .sorted(Comparator.comparingInt(REPORT::getId))
                    .collect(Collectors.toList());
            title = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_TITLE.build(), uuid)
                    .append(space())
                    .append(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_PROCESSED.build(),
                            uuid));
        } else {
            filteredReports = reports.stream()
                    .filter(report -> report.getStatus() != ReportStatus.ENDED)
                    .sorted(Comparator.comparingInt(REPORT::getId))
                    .collect(Collectors.toList());
            title = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_TITLE.build(), uuid);
        }
        List<Button> buttons = new ArrayList<>();
        for (REPORT report : filteredReports) {
            int id = report.getId();
            Button d = Button.of(p -> {
                Component rt = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_TITLE.build(id), uuid);
                List<Component> lore = getReportLore(report, uuid);
                lore.add(Component.space());
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_CLICK_TO_LOOK.build(), uuid));
                ItemStackBuilder ri = new ItemStackBuilder(Material.PAPER).setName(rt).setLore(lore);
                if (report.getStatus() == ReportStatus.ACCEPTED) {
                    ri.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).setHideEnchants(true);
                }
                return ri.get();
            }, p -> getReportGui(player, report.getUniqueId(), conclusion).openGui());
            buttons.add(d);
        }
        Slots LINE = Slots.pattern(new String[]{
                "---------",
                "---------",
                "xxxxxxxxx",
                "xxxxxxxxx",
                "xxxxxxxxx",
                "---------"
        }, 'x');
        GuiPage gui = new GuiPage(player, buttons, 27, LINE);
        gui.title(title);
        gui.setPlayer(player);
        gui.tick(20);
        gui.addTick(g -> g.refresh(true));
        Button b1 = Button.of(p -> {
            Component t = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_PAGE.build(gui.page()),
                    uuid);
            return new ItemStackBuilder(Material.BOOKSHELF).setName(title)
                    .setLore(Collections.singletonList(t)).get();
        });
        GuiButton gb1 = new GuiButton(Slot.ofGame(5, 1), b1);
        gui.addAttachedButton(gb1);
        supplementaryMenu(gui);
        if (conclusion) {
            Button b2 = Button.of(p -> {
                Component back = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_BACK.build(), uuid);
                return new ItemStackBuilder(Material.ARROW).setName(back).get();
            }, p -> getReportsMainGui(player, false).openGui());
            GuiButton gb2 = new GuiButton(Slot.ofGame(6, 6), b2);
            gui.addAttachedButton(gb2);
        } else {
            Button b2 = Button.of(p -> {
                Component t1 = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_PROCESSED.build(), uuid);
                return new ItemStackBuilder(Material.CHEST).setName(t1).get();
            }, p -> getReportsMainGui(player, true).openGui());
            GuiButton gb2 = new GuiButton(Slot.ofGame(6, 6), b2);
            gui.addAttachedButton(gb2);
        }
        setPageSlot(uuid, gui);
        Button b2 = Button.of(p -> {
            Component close = TranslationManager.render(MenuMessage.COMMAND_MISC_GUI_CLOSE.build(), uuid);
            return new ItemStackBuilder(Material.BARRIER).setName(close).get();
        }, HumanEntity::closeInventory);
        GuiButton gb2 = new GuiButton(Slot.ofGame(5, 6), b2);
        gui.addAttachedButton(gb2);
        return gui;
    }

    private void setPageSlot(UUID uuid, GuiPage gui) {
        Button bp = Button.of(p -> {
            Component previous =
                    TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_PREVIOUS_PAGE.build(), uuid);
            Component turn =
                    TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_TURN_TO_PAGE.build(gui.page() - 1), uuid);
            return new ItemStackBuilder(Material.ARROW).setName(previous)
                    .setLore(Collections.singletonList(turn))
                    .get();
        });
        Button bn = Button.of(p -> {
            Component next =
                    TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_NEXT_PAGE.build(), uuid);
            Component turn1 =
                    TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_TURN_TO_PAGE.build(gui.page() + 1),
                            uuid);
            ItemStack n = new ItemStackBuilder(Material.ARROW).setName(next)
                    .setLore(Collections.singletonList(turn1))
                    .get();
            return n;
        });
        gui.pageSetting(PageSettings.normal(gui, bp, bn));
    }

    private GuiCustom getReportGui(Player player, UUID reportUUID, boolean conclusion) {
        UUID uuid = player.getUniqueId();
        REPORT report = getStorageImplementation().selectReport(reportUUID);
        Component title = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_REPORT_TITLE.build(), uuid);
        Component finalTitle = title.append(Component.space())
                .append(AbstractMessage.ARROW.color(NamedTextColor.GRAY))
                .append(Component.space())
                .append(Component.text("#" + report.getId()).color(NamedTextColor.RED));
        GuiCustom gui = new GuiCustom(player);
        gui.title(finalTitle);
        gui.line(6);
        Button button = Button.of(p -> {
            ItemStackBuilder i1 = new ItemStackBuilder(Material.PAPER).setName(finalTitle)
                    .setLore(getReportLore(report, uuid));
            i1.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).setHideEnchants(true);
            return i1.get();
        });
        GuiButton guiButton = new GuiButton(Slot.ofGame(5, 1), button);
        gui.addAttachedButton(guiButton);
        String resultRns = getReports(report);
        Button b1 = Button.of(p -> {
            ItemStack rs = getPlayerItemStackBuilder(report.getReporters().get(0)).setName(TranslationManager.render(
                            MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTER.build(resultRns),
                            uuid))
                    .setLore(Collections.singletonList(
                            TranslationManager.render(
                                    MenuMessage.COMMAND_REPORTS_CLICK_TO_LOOK.build(),
                                    uuid)))
                    .get();
            return rs;
        }, p -> getReportersGUI(player, reportUUID, conclusion).openGui());
        GuiButton gb1 = new GuiButton(Slot.ofGame(4, 3), b1);
        gui.addAttachedButton(gb1);
        String r1 = getPlayerRecordName(report.getReported());
        if (r1 == null) {
            r1 = "UNKNOWN";
        }
        boolean online = isOnline(report.getReported());
        ItemStackBuilder rds = getPlayerItemStackBuilder(report.getReported()).setName(TranslationManager.render(
                MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTED.build(r1, online),
                uuid));
        if (online) {
            String finalR = r1;
            Button b2 = Button.of(p -> {
                rds.setLore(Collections.singletonList(TranslationManager.render(MiscMessage.CLICK_TP, uuid)));
                return rds.get();
            }, p -> {
                reportTeleport(player, finalR);
                player.closeInventory();
            });
            GuiButton gb2 = new GuiButton(Slot.ofGame(6, 3), b2);
            gui.addAttachedButton(gb2);
        } else {
            Button b2 = Button.of(p -> rds.get());
            GuiButton gb2 = new GuiButton(Slot.ofGame(6, 3), b2);
            gui.addAttachedButton(gb2);
        }
        switch (report.getStatus()) {
            case WAITING:
                Button b3 = Button.of(p -> {
                    Component accepted = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_REPORT_ACCEPTED.build(),
                            uuid);
                    return ItemStackBuilder.limeStainedGlassPane().setName(accepted).get();
                }, p -> {
                    report.setStatus(ReportStatus.ACCEPTED);
                    getReportGui(player, reportUUID, conclusion).openGui();
                    String reported = getPlayerRecordName(report.getReported());
                    for (UUID reporter : report.getReporters()) {
                        getPlugin().getBukkitMessagingFactory().pushNoticeMessage(reporter,
                                NoticeMessage.NoticeType.REPORT_ACCEPTED,
                                Collections.singletonList(reported));
                    }
                    getPlugin().getBukkitMessagingFactory().pushNoticeMessage(UUID.randomUUID(),
                            NoticeMessage.NoticeType.REPORT_STAFF_ACCEPTED,
                            Arrays.asList(resultRns, reported));
                });
                GuiButton gb3 = new GuiButton(Slot.ofGame(5, 4), b3);
                gui.addAttachedButton(gb3);
                break;
            case ACCEPTED:
                Button b4 = Button.of(p -> {
                    Component end = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_REPORT_END.build(), uuid);
                    ItemStackBuilder ei = ItemStackBuilder.lightBlueStainedGlassPane().setName(end);
                    ei.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).setHideEnchants(true);
                    return ei.get();
                }, p -> {
                    report.setStatus(ReportStatus.ENDED);
                    report.setConclusionTime(System.currentTimeMillis());
                    getReportGui(player, reportUUID, conclusion).openGui();
                    String reported = getPlayerRecordName(report.getReported());
                    for (UUID reporter : report.getReporters()) {
                        getPlugin().getBukkitMessagingFactory().pushNoticeMessage(reporter,
                                NoticeMessage.NoticeType.REPORT_PROCESSED,
                                Collections.singletonList(reported));
                    }
                    getPlugin().getBukkitMessagingFactory().pushNoticeMessage(UUID.randomUUID(),
                            NoticeMessage.NoticeType.REPORT_STAFF_PROCESSED,
                            Arrays.asList(resultRns, reported));
                });
                GuiButton gb4 = new GuiButton(Slot.ofGame(5, 4), b4);
                gui.addAttachedButton(gb4);
                break;
            case ENDED:
                Button b5 = Button.of(p -> {
                    Component ended = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_REPORT_ENDED.build(),
                            uuid);
                    return ItemStackBuilder.redStainedGlassPane().setName(ended).get();
                });
                GuiButton gb5 = new GuiButton(Slot.ofGame(5, 4), b5);
                gui.addAttachedButton(gb5);
                break;
        }
        supplementaryMenu(gui);
        Button b6 = Button.of(p -> {
            Component back = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_BACK.build(), uuid);
            return new ItemStackBuilder(Material.ARROW).setName(back).get();
        }, p -> getReportsMainGui(player, conclusion).openGui());
        GuiButton gb6 = new GuiButton(Slot.ofGame(9, 6), b6);
        gui.addAttachedButton(gb6);
        Button b7 = Button.of(p -> {
            Component close = TranslationManager.render(MenuMessage.COMMAND_MISC_GUI_CLOSE.build(), uuid);
            return new ItemStackBuilder(Material.BARRIER).setName(close).get();
        }, HumanEntity::closeInventory);
        GuiButton gb7 = new GuiButton(Slot.ofGame(5, 6), b7);
        gui.addAttachedButton(gb7);
        return gui;
    }

    private GuiPage getReportersGUI(Player player, UUID reportUUID, boolean conclusion) {
        UUID uuid = player.getUniqueId();
        REPORT report = getStorageImplementation().selectReport(reportUUID);
        Component title = TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTER_TITLE.build(), uuid);
        Component finalTitle = title.append(Component.space())
                .append(AbstractMessage.ARROW.color(NamedTextColor.GRAY))
                .append(Component.space())
                .append(Component.text("#" + report.getId()).color(NamedTextColor.RED));
        List<Button> buttons = new ArrayList<>();
        List<UUID> reporters = report.getReporters();
        for (UUID reporter : reporters) {
            String r1 = getPlayerRecordName(reporter);
            if (r1 == null) {
                r1 = "UNKNOWN";
            }
            boolean online = isOnline(reporter);
            ItemStackBuilder rds = getPlayerItemStackBuilder(reporter).setName(TranslationManager.render(
                    MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTER_DETAILED.build(r1, online),
                    uuid));
            if (online) {
                rds.setLore(Collections.singletonList(TranslationManager.render(MiscMessage.CLICK_TP, uuid)));
                String finalR = r1;
                Button button = Button.of(p -> rds.get(), p -> {
                    reportTeleport(player, finalR);
                    player.closeInventory();
                });
                buttons.add(button);
            } else {
                Button button = Button.of(p -> rds.get());
                buttons.add(button);
            }
        }
        Slots LINE = Slots.pattern(new String[]{
                "---------",
                "---------",
                "xxxxxxxxx",
                "xxxxxxxxx",
                "xxxxxxxxx",
                "---------"
        }, 'x');
        GuiPage gui = new GuiPage(player, buttons, 18, LINE);
        gui.title(finalTitle);
        gui.setPlayer(player);
        Button b = Button.of(p -> {
            ItemStackBuilder i1 = new ItemStackBuilder(Material.PAPER).setName(finalTitle)
                    .setLore(getReportLore(report, uuid));
            i1.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).setHideEnchants(true);
            return i1.get();
        });
        GuiButton gb = new GuiButton(Slot.ofGame(5, 1), b);
        gui.addAttachedButton(gb);
        supplementaryMenu(gui);
        setPageSlot(uuid, gui);
        Button b1 = Button.of(p -> {
            Component back = TranslationManager.render(MiscMessage.COMMAND_MISC_GUI_BACK.build(), uuid);
            return new ItemStackBuilder(Material.ARROW).setName(back).get();
        }, p -> getReportGui(player, reportUUID, conclusion).openGui());
        GuiButton gb1 = new GuiButton(Slot.ofGame(6, 6), b1);
        gui.addAttachedButton(gb1);
        Button b2 = Button.of(p -> {
            Component close = TranslationManager.render(MenuMessage.COMMAND_MISC_GUI_CLOSE.build(), uuid);
            return new ItemStackBuilder(Material.BARRIER).setName(close).get();
        }, HumanEntity::closeInventory);
        GuiButton gb2 = new GuiButton(Slot.ofGame(5, 6), b2);
        gui.addAttachedButton(gb2);
        return gui;
    }

    private ItemStackBuilder getPlayerItemStackBuilder(UUID uuid) {
        return ItemStackBuilder.questionMark();
    }

    private String getReports(REPORT report) {
        List<String> rns = new ArrayList<>();
        for (UUID reporter : report.getReporters()) {
            String name = getPlayerRecordName(reporter);
            if (name != null) {
                rns.add(name);
            }
        }
        return StringUtil.joinList(rns, 3);
    }

    private void supplementaryMenu(GuiCustom gui) {
        Slots LINE = Slots.pattern(new String[]{
                "---------",
                "xxxxxxxxx",
                "---------",
                "---------",
                "---------",
                "xxxxxxxxx",
        }, 'x');
        ItemStack empty = ItemStackBuilder.grayStainedGlassPane().setName(Component.space()).get();
        Button button = Button.of(p -> empty);
        gui.draw().set(LINE, button);
    }

    private List<Component> getReportLore(REPORT report, UUID uuid) {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.space());
        switch (report.getStatus()) {
            case WAITING:
                Component waiting = TranslationManager.render(MenuMessage.COMMAND_REPORTS_STATUS_WAITING.build(),
                        uuid);
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_STATUS.build(waiting),
                        uuid));
                break;
            case ACCEPTED:
                Component accepted = TranslationManager.render(MenuMessage.COMMAND_REPORTS_STATUS_ACCEPTED.build(),
                        uuid);
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_STATUS.build(accepted),
                        uuid));
                break;
            case ENDED:
                Component ended = TranslationManager.render(MenuMessage.COMMAND_REPORTS_STATUS_ENDED.build(), uuid);
                lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_STATUS.build(ended),
                        uuid));
                break;
        }
        lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORT_TIME.build(DurationFormatter.getTimeFromTimestamp(
                report.getReportTime())), uuid));
        lore.add(Component.space());
        String resultRns = getReports(report);
        lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTER.build(resultRns), uuid));
        String reported = getPlayerRecordName(report.getReported());
        if (reported == null) {
            reported = "UNKNOWN";
        }
        boolean online = isOnline(report.getReported());
        lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REPORTED.build(reported, online),
                uuid));
        String resultReason = StringUtil.joinList(report.getReasons(), 2);
        lore.add(TranslationManager.render(MenuMessage.COMMAND_REPORTS_GUI_MAIN_REASON.build(resultReason), uuid));
        return lore;
    }
}
