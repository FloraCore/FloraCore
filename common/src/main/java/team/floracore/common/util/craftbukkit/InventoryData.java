package team.floracore.common.util.craftbukkit;

import org.bukkit.inventory.*;

import java.util.*;

public class InventoryData {
    private final List<Integer> emptySlots;
    private final HashMap<ItemStack, List<Integer>> partialSlots;

    public InventoryData(List<Integer> emptySlots, HashMap<ItemStack, List<Integer>> partialSlots) {
        this.emptySlots = emptySlots;
        this.partialSlots = partialSlots;
    }

    public List<Integer> getEmptySlots() {
        return emptySlots;
    }

    public HashMap<ItemStack, List<Integer>> getPartialSlots() {
        return partialSlots;
    }
}