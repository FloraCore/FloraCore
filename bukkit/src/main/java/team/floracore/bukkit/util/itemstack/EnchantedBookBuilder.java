package team.floracore.bukkit.util.itemstack;

import org.bukkit.inventory.ItemStack;
import team.floracore.bukkit.util.wrappednms.NmsNBTTagCompound;

public class EnchantedBookBuilder extends ItemStackBuilder {
    public static final String id = "minecraft:enchanted_book";

    public EnchantedBookBuilder() {
        super(id);
    }

    public EnchantedBookBuilder(ItemStackBuilder is) {
        super(is);
    }

    public EnchantedBookBuilder(NmsNBTTagCompound nbt) {
        super(nbt);
    }

    public EnchantedBookBuilder(ItemStack is) {
        super(is);
    }

    public boolean hasStoredEnchant() {
        return hasTag() && tag().containsKey("StoredEnchantments");
    }

    public boolean isStoredEnchantsHide() {
        return (getHideFlags() & 32) != 0;
    }

    public ItemStackBuilder setHideStoredEnchants(boolean hideEnchants) {
        if (hideEnchants) {
            return setHideFlags((byte) (getHideFlags() | 32));
        } else {
            return setHideFlags((byte) (getHideFlags() & ~32));
        }
    }
}
