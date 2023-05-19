package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.*;
import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.checkerframework.checker.nullness.qual.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.exception.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.util.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Give命令
 */
@CommandDescription("给予玩家物品")
@CommandPermission("floracore.command.give")
public class GiveCommand extends AbstractFloraCoreCommand {
    public GiveCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @SuppressWarnings("RedundantSuppression")
    @CommandMethod("give <player> <item> [amount] [data] [tags]")
    public void execute(
            @NotNull CommandSender s,
            @NotNull @Argument("player") Player player,
            @NotNull @Argument(value = "item", suggestions = "all-items-name") String itemKey,
            @Nullable @Argument("amount") Integer amount,
            @Nullable @Argument("data") Integer data,
            @Nullable @Greedy @Argument("tags") String tags
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        @Nullable Material material = getMaterialByItemKey(itemKey);
        if (material == null) {
            Message.COMMAND_GIVE_ITEM_NOSUCH.send(sender, itemKey);
            return;
        }
        amount = amount != null ? amount : 1;
        @Nullable Object item = getNMSItemByItemKey(itemKey);
        if (item == null) {
            Message.COMMAND_GIVE_ITEM_NOSUCH.send(sender, itemKey);
            return;
        }
        Object nmsItemStack;
        String itemName;
        Class<?> classItemStack;
        try {
            classItemStack = Class.forName("net.minecraft.world.item.ItemStack");
//            nmsItemStack = new net.minecraft.world.item.ItemStack(item, amount);
            nmsItemStack = ReflectionWrapper.newInstance(classItemStack, new Class[]{
                    ReflectionWrapper.getClassByName("net.minecraft.world.level.IMaterial"),
                    int.class
            }, item, amount);
            if (data != null && data != 0) {
//                nmsItemStack.b(data); // 为物品堆设置damage
                ReflectionWrapper.invokeMethod(classItemStack, "b", new Class[]{int.class}, nmsItemStack, data);
            }
            if (tags != null) {
//                NBTTagCompound nbt = MojangsonParser.a(tags); 解析NBT
                Object nbt;
                try {
                    nbt = ReflectionWrapper.getClassByName("net.minecraft.nbt.MojangsonParser").getMethod("a", String.class).invoke(null, tags);
                } catch (InvocationTargetException e) {
                    if ("com.mojang.brigadier.exceptions.CommandSyntaxException".equals(e.getCause().getClass().getName())) {
                        // NBT标签语法错误
                        Message.COMMAND_GIVE_ITEM_NBT_SYNTAX_EXCEPTION.send(sender);
                        return;
                    }
                    throw new RuntimeException(e);
                } catch (IllegalAccessException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                Class<?> classNBTTagCompound = ReflectionWrapper.getClassByName("net.minecraft.nbt.NBTTagCompound");
//                nmsItemStack.c(nbt); // 为物品堆设置NBT
                ReflectionWrapper.invokeMethod(classItemStack, "c", new Class[]{classNBTTagCompound}, nmsItemStack, nbt);
            }
//            IChatBaseComponent text = nmsItemStack.x(); // 获取物品名称
            Object text = ReflectionWrapper.invokeMethod(classItemStack, "x", new Class[0], nmsItemStack);
            Class<?> classIChatBaseComponent = ReflectionWrapper.getClassByName("net.minecraft.network.chat.IChatBaseComponent");
//            itemName = text.getString();
            itemName = ReflectionWrapper.invokeMethod(classIChatBaseComponent, "getString", new Class[0], text);
        } catch (ClassNotFoundException e) {
            classItemStack = ReflectionWrapper.getNMSClass("ItemStack");
//            nmsItemStack = new ItemStack(item, amount, data == null ? 0 : data);
            nmsItemStack = ReflectionWrapper.newInstance(classItemStack, new Class[]{
                    ReflectionWrapper.getNMSClass("Item"),
                    int.class, int.class
            }, item, amount, data == null ? 0 : data);
            if (tags != null) {
//                NBTTagCompound nbt = MojangsonParser.parse(tags); 解析NBT
                Object nbt;
                try {
                    nbt = ReflectionWrapper.getNMSClass("MojangsonParser").getMethod("parse", String.class).invoke(null, tags);
                } catch (InvocationTargetException ex) {
                    if (ReflectionWrapper.getNMSClassName("MojangsonParseException").equals(ex.getCause().getClass().getName())) {
                        // NBT标签语法错误
                        Message.COMMAND_GIVE_ITEM_NBT_SYNTAX_EXCEPTION.send(sender);
                        return;
                    }
                    throw new RuntimeException(ex);
                } catch (IllegalAccessException | NoSuchMethodException ex) {
                    throw new RuntimeException(ex);
                }
                Class<?> classNBTTagCompound = ReflectionWrapper.getNMSClass("NBTTagCompound");
//                nmsItemStack.setTag(nbt);
                ReflectionWrapper.invokeMethod(classItemStack, "setTag", new Class[]{classNBTTagCompound}, nmsItemStack, nbt);
            }
            itemName = ReflectionWrapper.invokeMethod(classItemStack, "getName", new Class[0], nmsItemStack);
        }
        Message.COMMAND_GIVE_ITEM_GIVEN.send(sender, itemName, player.getName());
        Class<?> classCraftItemStack = ReflectionWrapper.getCraftBukkitClass("inventory.CraftItemStack");
//        ItemStack itemStack = CraftItemStack.asBukkitCopy(nmsItemStack);
        ItemStack itemStack = ReflectionWrapper.invokeStaticMethod(classCraftItemStack, "asBukkitCopy", new Class[]{classItemStack}, nmsItemStack);
        player.getInventory().addItem(itemStack);
    }

    private @Nullable Object getNMSItemByItemKey(@NotNull String itemKey) {
        Objects.requireNonNull(itemKey);
        try {
            Class<?> classBuiltInRegistries = Class.forName("net.minecraft.core.registries.BuiltInRegistries");
//            RegistryBlocks<Item> ITEM = BuiltInRegistries.l;
            Object ITEM = ReflectionWrapper.getStaticFieldValue(classBuiltInRegistries, "i");
            Class<?> classMinecraftKey = ReflectionWrapper.getClassByName("net.minecraft.resources.MinecraftKey");
//            MinecraftKey key = MinecraftKey.a(itemKey);
            Object key = ReflectionWrapper.invokeStaticMethod(classMinecraftKey, "a", new Class[]{String.class}, itemKey);
            Class<?> classRegistryBlocks = ReflectionWrapper.getClassByName("net.minecraft.core.RegistryBlocks");
//            return ITEM.a(key);
            return ReflectionWrapper.invokeMethod(classRegistryBlocks, "a", new Class[]{classMinecraftKey}, key);
        } catch (ClassNotFoundException e) {
            Class<?> classItem = ReflectionWrapper.getNMSClass("Item");
//            return Item.d(itemKey);
            return ReflectionWrapper.invokeStaticMethod(classItem, "d", new Class[]{String.class}, itemKey);
        }
    }

    private @Nullable Material getMaterialByItemKey(@NotNull String itemKey) {
        Objects.requireNonNull(itemKey);
        try { // 新版可以通过Material#matchMaterial(String)从命名获取物品
            @SuppressWarnings({"JavaReflectionMemberAccess", "RedundantSuppression"})
            Method methodMatchMaterial = Material.class.getMethod("matchMaterial", String.class);
            return ReflectionWrapper.invokeStaticMethod(methodMatchMaterial, itemKey);
        } catch (NoSuchMethodException e) {
            // 旧版需要调用NMS
            Class<?> classItem = ReflectionWrapper.getNMSClass("Item");
            // Item nmsItem = Item.d(item);
            Object nmsItem = ReflectionWrapper.invokeStaticMethod(classItem, "d", new Class[]{String.class}, itemKey);
//            return CraftMagicNumbers.getMaterial(nmsItem);
            Class<?> classCraftMagicNumbers = ReflectionWrapper.getCraftBukkitClass("util.CraftMagicNumbers");
            Method methodGetMaterial = ReflectionWrapper.getMethod(classCraftMagicNumbers, "getMaterial", classItem);
            return ReflectionWrapper.invokeStaticMethod(methodGetMaterial, nmsItem);
        }
    }

    /**
     * 通过NMS获取ItemStack名称
     *
     * @param itemStack ItemStack
     * @return 通过NMS获取的ItemStack名称
     */
    private @NotNull String getItemNameByItemStack(@NotNull ItemStack itemStack) {
        Class<?> classCraftItemStack = ReflectionWrapper.getCraftBukkitClass("inventory.CraftItemStack");
//        ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        Object nmsCopy = ReflectionWrapper.invokeStaticMethod(classCraftItemStack, "asNMSCopy", new Class[]{ItemStack.class}, itemStack);
        try {
            Class<?> classNMSItemStack = Class.forName("net.minecraft.world.item.ItemStack");
//            IChatBaseComponent text = nmsCopy.x();
            Object text = ReflectionWrapper.invokeMethod(classNMSItemStack, "x", new Class[0], nmsCopy);
            Class<?> classIChatBaseComponent = ReflectionWrapper.getClassByName("net.minecraft.network.chat.IChatBaseComponent");
//            return text.getString();
            return ReflectionWrapper.invokeMethod(classIChatBaseComponent, "getString", new Class[0], text);
        } catch (ClassNotFoundException e) {
            // 旧版直接getName()
            Class<?> classNMSItemStack = ReflectionWrapper.getNMSClass("ItemStack");
//            return nmsCopy.getName();
            return ReflectionWrapper.invokeMethod(classNMSItemStack, "getName", new Class[0], nmsCopy);
        }
    }

    private void setDurability(@NotNull ItemStack item, int durability) throws ItemStackNonApplicableDataValueException {
        try { // 新版的耐久度是集合进ItemMeta里的
            Class<?> classDamageable = Class.forName("org.bukkit.inventory.meta.Damageable");
            ItemMeta meta = item.getItemMeta();
//            if (meta instanceof Damageable)
            if (classDamageable.isInstance(meta)) {
//                ((Damageable) meta).setDamage(durability);
                ReflectionWrapper.invokeMethod(classDamageable, "setDamage", new Class[]{int.class}, meta, durability);
                return;
            }
            throw new ItemStackNonApplicableDataValueException();
        } catch (ClassNotFoundException e) {
            // 旧版可以直接调用setDurability
            ReflectionWrapper.invokeMethod(ItemStack.class, "setDurability", new Class[]{short.class}, item, (short) durability);
        }
    }

    private ItemStack applyNBTTags(@NotNull ItemStack itemStack, @NotNull String tags) throws NBTSyntaxException {
        Class<?> classMojangsonParser;
        Method methodParse;
        try {
            classMojangsonParser = Class.forName("net.minecraft.nbt.MojangsonParser");
            methodParse = ReflectionWrapper.getMethod(classMojangsonParser, "a", String.class);
        } catch (ClassNotFoundException e) {
            classMojangsonParser = ReflectionWrapper.getNMSClass("MojangsonParser");
            methodParse = ReflectionWrapper.getMethod(classMojangsonParser, "parse", String.class);
        }
        try {
            // NBTTagCompound nbt = MojangsonParser.parse(tags);
            Object nbt = methodParse.invoke(null, tags);
            Class<?> classCraftItemStack = ReflectionWrapper.getCraftBukkitClass("inventory.CraftItemStack");
            Method methodAsNMSCopy = ReflectionWrapper.getMethod(classCraftItemStack, "asNMSCopy", ItemStack.class);
//            ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
            Object nmsCopy = ReflectionWrapper.invokeStaticMethod(methodAsNMSCopy, itemStack);
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            Method methodSetTag = ReflectionWrapper.findPossibleMethod(nmsCopy.getClass(),
                    cls -> cls.getMethod("c", nbt.getClass()),
                    cls -> cls.getMethod("setTag", nbt.getClass())
            ).get();
//            nmsCopy.setTag(nbt);
            ReflectionWrapper.invokeMethod(methodSetTag, nmsCopy, nbt);
            Method methodAsBukkitCopy = ReflectionWrapper.getMethod(classCraftItemStack, "asBukkitCopy", nmsCopy.getClass());
//            return CraftItemStack.asBukkitCopy(nmsCopy);
            return ReflectionWrapper.invokeStaticMethod(methodAsBukkitCopy, nmsCopy);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
//            } catch (CommandSyntaxException e) {
            if ("com.mojang.brigadier.exceptions.CommandSyntaxException".equals(cause.getClass().getName()) ||
                    ReflectionWrapper.getNMSClassName("MojangsonParseException").equals(cause.getClass().getName())) {
                throw new NBTSyntaxException(e);
            }
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Suggestions("all-items-name")
    public @NotNull List<String> getAllItemsName(final @NonNull CommandContext<CommandSender> sender, final @NonNull String input) {
        try { // 新版可以通过Material#getKey()获取Item命名
            @SuppressWarnings({"JavaReflectionMemberAccess", "RedundantSuppression"})
            Method methodGetKey = Material.class.getMethod("getKey");
            Class<?> classNamespacedKey = ReflectionWrapper.getClassByName("org.bukkit.NamespacedKey");
            Method methodNamespacedKeyGetKey = ReflectionWrapper.getMethod(classNamespacedKey, "getKey");
            Method methodIsLegacy = ReflectionWrapper.getMethod(Material.class, "isLegacy");
            return Arrays.stream(Material.values()).collect(
                    ArrayList::new,
                    (list, material) -> {
                        // if (!material.isLegacy)
                        if (!(boolean) (ReflectionWrapper.invokeMethod(methodIsLegacy, material))) {
//                            NamespacedKey namespacedKey = material.getKey();
                            Object namespacedKey = ReflectionWrapper.invokeMethod(methodGetKey, material);
                            // list.add(namespacedKey.getKey());
                            list.add(ReflectionWrapper.invokeMethod(methodNamespacedKeyGetKey, namespacedKey));
                        }
                    }, ArrayList::addAll
            );
        } catch (NoSuchMethodException e) {
            // 旧版需要调用NMS的Registry
            /*
            return Item.REGISTRY.keySet().stream().collect(
                    ArrayList::new,
                    (list, key) -> list.add(key.b),
                    ArrayList::addAll
            );
             */
            Class<?> classItem = ReflectionWrapper.getNMSClass("Item");
            Field fieldRegistry = ReflectionWrapper.getField(classItem, "REGISTRY");
            // RegistryMaterials<MinecraftKey, Item> registry = Item.REGISTRY;
            Object registry = ReflectionWrapper.getStaticFieldValue(fieldRegistry);
            Class<?> classRegistrySimple = ReflectionWrapper.getNMSClass("RegistrySimple");
            Method methodKeySet = ReflectionWrapper.getMethod(classRegistrySimple, "keySet");
            // Set<MinecraftKey> keySet = registry.keySet();
            Set<?> keySet = ReflectionWrapper.invokeMethod(methodKeySet, registry);
            Class<?> classMinecraftKey = ReflectionWrapper.getNMSClass("MinecraftKey");
            Method methodGetResourcePath = ReflectionWrapper.getMethod(classMinecraftKey, "a");
            return keySet.stream().collect(
                    ArrayList::new,
                    // (list, key) -> list.add(key.a()),
                    (list, key) -> list.add(ReflectionWrapper.invokeMethod(methodGetResourcePath, key)),
                    ArrayList::addAll
            );
        }
    }
}
