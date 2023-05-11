package team.floracore.common.gui.builder.gui;

import org.jetbrains.annotations.*;
import team.floracore.common.gui.components.*;
import team.floracore.common.gui.components.util.*;
import team.floracore.common.gui.guis.*;

import java.util.function.*;

/**
 * The simple GUI builder is used for creating a {@link Gui}
 */
public final class SimpleBuilder extends BaseGuiBuilder<Gui, SimpleBuilder> {

    private GuiType guiType;

    /**
     * Main constructor
     *
     * @param guiType The {@link GuiType} to default to
     */
    public SimpleBuilder(@NotNull final GuiType guiType) {
        this.guiType = guiType;
    }

    /**
     * Sets the {@link GuiType} to use on the GUI
     * This method is unique to the simple GUI
     *
     * @param guiType The {@link GuiType}
     * @return The current builder
     */
    @NotNull
    @Contract("_ -> this")
    public SimpleBuilder type(@NotNull final GuiType guiType) {
        this.guiType = guiType;
        return this;
    }

    /**
     * Creates a new {@link Gui}
     *
     * @return A new {@link Gui}
     */
    @NotNull
    @Override
    @Contract(" -> new")
    public Gui create() {
        final Gui gui;
        final String title = Legacy.SERIALIZER.serialize(getTitle());
        if (guiType == null || guiType == GuiType.CHEST) {
            gui = new Gui(getRows(), title, getModifiers());
        } else {
            gui = new Gui(guiType, title, getModifiers());
        }

        final Consumer<Gui> consumer = getConsumer();
        if (consumer != null) consumer.accept(gui);

        return gui;
    }

}
