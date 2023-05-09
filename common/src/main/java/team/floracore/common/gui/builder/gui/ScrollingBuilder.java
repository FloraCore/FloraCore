package team.floracore.common.gui.builder.gui;

import net.kyori.adventure.text.*;
import org.jetbrains.annotations.*;
import team.floracore.common.gui.components.*;
import team.floracore.common.gui.components.util.*;
import team.floracore.common.gui.guis.*;

import java.util.function.*;

/**
 * The simple GUI builder is used for creating a {@link ScrollingGui} that uses {@link Component} for title
 * TODO This class needs more work to remove the redundant pageSize since it's the same as the paginated builder
 */
public final class ScrollingBuilder extends BaseGuiBuilder<ScrollingGui, ScrollingBuilder> {

    private ScrollType scrollType;
    private int pageSize = 0;

    /**
     * Main constructor
     *
     * @param scrollType The {@link ScrollType} to default to
     */
    public ScrollingBuilder(@NotNull final ScrollType scrollType) {
        this.scrollType = scrollType;
    }

    /**
     * Sets the {@link ScrollType} to be used
     *
     * @param scrollType Either horizontal or vertical scrolling
     * @return The current builder
     */
    @NotNull
    @Contract("_ -> this")
    public ScrollingBuilder scrollType(@NotNull final ScrollType scrollType) {
        this.scrollType = scrollType;
        return this;
    }

    /**
     * Sets the desirable page size, most of the times this isn't needed
     *
     * @param pageSize The amount of free slots that page items should occupy
     * @return The current builder
     */
    @NotNull
    @Contract("_ -> this")
    public ScrollingBuilder pageSize(final int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Creates a new {@link ScrollingGui}
     *
     * @return A new {@link ScrollingGui}
     */
    @NotNull
    @Override
    @Contract(" -> new")
    public ScrollingGui create() {
        final ScrollingGui gui = new ScrollingGui(getRows(), pageSize, Legacy.SERIALIZER.serialize(getTitle()), scrollType, getModifiers());

        final Consumer<ScrollingGui> consumer = getConsumer();
        if (consumer != null) consumer.accept(gui);

        return gui;
    }

}
