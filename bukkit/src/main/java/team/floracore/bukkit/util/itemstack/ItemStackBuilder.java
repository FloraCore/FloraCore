package team.floracore.bukkit.util.itemstack;

import com.google.common.collect.*;
import com.google.gson.*;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.serializer.legacy.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.*;
import team.floracore.bukkit.util.wrappednms.*;
import team.floracore.bukkit.util.wrappedobc.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.*;
import team.floracore.common.util.wrapper.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class ItemStackBuilder implements Supplier<ItemStack> {
    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();
    public ObcItemStack item;

    public ItemStackBuilder(NmsNBTTagCompound nbt) {
        this(ObcItemStack.asCraftMirror(NmsItemStack.fromNbt(nbt)).getRaw());
    }

    public ItemStackBuilder(ItemStack is) {
        this(ObcItemStack.ensure(is));
    }

    public ItemStackBuilder(ObcItemStack obc) {
        item = obc;
    }

    public ItemStackBuilder(ItemStackBuilder isb) {
        this(isb.item.getRaw());
    }

    public ItemStackBuilder(String id) {
        this(ObcMagicNumbers.getMaterial(NmsItem.fromId(id)));
    }

    public ItemStackBuilder(Material m) {
        this(new ItemStack(m));
    }

    public static ItemStackBuilder air() {
        return new ItemStackBuilder(Material.AIR);
    }

    public static ItemStackBuilder whiteStainedGlassPane() {
        return forFlattening("stained_glass_pane", (short) 0, "white_stained_glass_pane");
    }

    public static ItemStackBuilder forFlattening(String id, int childId, String idV13) {
        if (BukkitWrapper.v13) {
            return new ItemStackBuilder(idV13);
        } else {
            return new ItemStackBuilder(id).setChildId(childId);
        }
    }

    public static ItemStackBuilder orangeStainedGlassPane() {
        return forFlattening("stained_glass_pane", (short) 1, "orange_stained_glass_pane");
    }

    public static ItemStackBuilder lightBlueStainedGlassPane() {
        return forFlattening("stained_glass_pane", (short) 3, "light_blue_stained_glass_pane");
    }

    public static ItemStackBuilder yellowStainedGlassPane() {
        return forFlattening("stained_glass_pane", (short) 4, "yellow_stained_glass_pane");
    }

    public static ItemStackBuilder limeStainedGlassPane() {
        return forFlattening("stained_glass_pane", (short) 5, "lime_stained_glass_pane");
    }

    public static ItemStackBuilder grayStainedGlassPane() {
        return forFlattening("stained_glass_pane", (short) 7, "gray_stained_glass_pane");
    }

    public static ItemStackBuilder purpleStainedGlassPane() {
        return forFlattening("stained_glass_pane", (short) 10, "purple_stained_glass_pane");
    }

    public static ItemStackBuilder blueStainedGlassPane() {
        return forFlattening("stained_glass_pane", (short) 11, "blue_stained_glass_pane");
    }

    public static ItemStackBuilder brownStainedGlassPane() {
        return forFlattening("stained_glass_pane", (short) 12, "brown_stained_glass_pane");
    }

    public static ItemStackBuilder redStainedGlassPane() {
        return forFlattening("stained_glass_pane", (short) 14, "red_stained_glass_pane");
    }

    public static ItemStackBuilder blackStainedGlassPane() {
        return forFlattening("stained_glass_pane", (short) 15, "black_stained_glass_pane");
    }

    public static ItemStackBuilder grass() {
        return forFlattening("tallgrass", (short) 1, "grass");
    }

    public static ItemStackBuilder grassBlock() {
        return forFlattening("grass", (short) 0, "grass_block");
    }

    public static ItemStackBuilder expBottle() {
        return forFlattening("exp_bottle", (short) 0, "experience_bottle");
    }

    public static ItemStackBuilder sign() {
        return forFlattening("sign", (short) 0, "oak_sign");
    }

    public static ItemStackBuilder planks() {
        return forFlattening("planks", (short) 0, "oak_planks");
    }

    public static ItemStackBuilder clock() {
        return new ItemStackBuilder("clock");
    }

    public static ItemStackBuilder enderEye() {
        return new ItemStackBuilder("ender_eye");
    }

    public static ItemStackBuilder craftingTable() {
        return new ItemStackBuilder("crafting_table");
    }

    public static ItemStackBuilder questionMark() {
        return newSkull(null,
                "http://textures.minecraft.net/texture/65b95da1281642daa5d022adbd3e7cb69dc0942c81cd63be9c3857d222e1c8d9");
    }

    public static ItemStackBuilder newSkull(String name, String url) {
        return newSkull(name,
                UUID.nameUUIDFromBytes(url.getBytes(StringUtil.UTF8)),
                Base64.getEncoder()
                        .encodeToString(("{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}").getBytes(StringUtil.UTF8)));
    }

    public static ItemStackBuilder newSkull(String name, UUID id, String value) {
        ItemStackBuilder is = forFlattening("skull", 3, "player_head");
        is.tag()
                .set("SkullOwner",
                        NmsNBTTagCompound.newInstance()
                                .set("Id",
                                        BukkitWrapper.version < 16 ? NmsNBTTagString.newInstance(id.toString()) : NmsNBTTagIntArray.newInstance(
                                                id))
                                .set("Properties",
                                        NmsNBTTagCompound.newInstance()
                                                .set("textures",
                                                        NmsNBTTagList.newInstance(NmsNBTTagCompound.newInstance()
                                                                .set("Value",
                                                                        NmsNBTTagString.newInstance(
                                                                                value))))));
        if (name != null) {
            is.tag().getCompound("SkullOwner").set("Name", NmsNBTTagString.newInstance(name));
        }
        return is;
    }

    public static ItemStackBuilder returnArrow() {
        return newSkull(null,
                "http://textures.minecraft.net/texture/d9ed8bcbafbe99787325239048b8099407a098e7077c9b4c3b478b289b9149fd");
    }

    public static ItemStackBuilder leftArrow() {
        return newSkull(null,
                "http://textures.minecraft.net/texture/3866a889e51ca79c5d200ea6b5cfd0a655f32fea38b8138598c72fb200b97b9");
    }

    public static ItemStackBuilder rightArrow() {
        return newSkull(null,
                "http://textures.minecraft.net/texture/dfbf1402a04064cebaa96b77d5455ee93b685332e264c80ca36415df992fb46c");
    }

    public static ItemStackBuilder checkmark() {
        return newSkull(null,
                "http://textures.minecraft.net/texture/ce2a530f42726fa7a31efab8e43dadee188937cf824af88ea8e4c93a49c57294");
    }

    @SuppressWarnings("deprecation")
    public static MaterialData getData(ItemStack is) {
        if (is == null) {
            return new MaterialData(Material.AIR);
        }
        return new MaterialData(is.getType(), (byte) is.getDurability());
    }

    public static NmsItemStack toNms(ItemStack is) {
        return ObcItemStack.asNMSCopy(is);
    }

    public static ItemStack fromNms(NmsItemStack nms) {
        return ObcItemStack.asBukkitCopy(nms);
    }

    public static byte getCount(ItemStack item) {
        if (isAir(item)) {
            return 0;
        } else {
            return (byte) item.getAmount();
        }
    }

    public static boolean isAir(ItemStack is) {
        return is == null || is.getType() == Material.AIR || is.getAmount() < 1;
    }

    public static JsonObject setDefaultNonItalic(JsonObject json) {
        if (!json.has("italic")) {
            json.addProperty("italic", false);
        }
        if (json.has("extra")) {
            for (JsonElement extra : json.get("extra").getAsJsonArray()) {
                setDefaultNonItalic(extra.getAsJsonObject());
            }
        }
        if (json.has("with")) {
            for (JsonElement extra : json.get("with").getAsJsonArray()) {
                setDefaultNonItalic(extra.getAsJsonObject());
            }
        }
        return json;
    }

    public static String getId(Material m) {
        return ObcMagicNumbers.getItem(m).getId();
    }

    public NmsNBTTagCompound tag() {
        if (!hasTag()) {
            getHandle().setTag(NmsNBTTagCompound.newInstance());
        }
        return getHandle().getTag();
    }

    public boolean hasTag() {
        if (WrappedObject.isNull(this.item.getHandle())) {
            return false;
        }
        return !WrappedObject.isNull(item.getHandle().getTag());
    }

    public NmsItemStack getHandle() {
        if (WrappedObject.isNull(this.item.getHandle())) {
            this.item.setHandle(NmsItemStack.newInstance(ObcMagicNumbers.getItem(Material.AIR)));
        }
        return this.item.getHandle();
    }

    @Override
    public ItemStack get() {
        return item.getRaw();
    }

    public String getId() {
        if (WrappedObject.isNull(item.getHandle())) {
            return "minecraft:air";
        }
        return item.getHandle().getItem().getId();
    }

    public ItemStackBuilder setId(String id) {
        if (WrappedObject.isNull(this.item.getHandle())) {
            this.item.setHandle(NmsItemStack.newInstance(NmsItem.fromId(id)));
        } else {
            item.getHandle().setItem(NmsItem.fromId(id));
        }
        return this;
    }

    public ItemStackBuilder setId(Material m) {
        return setId(getId(m));
    }

    public ItemStackBuilder add(int count) {
        return setCount((byte) (getCount() + count));
    }

    public byte getCount() {
        return (byte) item.getRaw().getAmount();
    }

    public ItemStackBuilder setCount(int count) {
        item.getRaw().setAmount(count);
        return this;
    }

    public ItemStackBuilder setTag(NmsNBTTagCompound tag) {
        if (WrappedObject.isNull(this.item.getHandle())) {
            this.item.setHandle(NmsItemStack.newInstance(ObcMagicNumbers.getItem(Material.AIR)));
        }
        item.getHandle().setTag(tag);
        return this;
    }

    public int getDamage() {
        if (BukkitWrapper.v13) {
            if (tag().getMap().containsKey("Damage")) {
                return WrappedObject.wrap(NmsNBTTagInt.class, tag().getMap().get("Damage")).getValue();
            }
            return 0;
        } else {
            return getChildId();
        }
    }

    public ItemStackBuilder setDamage(int damage) {
        if (BukkitWrapper.v13) {
            tag().set("Damage", NmsNBTTagInt.newInstance(damage));
        } else {
            setChildId((short) damage);
        }
        return this;
    }

    /**
     * It doesn't work after The Flattening(MC 1.13)
     *
     * @return childId child ID
     */
    public short getChildId() {
        if (BukkitWrapper.v13) {
            return 0;
        }
        return item.getRaw().getDurability();
    }

    /**
     * It doesn't work after The Flattening(MC 1.13)
     *
     * @param childId child ID
     * @return this
     */
    public ItemStackBuilder setChildId(int childId) {
        if (!BukkitWrapper.v13) {
            item.getRaw().setDurability((short) childId);
        }
        return this;
    }

    public ItemStackBuilder removeMapId() {
        if (BukkitWrapper.v13) {
            tag().remove("map");
        } else {
            setChildId(0);
        }
        return this;
    }

    public ItemStackBuilder setMapId(int mapId) {
        if (BukkitWrapper.v13) {
            tag().set("map", NmsNBTTagInt.newInstance(mapId));
        } else {
            setChildId(mapId);
        }
        return this;
    }

    public boolean hasEnchant() {
        if (BukkitWrapper.v13) {
            return hasTag() && (tag().containsKey("Enchantments"));
        } else {
            return hasTag() && (tag().containsKey("ench"));
        }
    }

    public boolean isEnchantsHide() {
        return (getHideFlags() & 1) != 0;
    }

    public int getHideFlags() {
        if (tag().containsKey("HideFlags")) {
            try {
                return tag().getInt("HideFlags");
            } catch (Throwable e) {
                return tag().getByte("HideFlags");
            }
        }
        return 0;
    }

    public ItemStackBuilder setHideFlags(int hideFlags) {
        tag().set("HideFlags", NmsNBTTagInt.newInstance(hideFlags));
        return this;
    }

    public ItemStackBuilder setHideEnchants(boolean hideEnchants) {
        if (hideEnchants) {
            return setHideFlags((byte) (getHideFlags() | 1));
        } else {
            return setHideFlags((byte) (getHideFlags() & ~1));
        }
    }

    public boolean hasDisplay() {
        return hasTag() && tag().containsKey("display");
    }

    public boolean hasLocName() {
        return hasDisplay() && display().containsKey("LocName");
    }

    public String getLocName() {
        return display().getString("LocName");
    }

    public ItemStackBuilder setLocName(String locName) {
        display().set("LocName", NmsNBTTagString.newInstance(locName));
        return this;
    }

    public NmsNBTTagCompound display() {
        NmsNBTTagCompound tag = tag();
        if (!tag.containsKey("display")) {
            tag.set("display", NmsNBTTagCompound.newInstance());
        }
        return tag.getCompound("display");
    }

    public NmsIChatBaseComponent getNameV14() {
        return NmsIChatBaseComponent.NmsChatSerializer.jsonToComponent(display().getString("Name"));
    }

    public ItemStackBuilder setNameV14(NmsIChatBaseComponent name) {
        display().set("Name", NmsNBTTagString.newInstance(ObcChatMessage.toJson(name)));
        return this;
    }

    public String getName() {
        String r = display().getString("Name");
        if (BukkitWrapper.v13) {
            r = ObcChatMessage.fromJSONComponentV13(r);
        }
        return r;
    }

    public ItemStackBuilder setName(Component component) {
        return setName(SERIALIZER.serialize(component));
    }

    public ItemStackBuilder setName(String name) {
        if (BukkitWrapper.v13) {
            name = ObcChatMessage.fromStringOrNullToJSONV13(name);
            if (name == null) {
                name = "{\"text\":\"\"}";
            }
            name = setDefaultNonItalic(new JsonParser().parse(name).getAsJsonObject()).toString();
        }
        display().set("Name", NmsNBTTagString.newInstance(name));
        return this;
    }

    public boolean hasName() {
        return hasDisplay() && display().containsKey("Name");
    }

    public List<String> getLore() {
        if (hasDisplay() && display().containsKey("Lore")) {
            List<String> r = display().getList("Lore")
                    .values()
                    .stream()
                    .map(n -> n.cast(NmsNBTTagString.class).getValue())
                    .collect(Collectors.toList());
            if (BukkitWrapper.version >= 14) {
                r = r.stream().map(l -> ObcChatMessage.fromJSONComponentV13(l)).collect(Collectors.toList());
            }
            return r;
        }
        return new ArrayList<>();
    }

    public ItemStackBuilder setLore(List<Component> lore) {
        List<String> ret = new ArrayList<>();
        for (Component component : lore) {
            ret.add(SERIALIZER.serialize(component));
        }
        return setLoreString(ret);
    }

    public ItemStackBuilder setEnchants(Map<Enchantment, Integer> enchants) {
        ItemStack is = item.getRaw();
        is.addEnchantments(enchants);
        return this;
    }

    public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
        ItemStack is = item.getRaw();
        ItemMeta im = is.getItemMeta();
        im.addEnchant(enchantment, level, true);
        is.setItemMeta(im);
        return this;
    }

    public ItemStackBuilder setInfinityDurability() {
        ItemStack is = item.getRaw();
        ItemMeta im = is.getItemMeta();
        im.setUnbreakable(true);
        return this;
    }

    public ItemStackBuilder setLoreString(List<String> lore) {
        if (BukkitWrapper.version >= 14) {
            lore = lore.stream()
                    .map(l -> l == null || l.length() == 0 ? "{\"text\":\"\"}" : ObcChatMessage.fromStringOrNullToJSONV13(
                            l))
                    .map(l -> setDefaultNonItalic(new JsonParser().parse(l).getAsJsonObject()).toString())
                    .collect(Collectors.toList());
        }
        if (lore.isEmpty()) {
            display().remove("Lore");
        } else {
            display().set("Lore", NmsNBTTagList.wrapValues(lore));
        }
        return this;
    }

    public ItemStackBuilder setLoreString(String... lore) {
        return setLoreString(Lists.newArrayList(lore));
    }

    public ItemStackBuilder debug() {
        System.out.println(this.save());
        return this;
    }

    public NmsNBTTagCompound save() {
        if (WrappedObject.isNull(this.item.getHandle())) {
            this.item.setHandle(NmsItemStack.newInstance(ObcMagicNumbers.getItem(Material.AIR)));
        }
        return item.getHandle().save(NmsNBTTagCompound.newInstance());
    }

    @Override
    public String toString() {
        return save().toString();
    }

    public ItemStackBuilder addLore(String... lore) {
        List<String> l = getLore();
        l.addAll(Lists.newArrayList(lore));
        setLoreString(l);
        return this;
    }
}
