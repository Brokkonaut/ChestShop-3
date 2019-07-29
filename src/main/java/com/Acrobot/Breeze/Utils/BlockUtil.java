package com.Acrobot.Breeze.Utils;

import java.util.EnumSet;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

    private static EnumSet<Material> SIGNS = EnumSet.noneOf(Material.class);
    private static EnumSet<Material> CONTAINERS = EnumSet.noneOf(Material.class);
    static {
        SIGNS.add(Material.ACACIA_SIGN);
        SIGNS.add(Material.ACACIA_WALL_SIGN);
        SIGNS.add(Material.BIRCH_SIGN);
        SIGNS.add(Material.BIRCH_WALL_SIGN);
        SIGNS.add(Material.DARK_OAK_SIGN);
        SIGNS.add(Material.DARK_OAK_WALL_SIGN);
        SIGNS.add(Material.JUNGLE_SIGN);
        SIGNS.add(Material.JUNGLE_WALL_SIGN);
        SIGNS.add(Material.OAK_SIGN);
        SIGNS.add(Material.OAK_WALL_SIGN);
        SIGNS.add(Material.SPRUCE_SIGN);
        SIGNS.add(Material.SPRUCE_WALL_SIGN);

        CONTAINERS.add(Material.CHEST);
        CONTAINERS.add(Material.TRAPPED_CHEST);
        CONTAINERS.add(Material.BARREL);
        CONTAINERS.add(Material.SHULKER_BOX);
        CONTAINERS.add(Material.BLACK_SHULKER_BOX);
        CONTAINERS.add(Material.BLUE_SHULKER_BOX);
        CONTAINERS.add(Material.BROWN_SHULKER_BOX);
        CONTAINERS.add(Material.CYAN_SHULKER_BOX);
        CONTAINERS.add(Material.GRAY_SHULKER_BOX);
        CONTAINERS.add(Material.GREEN_SHULKER_BOX);
        CONTAINERS.add(Material.LIGHT_BLUE_SHULKER_BOX);
        CONTAINERS.add(Material.LIGHT_GRAY_SHULKER_BOX);
        CONTAINERS.add(Material.LIME_SHULKER_BOX);
        CONTAINERS.add(Material.MAGENTA_SHULKER_BOX);
        CONTAINERS.add(Material.ORANGE_SHULKER_BOX);
        CONTAINERS.add(Material.PINK_SHULKER_BOX);
        CONTAINERS.add(Material.PURPLE_SHULKER_BOX);
        CONTAINERS.add(Material.RED_SHULKER_BOX);
        CONTAINERS.add(Material.WHITE_SHULKER_BOX);
        CONTAINERS.add(Material.YELLOW_SHULKER_BOX);
    }

    /**
     * Checks if the material is a sign
     *
     * @param material
     *            Material to check
     * @return Is this material a sign?
     */
    public static boolean isSign(Material material) {
        return SIGNS.contains(material);
    }

    /**
     * Checks if the block is a sign
     *
     * @param block
     *            Block to check
     * @return Is this block a sign?
     */
    public static boolean isSign(Block block) {
        return isSign(block.getType());
    }

    /**
     * Checks if the block is a chest
     *
     * @param block
     *            Block to check
     * @return Is this block a chest?
     */
    public static boolean isChest(Block block) {
        return block != null && CONTAINERS.contains(block.getType());
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
