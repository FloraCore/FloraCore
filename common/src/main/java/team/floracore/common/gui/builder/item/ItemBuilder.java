package team.floracore.common.gui.builder.item;

import com.mojang.authlib.*;
import com.mojang.authlib.properties.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.jetbrains.annotations.*;
import team.floracore.common.gui.components.exception.*;
import team.floracore.common.gui.components.util.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Main ItemBuilder
 */
public class ItemBuilder extends BaseItemBuilder<ItemBuilder> {

    /**
     * Constructor of the item builder
     *
     * @param itemStack The {@link ItemStack} of the item
     */
    ItemBuilder(@NotNull final ItemStack itemStack) {
        super(itemStack);
    }

    /**
     * Main method to create {@link ItemBuilder}
     *
     * @param itemStack The {@link ItemStack} you want to edit
     * @return A new {@link ItemBuilder}
     */
    @NotNull
    @Contract("_ -> new")
    public static ItemBuilder from(@NotNull final ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }


    /**
     * Alternative method to create {@link ItemBuilder}
     *
     * @param material The {@link Material} you want to create an item from
     * @return A new {@link ItemBuilder}
     */
    @NotNull
    @Contract("_ -> new")
    public static ItemBuilder from(@NotNull final Material material) {
        return new ItemBuilder(new ItemStack(material));
    }

    /**
     * Method for creating a {@link BannerBuilder} which will have BANNER specific methods
     *
     * @return A new {@link BannerBuilder}
     * @since 3.0.1
     */
    @NotNull
    @Contract(" -> new")
    public static BannerBuilder banner() {
        return new BannerBuilder();
    }

    /**
     * Method for creating a {@link BannerBuilder} which will have BANNER specific methods
     *
     * @param itemStack An existing BANNER {@link ItemStack}
     * @return A new {@link BannerBuilder}
     * @throws GuiException if the item is not a BANNER
     * @since 3.0.1
     */
    @NotNull
    @Contract("_ -> new")
    public static BannerBuilder banner(@NotNull final ItemStack itemStack) {
        return new BannerBuilder(itemStack);
    }

    /**
     * Method for creating a {@link BookBuilder} which will have {@link Material#WRITABLE_BOOK} /
     * {@link Material#WRITTEN_BOOK} specific methods
     *
     * @param itemStack an existing {@link Material#WRITABLE_BOOK} / {@link Material#WRITTEN_BOOK} {@link ItemStack}
     * @return A new {@link FireworkBuilder}
     * @throws GuiException if the item type is not {@link Material#WRITABLE_BOOK}
     *                      or {@link Material#WRITTEN_BOOK}
     * @since 3.0.1
     */
    @NotNull
    @Contract("_ -> new")
    public static BookBuilder book(@NotNull final ItemStack itemStack) {
        return new BookBuilder(itemStack);
    }

    /**
     * Method for creating a {@link FireworkBuilder} which will have {@link Material#FIREWORK_ROCKET} specific methods
     *
     * @return A new {@link FireworkBuilder}
     * @since 3.0.1
     */
    @NotNull
    @Contract(" -> new")
    public static FireworkBuilder firework() {
        return new FireworkBuilder(new ItemStack(Material.FIREWORK_ROCKET));
    }

    /**
     * Method for creating a {@link FireworkBuilder} which will have {@link Material#FIREWORK_ROCKET} specific methods
     *
     * @param itemStack an existing {@link Material#FIREWORK_ROCKET} {@link ItemStack}
     * @return A new {@link FireworkBuilder}
     * @throws GuiException if the item type is not {@link Material#FIREWORK_ROCKET}
     * @since 3.0.1
     */
    @NotNull
    @Contract("_ -> new")
    public static FireworkBuilder firework(@NotNull final ItemStack itemStack) {
        return new FireworkBuilder(itemStack);
    }

    /**
     * Method for creating a {@link MapBuilder} which will have {@link Material#MAP} specific methods
     *
     * @return A new {@link MapBuilder}
     * @since 3.0.1
     */
    @NotNull
    @Contract(" -> new")
    public static MapBuilder map() {
        return new MapBuilder();
    }

    /**
     * Method for creating a {@link MapBuilder} which will have @link Material#MAP} specific methods
     *
     * @param itemStack An existing {@link Material#MAP} {@link ItemStack}
     * @return A new {@link MapBuilder}
     * @throws GuiException if the item type is not {@link Material#MAP}
     * @since 3.0.1
     */
    @NotNull
    @Contract("_ -> new")
    public static MapBuilder map(@NotNull final ItemStack itemStack) {
        return new MapBuilder(itemStack);
    }

    /**
     * Method for creating a {@link SkullBuilder} which will have PLAYER_HEAD specific methods
     *
     * @return A new {@link SkullBuilder}
     */
    @NotNull
    @Contract(" -> new")
    public static SkullBuilder skull() {
        return new SkullBuilder();
    }

    /**
     * Method for creating a {@link SkullBuilder} which will have PLAYER_HEAD specific methods
     *
     * @param itemStack An existing PLAYER_HEAD {@link ItemStack}
     * @return A new {@link SkullBuilder}
     * @throws GuiException if the item is not a player head
     */
    @NotNull
    @Contract("_ -> new")
    public static SkullBuilder skull(@NotNull final ItemStack itemStack) {
        return new SkullBuilder(itemStack);
    }

    /**
     * Method for creating a {@link FireworkBuilder} which will have {@link Material#FIREWORK_STAR} specific methods
     *
     * @return A new {@link FireworkBuilder}
     * @since 3.0.1
     */
    @NotNull
    @Contract(" -> new")
    public static FireworkBuilder star() {
        return new FireworkBuilder(new ItemStack(Material.FIREWORK_STAR));
    }

    /**
     * Method for creating a {@link FireworkBuilder} which will have {@link Material#FIREWORK_STAR} specific methods
     *
     * @param itemStack an existing {@link Material#FIREWORK_STAR} {@link ItemStack}
     * @return A new {@link FireworkBuilder}
     * @throws GuiException if the item type is not {@link Material#FIREWORK_STAR}
     * @since 3.0.1
     */
    @NotNull
    @Contract("_ -> new")
    public static FireworkBuilder star(@NotNull final ItemStack itemStack) {
        return new FireworkBuilder(itemStack);
    }

    /**
     * Sets the skull texture
     *
     * @param texture The base64 texture
     * @return {@link ItemBuilder}
     * @deprecated In favor of {@link SkullBuilder#texture(String)}, nothing changed just the name, will be removed in 3.0.1
     */
    @Deprecated
    public ItemBuilder setSkullTexture(@NotNull final String texture) {
        if (!SkullUtil.isPlayerSkull(getItemStack())) return this;

        final SkullMeta skullMeta = (SkullMeta) getMeta();
        final GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        final Field profileField;

        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        setMeta(skullMeta);
        return this;
    }

    /**
     * Sets skull owner via bukkit methods
     *
     * @param player {@link OfflinePlayer} to set skull of
     * @return {@link ItemBuilder}
     * @deprecated In favor of {@link SkullBuilder#owner(OfflinePlayer)}, nothing changed just the name, will be removed in 3.0.1
     */
    @Deprecated
    public ItemBuilder setSkullOwner(@NotNull final OfflinePlayer player) {
        if (!SkullUtil.isPlayerSkull(getItemStack())) return this;

        final SkullMeta skullMeta = (SkullMeta) getMeta();
        skullMeta.setOwningPlayer(player);

        setMeta(skullMeta);
        return this;
    }

}
