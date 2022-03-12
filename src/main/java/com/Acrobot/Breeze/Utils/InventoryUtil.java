package com.Acrobot.Breeze.Utils;

import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import com.Acrobot.ChestShop.Containers.AdminInventory;

/**
 * @author Acrobot
 */
public class InventoryUtil {
    /**
     * Returns the amount of the item inside the inventory
     *
     * @param item
     *            Item to check
     * @param inventory
     *            inventory
     * @return amount of the item
     */
    public static int getAmount(ItemStack item, Inventory inventory) {
        if (inventory.getType() == null) {
            return Integer.MAX_VALUE;
        }

        // Special case required because AdminInventory has no storage contents
        if (inventory instanceof AdminInventory) {
            return Integer.MAX_VALUE;
        }

        boolean isPlayerInventory = inventory instanceof PlayerInventory;
        int itemAmount = 0;
        for (ItemStack content : inventory.getStorageContents()) {
            if (item.isSimilar(content)) {
                itemAmount += content.getAmount();
            } else if (!isPlayerInventory && content != null && BlockUtil.isShulkerBox(content.getType())) {
                ItemMeta meta = content.getItemMeta();
                if (meta instanceof BlockStateMeta) {
                    BlockStateMeta bsm = (BlockStateMeta) meta;
                    BlockState blockState = bsm.getBlockState();
                    if (blockState instanceof ShulkerBox) {
                        ShulkerBox shulkerBox = (ShulkerBox) blockState;
                        for (ItemStack shulkerContent : shulkerBox.getSnapshotInventory().getStorageContents()) {
                            if (shulkerContent != null && item.isSimilar(shulkerContent)) {
                                itemAmount += shulkerContent.getAmount();
                            }
                        }
                    }
                }
            }
        }

        return itemAmount;
    }

