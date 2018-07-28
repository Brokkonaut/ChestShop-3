package com.Acrobot.Breeze.Utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @author Acrobot
 */
public class BlockUtil {
    /**
     * Checks if the block is a sign
     *
     * @param block
     *            Block to check
     * @return Is this block a sign?
     */
    public static boolean isSign(Block block) {
        Material m = block.getType();
        return m == Material.SIGN || m == Material.WALL_SIGN;
    }

    /**
     * Checks if the block is a chest
     *
     * @param block
     *            Block to check
     * @return Is this block a chest?
     */
    public static boolean isChest(Block block) {
        Material m = block.getType();
        return m == Material.CHEST || m == Material.TRAPPED_CHEST;
    }

    /**
     * Checks if the InventoryHolder is a chest
     *
     * @param holder
     *            Inventory holder to check
     * @return Is this holder a chest?
     */
    public static boolean isChest(InventoryHolder holder) {
        return holder instanceof Chest || holder instanceof DoubleChest;
    }

    /**
     * Gets the block to which the sign is attached
     *
     * @param sign
     *            Sign which is attached
     * @return Block to which the sign is attached
     */
    public static Block getAttachedBlock(Sign sign) {
        BlockData signData = sign.getBlockData();
        if (signData instanceof WallSign) {
            return sign.getBlock().getRelative(((Directional) signData).getFacing().getOppositeFace());
        }
        return sign.getBlock().getRelative(BlockFace.DOWN);
    }

    /**
     * Opens the holder's inventory GUI
     *
     * @param holder
     *            Inventory holder
     * @param player
     *            Player on whose screen the GUI is going to be shown
     * @return Was the opening successful?
     */
    public static boolean openBlockGUI(InventoryHolder holder, Player player) {
        Inventory inventory = holder.getInventory();
        player.openInventory(inventory);

        return true;
    }
}
