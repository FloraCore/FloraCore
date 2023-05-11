package team.floracore.common.gui.components;

import java.util.*;

/**
 * Used to control what kind of interaction can happen inside a GUI
 *
 * @author SecretX
 * @since 3.0.0
 */
public enum InteractionModifier {
    PREVENT_ITEM_PLACE,
    PREVENT_ITEM_TAKE,
    PREVENT_ITEM_SWAP,
    PREVENT_ITEM_DROP,
    PREVENT_OTHER_ACTIONS;

    public static final Set<InteractionModifier> VALUES = Collections.unmodifiableSet(EnumSet.allOf(InteractionModifier.class));
}
