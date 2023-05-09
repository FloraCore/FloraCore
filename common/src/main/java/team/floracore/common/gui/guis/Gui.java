package team.floracore.common.gui.guis;

import org.jetbrains.annotations.*;
import team.floracore.common.gui.builder.gui.*;
import team.floracore.common.gui.components.*;

import java.util.*;

/**
 * Standard GUI implementation of {@link BaseGui}
 */
public class Gui extends BaseGui {

    /**
     * Main constructor for the GUI
     *
     * @param rows                 The amount of rows the GUI should have
     * @param title                The GUI's title using {@link String}
     * @param interactionModifiers A set containing the {@link InteractionModifier} this GUI should use
     * @author SecretX
     * @since 3.0.3
     */
    public Gui(final int rows, @NotNull final String title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        super(rows, title, interactionModifiers);
    }

    /**
     * Alternative constructor that takes both a {@link GuiType} and a set of {@link InteractionModifier}
     *
     * @param guiType              The {@link GuiType} to be used
     * @param title                The GUI's title using {@link String}
     * @param interactionModifiers A set containing the {@link InteractionModifier} this GUI should use
     * @author SecretX
     * @since 3.0.3
     */
    public Gui(@NotNull final GuiType guiType, @NotNull final String title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        super(guiType, title, interactionModifiers);
    }

    /**
     * Old main constructor for the GUI
     *
     * @param rows  The amount of rows the GUI should have
     * @param title The GUI's title
     * @deprecated In favor of {@link Gui#Gui(int, String, Set)}
     */
    @Deprecated
    public Gui(final int rows, @NotNull final String title) {
        super(rows, title);
    }

    /**
     * Alternative constructor that defaults to 1 row
     *
     * @param title The GUI's title
     * @deprecated In favor of {@link Gui#Gui(int, String, Set)}
     */
    @Deprecated
    public Gui(@NotNull final String title) {
        super(1, title);
    }

    /**
     * Main constructor that takes a {@link GuiType} instead of rows
     *
     * @param guiType The {@link GuiType} to be used
     * @param title   The GUI's title
     * @deprecated In favor of {@link Gui#Gui(GuiType, String, Set)}
     */
    @Deprecated
    public Gui(@NotNull final GuiType guiType, @NotNull final String title) {
        super(guiType, title);
    }

    /**
     * Creates a {@link SimpleBuilder} to build a {@link dev.triumphteam.gui.guis.Gui}
     *
     * @param type The {@link GuiType} to be used
     * @return A {@link SimpleBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract("_ -> new")
    public static SimpleBuilder gui(@NotNull final GuiType type) {
        return new SimpleBuilder(type);
    }

    /**
     * Creates a {@link SimpleBuilder} with CHEST as the {@link GuiType}
     *
     * @return A CHEST {@link SimpleBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract(" -> new")
    public static SimpleBuilder gui() {
        return gui(GuiType.CHEST);
    }

    /**
     * Creates a {@link StorageBuilder}.
     *
     * @return A CHEST {@link StorageBuilder}.
     * @since 3.0.0.
     */
    @NotNull
    @Contract(" -> new")
    public static StorageBuilder storage() {
        return new StorageBuilder();
    }

    /**
     * Creates a {@link PaginatedBuilder} to build a {@link PaginatedGui}
     *
     * @return A {@link PaginatedBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract(" -> new")
    public static PaginatedBuilder paginated() {
        return new PaginatedBuilder();
    }

    /**
     * Creates a {@link ScrollingBuilder} to build a {@link ScrollingGui}
     *
     * @param scrollType The {@link ScrollType} to be used by the GUI
     * @return A {@link ScrollingBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract("_ -> new")
    public static ScrollingBuilder scrolling(@NotNull final ScrollType scrollType) {
        return new ScrollingBuilder(scrollType);
    }

    /**
     * Creates a {@link ScrollingBuilder} with VERTICAL as the {@link ScrollType}
     *
     * @return A vertical {@link SimpleBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract(" -> new")
    public static ScrollingBuilder scrolling() {
        return scrolling(ScrollType.VERTICAL);
    }

}
