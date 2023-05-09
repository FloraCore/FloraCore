package team.floracore.common.gui.builder.gui;

import org.jetbrains.annotations.*;
import team.floracore.common.gui.components.util.*;
import team.floracore.common.gui.guis.*;

import java.util.function.*;

/**
 * The simple GUI builder is used for creating a {@link StorageGui}
 */
public final class StorageBuilder extends BaseGuiBuilder<StorageGui, StorageBuilder> {

    /**
     * Creates a new {@link StorageGui}
     *
     * @return A new {@link StorageGui}
     */
    @NotNull
    @Override
    @Contract(" -> new")
    public StorageGui create() {
        final StorageGui gui = new StorageGui(getRows(), Legacy.SERIALIZER.serialize(getTitle()), getModifiers());

        final Consumer<StorageGui> consumer = getConsumer();
        if (consumer != null) consumer.accept(gui);

        return gui;
    }

}
