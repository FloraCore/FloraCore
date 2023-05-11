package team.floracore.common.gui.builder.item;

import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.jetbrains.annotations.*;
import team.floracore.common.gui.components.exception.*;

import java.util.*;

/**
 * Item builder for {@link Material#FIREWORK_ROCKET} and {@link Material#FIREWORK_ROCKET} only
 */
public class FireworkBuilder extends BaseItemBuilder<FireworkBuilder> {

    private static final Material STAR = Material.FIREWORK_STAR;
    private static final Material ROCKET = Material.FIREWORK_ROCKET;

    FireworkBuilder(@NotNull final ItemStack itemStack) {
        super(itemStack);
        if (itemStack.getType() != STAR && itemStack.getType() != ROCKET) {
            throw new GuiException("FireworkBuilder requires the material to be a FIREWORK_STAR/FIREWORK_ROCKET!");
        }
    }

    /**
     * Add several firework effects to this firework.
     *
     * @param effects effects to add
     * @return {@link FireworkBuilder}
     * @throws IllegalArgumentException If effects is null
     * @throws IllegalArgumentException If any effect is null (may be thrown after changes have occurred)
     * @since 3.0.1
     */
    @NotNull
    @Contract("_ -> this")
    public FireworkBuilder effect(@NotNull final FireworkEffect... effects) {
        return effect(Arrays.asList(effects));
    }

    /**
     * Add several firework effects to this firework.
     *
     * @param effects effects to add
     * @return {@link FireworkBuilder}
     * @throws IllegalArgumentException If effects is null
     * @throws IllegalArgumentException If any effect is null (may be thrown after changes have occurred)
     * @since 3.0.1
     */
    @NotNull
    @Contract("_ -> this")
    public FireworkBuilder effect(@NotNull final List<FireworkEffect> effects) {
        if (effects.isEmpty()) {
            return this;
        }

        if (getItemStack().getType() == STAR) {
            final FireworkEffectMeta effectMeta = (FireworkEffectMeta) getMeta();

            effectMeta.setEffect(effects.get(0));
            setMeta(effectMeta);
            return this;
        }

        final FireworkMeta fireworkMeta = (FireworkMeta) getMeta();

        fireworkMeta.addEffects(effects);
        setMeta(fireworkMeta);
        return this;
    }

    /**
     * Sets the approximate power of the firework. Each level of power is half
     * a second of flight time.
     *
     * @param power the power of the firework, from 0-128
     * @return {@link FireworkBuilder}
     * @throws IllegalArgumentException if {@literal height<0 or height>128}
     * @since 3.0.1
     */
    @NotNull
    @Contract("_ -> this")
    public FireworkBuilder power(final int power) {
        if (getItemStack().getType() == ROCKET) {
            final FireworkMeta fireworkMeta = (FireworkMeta) getMeta();

            fireworkMeta.setPower(power);
            setMeta(fireworkMeta);
        }

        return this;
    }

}