    /**
     * Tells if the inventory is empty
     *
     * @param inventory
     *            inventory
     * @return Is the inventory empty?
     */
    public static boolean isEmpty(Inventory inventory) {
        for (ItemStack stack : inventory.getContents()) {
            if (!MaterialUtil.isEmpty(stack)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the inventory has stock of this type
     *
     * @param items
     *            items
     * @param inventory
     *            inventory
     * @return Does the inventory contain stock of this type?
     */
    public static boolean hasItems(ItemStack item, Inventory inventory) {
        if (inventory.getType() == null) {
            return true;
        }

        // Special case required because AdminInventory has no storage contents
        if (inventory instanceof AdminInventory) {
            return true;
        }

        boolean isPlayerInventory = inventory instanceof PlayerInventory;
        int missingAmount = item.getAmount();
        for (ItemStack content : inventory.getStorageContents()) {
            if (item.isSimilar(content)) {
                missingAmount -= content.getAmount();
                if (missingAmount <= 0) {
                    return true;
                }
            } else if (!isPlayerInventory && content != null && BlockUtil.isShulkerBox(content.getType())) {
                ItemMeta meta = content.getItemMeta();
                if (meta instanceof BlockStateMeta) {
                    BlockStateMeta bsm = (BlockStateMeta) meta;
                    BlockState blockState = bsm.getBlockState();
                    if (blockState instanceof ShulkerBox) {
                        ShulkerBox shulkerBox = (ShulkerBox) blockState;
                        for (ItemStack shulkerContent : shulkerBox.getSnapshotInventory().getStorageContents()) {
                            if (shulkerContent != null && item.isSimilar(shulkerContent)) {
                                missingAmount -= shulkerContent.getAmount();
                                if (missingAmount <= 0) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns the free space for items of a certain type
     *
     * @param item
     *            Item to check
     * @param inventory
     *            inventory
     * @return free space for the item
     */
    public static int getFreeSpace(ItemStack item, Inventory inventory) {
        if (inventory.getType() == null) {
            return Integer.MAX_VALUE;
        }

        // Special case required because AdminInventory has no storage contents
        if (inventory instanceof AdminInventory) {
            return Integer.MAX_VALUE;
        }

        boolean isPlayerInventory = inventory instanceof PlayerInventory;
        boolean canBeStoredInShulker = !isPlayerInventory && BlockUtil.canBeStoredInShulkerBox(item.getType());
        int freeSpace = 0;
        int maxStack = Math.max(item.getMaxStackSize(), 1);
        for (ItemStack content : inventory.getStorageContents()) {
            if (item.isSimilar(content)) {
                freeSpace += Math.max(maxStack - content.getAmount(), 0);
            } else if (MaterialUtil.isEmpty(content)) {
                freeSpace += maxStack;
            } else if (canBeStoredInShulker && content != null && BlockUtil.isShulkerBox(content.getType())) {
                ItemMeta meta = content.getItemMeta();
                if (meta instanceof BlockStateMeta) {
                    BlockStateMeta bsm = (BlockStateMeta) meta;
                    BlockState blockState = bsm.getBlockState();
                    if (blockState instanceof ShulkerBox) {
                        ShulkerBox shulkerBox = (ShulkerBox) blockState;
                        for (ItemStack shulkerContent : shulkerBox.getSnapshotInventory().getStorageContents()) {
                            if (shulkerContent != null && item.isSimilar(shulkerContent)) {
                                freeSpace += Math.max(maxStack - shulkerContent.getAmount(), 0);
                            } else if (MaterialUtil.isEmpty(shulkerContent)) {
                                freeSpace += maxStack;
                            }
                        }
                    }
                }
            }
        }

        return freeSpace;
    }

    /**
     * Checks if the item fits the inventory
     *
     * @param item
     *            Item to check
     * @param inventory
     *            inventory
     * @return Does item fit inside inventory?
     */
    public static boolean fits(ItemStack item, Inventory inventory) {
        if (inventory.getType() == null) {
            return true;
        }

        // Special case required because AdminInventory has no storage contents
        if (inventory instanceof AdminInventory) {
            return true;
        }

        if (inventory.getMaxStackSize() == Integer.MAX_VALUE) {
            return true;
        }

        boolean isPlayerInventory = inventory instanceof PlayerInventory;
        boolean canBeStoredInShulker = !isPlayerInventory && BlockUtil.canBeStoredInShulkerBox(item.getType());
        int left = item.getAmount();
        int maxStack = Math.max(item.getMaxStackSize(), 1);
        for (ItemStack content : inventory.getStorageContents()) {
            if (item.isSimilar(content)) {
                left -= Math.max(maxStack - content.getAmount(), 0);
                if (left <= 0) {
                    return true;
                }
            } else if (MaterialUtil.isEmpty(content)) {
                left -= maxStack;
                if (left <= 0) {
                    return true;
                }
            } else if (canBeStoredInShulker && content != null && BlockUtil.isShulkerBox(content.getType())) {
                ItemMeta meta = content.getItemMeta();
                if (meta instanceof BlockStateMeta) {
                    BlockStateMeta bsm = (BlockStateMeta) meta;
                    BlockState blockState = bsm.getBlockState();
                    if (blockState instanceof ShulkerBox) {
                        ShulkerBox shulkerBox = (ShulkerBox) blockState;
                        for (ItemStack shulkerContent : shulkerBox.getSnapshotInventory().getStorageContents()) {
                            if (shulkerContent != null && item.isSimilar(shulkerContent)) {
                                left -= Math.max(maxStack - shulkerContent.getAmount(), 0);
                                if (left <= 0) {
                                    return true;
                                }
                            } else if (MaterialUtil.isEmpty(shulkerContent)) {
                                left -= maxStack;
                                if (left <= 0) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Adds an item to the inventory with given maximum stack size
     * (it currently uses a custom method of adding items, because Bukkit hasn't fixed it for a year now - not even kidding)
     *
     * @param item
     *            Item to add
     * @param inventory
     *            Inventory
     * @param maxStackSize
     *            Maximum item's stack size
     * @return Number of leftover items
     */
    public static int add(ItemStack item, Inventory inventory, int maxStackSize) {
        if (item.getAmount() <= 0) {
            return 0;
        }

        if (inventory.getType() == null) {
            return 0;
        }

        // Special case required because AdminInventory has no storage contents
        if (inventory instanceof AdminInventory) {
            return 0;
        }

        int left = item.getAmount();
        ItemStack[] contents = inventory.getStorageContents();
        boolean contentChanged = false;
        int contentsLength = contents.length;
        boolean isPlayerInventory = inventory instanceof PlayerInventory;
        // prefer shulker store
        if (!isPlayerInventory && BlockUtil.canBeStoredInShulkerBox(item.getType())) {
            for (int currentSlot = 0; currentSlot < contentsLength && left > 0; currentSlot++) {
                ItemStack content = contents[currentSlot];
                if (content != null && BlockUtil.isShulkerBox(content.getType())) {
                    ItemMeta meta = content.getItemMeta();
                    if (meta instanceof BlockStateMeta) {
                        BlockStateMeta bsm = (BlockStateMeta) meta;
                        BlockState blockState = bsm.getBlockState();
                        if (blockState instanceof ShulkerBox) {
                            ShulkerBox shulkerBox = (ShulkerBox) blockState;
                            ItemStack[] shulkerContents = shulkerBox.getSnapshotInventory().getStorageContents();
                            int shulkerContentsLength = shulkerContents.length;
                            boolean shulkerContentChanged = false;
                            for (int currentShulkerSlot = 0; currentShulkerSlot < shulkerContentsLength && left > 0; currentShulkerSlot++) {
                                ItemStack shulkerContent = shulkerContents[currentShulkerSlot];
                                int addHere = 0;
                                int oldAmount = 0;
                                if (shulkerContent != null && item.isSimilar(shulkerContent)) {
                                    oldAmount = shulkerContent.getAmount();
                                    addHere = Math.min(left, Math.max(maxStackSize - oldAmount, 0));
                                } else if (MaterialUtil.isEmpty(shulkerContent)) {
                                    addHere = Math.min(left, maxStackSize);
                                }
                                if (addHere > 0) {
                                    left -= addHere;
                                    shulkerContent = new ItemStack(item);
                                    shulkerContent.setAmount(oldAmount + addHere);
                                    shulkerContents[currentShulkerSlot] = shulkerContent;
                                    shulkerContentChanged = true;
                                }
                            }
                            if (shulkerContentChanged) {
                                shulkerBox.getSnapshotInventory().setStorageContents(shulkerContents);
                                bsm.setBlockState(shulkerBox);
                                content.setItemMeta(meta);
                                contentChanged = true;
                            }
                        }
                    }
                }
            }
        }

        for (int currentSlot = 0; currentSlot < contentsLength && left > 0; currentSlot++) {
            ItemStack content = contents[currentSlot];
            int addHere = 0;
            int oldAmount = 0;
            if (content != null && item.isSimilar(content)) {
                oldAmount = content.getAmount();
                addHere = Math.min(left, Math.max(maxStackSize - oldAmount, 0));
            } else if (MaterialUtil.isEmpty(content)) {
                addHere = Math.min(left, maxStackSize);
            }
            if (addHere > 0) {
                left -= addHere;
                content = new ItemStack(item);
                content.setAmount(oldAmount + addHere);
                contents[currentSlot] = content;
                contentChanged = true;
            }
        }
        if (contentChanged) {
            inventory.setStorageContents(contents);
        }

        return left;
    }

    /**
     * Adds an item to the inventor
     *
     * @param item
     *            Item to add
     * @param inventory
     *            Inventory
     * @return Number of leftover items
     */
    public static int add(ItemStack item, Inventory inventory) {
        return add(item, inventory, item.getMaxStackSize());
    }

    /**
     * Removes an item from the inventory
     *
     * @param item
     *            Item to remove
     * @param inventory
     *            Inventory
     * @return Number of items that couldn't be removed
     */
    public static int remove(ItemStack item, Inventory inventory) {
        if (item.getAmount() <= 0) {
            return 0;
        }

        if (inventory.getType() == null) {
            return 0;
        }

        // Special case required because AdminInventory has no storage contents
        if (inventory instanceof AdminInventory) {
            return 0;
        }

        int left = item.getAmount();
        ItemStack[] contents = inventory.getStorageContents();
        boolean contentChanged = false;
        int contentsLength = contents.length;
        boolean isPlayerInventory = inventory instanceof PlayerInventory;
        // prefer shulker store
        if (!isPlayerInventory && BlockUtil.canBeStoredInShulkerBox(item.getType())) {
            for (int currentSlot = 0; currentSlot < contentsLength && left > 0; currentSlot++) {
                ItemStack content = contents[currentSlot];
                if (content != null && BlockUtil.isShulkerBox(content.getType())) {
                    ItemMeta meta = content.getItemMeta();
                    if (meta instanceof BlockStateMeta) {
                        BlockStateMeta bsm = (BlockStateMeta) meta;
                        BlockState blockState = bsm.getBlockState();
                        if (blockState instanceof ShulkerBox) {
                            ShulkerBox shulkerBox = (ShulkerBox) blockState;
                            ItemStack[] shulkerContents = shulkerBox.getSnapshotInventory().getStorageContents();
                            int shulkerContentsLength = shulkerContents.length;
                            boolean shulkerContentChanged = false;
                            for (int currentShulkerSlot = 0; currentShulkerSlot < shulkerContentsLength && left > 0; currentShulkerSlot++) {
                                ItemStack shulkerContent = shulkerContents[currentShulkerSlot];
                                if (shulkerContent != null && item.isSimilar(shulkerContent)) {
                                    int oldAmount = shulkerContent.getAmount();
                                    int removeHere = Math.min(left, oldAmount);
                                    left -= removeHere;
                                    if (removeHere < oldAmount) {
                                        shulkerContent = new ItemStack(item);
                                        shulkerContent.setAmount(oldAmount - removeHere);
                                        shulkerContents[currentShulkerSlot] = shulkerContent;
                                    } else {
                                        shulkerContents[currentShulkerSlot] = null;
                                    }
                                    shulkerContentChanged = true;
                                }
                            }
                            if (shulkerContentChanged) {
                                shulkerBox.getSnapshotInventory().setStorageContents(shulkerContents);
                                bsm.setBlockState(shulkerBox);
                                content.setItemMeta(meta);
                                contentChanged = true;
                            }
                        }
                    }
                }
            }
        }

        for (int currentSlot = 0; currentSlot < contentsLength && left > 0; currentSlot++) {
            ItemStack content = contents[currentSlot];
            if (content != null && item.isSimilar(content)) {
                int oldAmount = content.getAmount();
                int removeHere = Math.min(left, oldAmount);
                left -= removeHere;
                if (removeHere < oldAmount) {
                    content = new ItemStack(item);
                    content.setAmount(oldAmount - removeHere);
                    contents[currentSlot] = content;
                } else {
                    contents[currentSlot] = null;
                }
                contentChanged = true;
            }
        }
        if (contentChanged) {
            inventory.setStorageContents(contents);
        }

        return left;
    }

}
