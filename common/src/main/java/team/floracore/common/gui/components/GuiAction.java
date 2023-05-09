package team.floracore.common.gui.components;

import org.bukkit.event.*;

@FunctionalInterface
public interface GuiAction<T extends Event> {

    /**
     * Executes the event passed to it
     *
     * @param event Inventory action
     */
    void execute(final T event);

}
