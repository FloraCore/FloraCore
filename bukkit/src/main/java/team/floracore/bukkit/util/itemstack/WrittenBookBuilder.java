package team.floracore.bukkit.util.itemstack;

import org.bukkit.inventory.*;
import team.floracore.bukkit.util.wrappednms.*;

public class WrittenBookBuilder extends ItemStackBuilder {
    public static final String id = "minecraft:written_book";

    public WrittenBookBuilder() {
        super(id);
    }

    public WrittenBookBuilder(ItemStackBuilder is) {
        super(is);
    }

    public WrittenBookBuilder(NmsNBTTagCompound nbt) {
        super(nbt);
    }

    public WrittenBookBuilder(ItemStack is) {
        super(is);
    }

    public boolean hasTitle() {
        return hasTag() && tag().containsKey("title");
    }

    public String getTitle() {
        return tag().getString("title");
    }

    public WrittenBookBuilder setTitle(String title) {
        tag().set("title", NmsNBTTagString.newInstance(title));
        return this;
    }
}
