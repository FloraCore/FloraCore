package team.floracore.common.gui.builder.gui;

import net.kyori.adventure.text.*;
import org.jetbrains.annotations.*;
import team.floracore.common.gui.components.*;
import team.floracore.common.gui.components.exception.*;
import team.floracore.common.gui.guis.*;

import java.util.*;
import java.util.function.*;

/**
 * The base for all the GUI builders this is due to some limitations
 * where some builders will have unique features based on the GUI type
 *
 * @param <G> The Type of {@link BaseGui}
 */
@SuppressWarnings("unchecked")
public abstract class BaseGuiBuilder<G extends BaseGui, B extends BaseGuiBuilder<G, B>> {

    private final EnumSet<InteractionModifier> interactionModifiers = EnumSet.noneOf(InteractionModifier.class);
    private Component title = null;
    private int rows = 1;
    private Consumer<G> consumer;

    /**
     * Sets the rows for the GUI
     * This will only work on CHEST {@link GuiType}
     *
     * @param rows The amount of rows
     * @return The builder
     */
    @NotNull
    @Contract("_ -> this")
    public B rows(final int rows) {
        this.rows = rows;
        return (B) this;
    }

    /**
     * Sets the title for the GUI
     * This will be either a Component or a String
     *
     * @param title The GUI title
     * @return The builder
     */
    @NotNull
    @Contract("_ -> this")
    public B title(@NotNull final Component title) {
        this.title = title;
        return (B) this;
    }

    /**
     * Disable item placement inside the GUI
     *
     * @return The builder
     * @author SecretX
     * @since 3.0.0
     */
    @NotNull
    @Contract(" -> this")
    public B disableItemPlace() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
        return (B) this;
    }

    /**
     * Disable item retrieval inside the GUI
     *
     * @return The builder
     * @author SecretX
     * @since 3.0.0
     */
    @NotNull
    @Contract(" -> this")
    public B disableItemTake() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
        return (B) this;
    }

    /**
     * Disable item swap inside the GUI
     *
     * @return The builder
     * @author SecretX
     * @since 3.0.0
     */
    @NotNull
    @Contract(" -> this")
    public B disableItemSwap() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
        return (B) this;
    }

    /**
     * Disable item drop inside the GUI
     *
     * @return The builder
     * @since 3.0.3
     */
    @NotNull
    @Contract(" -> this")
    public B disableItemDrop() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
        return (B) this;
    }

    /**
     * Disable other GUI actions
     * This option pretty much disables creating a clone stack of the item
     *
     * @return The builder
     * @since 3.0.4
     */
    @NotNull
    @Contract(" -> this")
    public B disableOtherActions() {
        interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return (B) this;
    }

    /**
     * Disable all the modifications of the GUI, making it immutable by player interaction
     *
     * @return The builder
     * @author SecretX
     * @since 3.0.0
     */
    @NotNull
    @Contract(" -> this")
    public B disableAllInteractions() {
        interactionModifiers.addAll(InteractionModifier.VALUES);
        return (B) this;
    }

    /**
     * Allows item placement inside the GUI
     *
     * @return The builder
     * @author SecretX
     * @since 3.0.0
     */
    @NotNull
    @Contract(" -> this")
    public B enableItemPlace() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_PLACE);
        return (B) this;
    }

    /**
     * Allow items to be taken from the GUI
     *
     * @return The builder
     * @author SecretX
     * @since 3.0.0
     */
    @NotNull
    @Contract(" -> this")
    public B enableItemTake() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_TAKE);
        return (B) this;
    }

    /**
     * Allows item swap inside the GUI
     *
     * @return The builder
     * @author SecretX
     * @since 3.0.0
     */
    @NotNull
    @Contract(" -> this")
    public B enableItemSwap() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_SWAP);
        return (B) this;
    }

    /**
     * Allows item drop inside the GUI
     *
     * @return The builder
     * @since 3.0.3
     */
    @NotNull
    @Contract(" -> this")
    public B enableItemDrop() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_DROP);
        return (B) this;
    }

    /**
     * Enable other GUI actions
     * This option pretty much enables creating a clone stack of the item
     *
     * @return The builder
     * @since 3.0.4
     */
    @NotNull
    @Contract(" -> this")
    public B enableOtherActions() {
        interactionModifiers.remove(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return (B) this;
    }

    /**
     * Enable all modifications of the GUI, making it completely mutable by player interaction
     *
     * @return The builder
     * @author SecretX
     * @since 3.0.0
     */
    @NotNull
    @Contract(" -> this")
    public B enableAllInteractions() {
        interactionModifiers.clear();
        return (B) this;
    }

    /**
     * Applies anything to the GUI once it's created
     * Can be pretty useful for setting up small things like default actions
     *
     * @param consumer A {@link Consumer} that passes the built GUI
     * @return The builder
     */
    @NotNull
    @Contract("_ -> this")
    public B apply(@NotNull final Consumer<G> consumer) {
        this.consumer = consumer;
        return (B) this;
    }

    /**
     * Creates the given GuiBase
     * Has to be abstract because each GUI are different
     *
     * @return The new {@link BaseGui}
     */
    @NotNull
    @Contract(" -> new")
    public abstract G create();

    /**
     * Getter for the title
     *
     * @return The current title
     */
    @NotNull
    protected Component getTitle() {
        if (title == null) {
            throw new GuiException("GUI title is missing!");
        }

        return title;
    }

    /**
     * Getter for the rows
     *
     * @return The amount of rows
     */
    protected int getRows() {
        return rows;
    }

    /**
     * Getter for the consumer
     *
     * @return The consumer
     */
    @Nullable
    protected Consumer<G> getConsumer() {
        return consumer;
    }


    /**
     * Getter for the set of interaction modifiers
     *
     * @return The set of {@link InteractionModifier}
     * @author SecretX
     * @since 3.0.0
     */
    @NotNull
    protected Set<InteractionModifier> getModifiers() {
        return interactionModifiers;
    }
}
