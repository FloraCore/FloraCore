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
import team.floracore.common.locale.*;
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

    @CommandMethod("give <player> <item> [amount] [data] [tags]")
    public void execute(
            @NotNull CommandSender s,
            @NotNull @Argument("player") Player player,
            @NotNull @Argument("item") String itemKey,
            @Nullable @Argument("amount") Integer amount,
            @Nullable @Argument("data") Integer data,
            @Nullable @Greedy @Argument("tags") String tags
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        @Nullable Material material = getMaterialByItemKey(itemKey);
        if (material == null) {
            Message.COMMAND_GIVE_ITEM_NOTFOUND.send(sender, itemKey);
            return;
        }
        amount = amount != null ? amount : 1;
        ItemStack item = new ItemStack(material, amount);
        if (data != null && data != 0) {
            try {
                setDurability(item, data);
            } catch (ItemStackNonApplicableDataValueException e) {
                Message.COMMAND_GIVE_ITEM_NODATA.send(sender, itemKey);
            }
        }
        if (tags != null) {
            try {
                item = applyNBTTags(item, tags);
            } catch (NBTSyntaxException e) {
                Message.COMMAND_GIVE_ITEM_NBTSYTAXEXCEPTION.send(sender);
            }
        }
        //noinspection DataFlowIssue
        Message.COMMAND_GIVE_ITEM_GIVEN.send(sender, item.getItemMeta().getDisplayName(), player.getName());
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
            Object nmsItem = ReflectionWrapper.invokeStaticMethod(ReflectionWrapper.getMethod(classItem, "d", String.class), itemKey);
//            return CraftMagicNumbers.getMaterial(nmsItem);
            Class<?> classCraftMagicNumbers = ReflectionWrapper.getCraftBukkitClass("util.CraftMagicNumbers");
            Method methodGetMaterial = ReflectionWrapper.getMethod(classCraftMagicNumbers, "getMaterial", classItem);
            return ReflectionWrapper.invokeStaticMethod(methodGetMaterial, nmsItem);
        }
    }

    private void setDurability(@NotNull ItemStack item, int durability) throws ItemStackNonApplicableDataValueException {
        try { // 新版的耐久度是集合进ItemMeta里的
            Class<?> classDamageable = Class.forName("org.bukkit.inventory.meta.Damageable");
            ItemMeta meta = item.getItemMeta();
//            if (meta instanceof Damageable)
            if (classDamageable.isInstance(meta)) {
//                ((Damageable) meta).setDamage(durability);
                ReflectionWrapper.invokeMethod(ReflectionWrapper.getMethod(classDamageable, "setDamage", Integer.class), meta, durability);
                return;
            }
            throw new ItemStackNonApplicableDataValueException();
        } catch (ClassNotFoundException e) {
            // 旧版可以直接调用setDurability
            ReflectionWrapper.invokeMethod(ReflectionWrapper.getMethod(ItemStack.class, "setDurability", Short.class), item, (short) durability);
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
//            net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
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
//            } catch (CommandSyntaxException e) {
            if ("MojangsonParseException".equals(e.getClass().getSimpleName()) || "com.mojang.brigadier.exceptions.CommandSyntaxException".equals(e.getClass().getName())) {
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
            Class<?> classRegistrySimple = ReflectionWrapper.getClassByName("RegistrySimple");
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
