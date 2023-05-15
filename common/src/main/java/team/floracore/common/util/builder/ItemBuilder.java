package team.floracore.common.util.builder;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.serializer.legacy.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.*;

import java.util.*;

/**
 * ItemBuilder - An API class to create an
 * {@link org.bukkit.inventory.ItemStack} with just one line of code!
 */
public class ItemBuilder {
    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private final ItemStack itemStack;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    public ItemBuilder(Material material, int amount, byte durability) {
        this.itemStack = new ItemStack(material, amount, durability);
    }

    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(itemStack);
    }

    public ItemBuilder durability(short durability) {
        itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder flags(ItemFlag... itemFlag) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(itemFlag);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder displayName(String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder displayName(Component displayName) {
        return displayName(SERIALIZER.serialize(displayName));
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level) {
        itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        itemStack.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        SkullMeta im = (SkullMeta) itemStack.getItemMeta();
        im.setOwner(owner);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setEnchantMeta(Map<Enchantment, Integer> enchantments) {
        EnchantmentStorageMeta im = (EnchantmentStorageMeta) itemStack.getItemMeta();
        for (Map.Entry<Enchantment, Integer> m : enchantments.entrySet()) {
            im.addStoredEnchant(m.getKey(), m.getValue(), true);
        }
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        ItemMeta im = itemStack.getItemMeta();
        im.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setInfinityDurability() {
        itemStack.setDurability(Short.MAX_VALUE);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lores = new ArrayList<>();
        Collections.addAll(lores, lore);
        if (im != null) {
            im.setLore(lores);
        }
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder loreString(List<String> lore) {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lores = new ArrayList<>(lore);
        if (im != null) {
            im.setLore(lores);
        }
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder lore(List<Component> lore) {
        List<String> ret = new ArrayList<>();
        for (Component component : lore) {
            ret.add(SERIALIZER.serialize(component));
        }
        return loreString(ret);
    }

    public ItemBuilder lore(Component lore) {
        List<String> ret = new ArrayList<>();
        ret.add(SERIALIZER.serialize(Component.space()));
        ret.add(SERIALIZER.serialize(lore));
        return loreString(ret);
    }

    public ItemBuilder glow() {
        ItemMeta im = itemStack.getItemMeta();
        im.addEnchant(Enchantment.DURABILITY, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder potion(PotionEffect potionEffect) {
        PotionMeta im = (PotionMeta) itemStack.getItemMeta();
        im.addCustomEffect(potionEffect, true);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder leatherArmorColor(Color color) {
        LeatherArmorMeta im = (LeatherArmorMeta) itemStack.getItemMeta();
        im.setColor(color);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder potionEffect(PotionEffect... potionEffect) {
        ItemStack result = itemStack;
        for (PotionEffect pe : potionEffect) {
            PotionMeta meta = (PotionMeta) result.getItemMeta();
            meta.addCustomEffect(pe, true);
            result.setItemMeta(meta);
        }
        return this;
    }


    public ItemStack build() {
        return itemStack;
    }
}