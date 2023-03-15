package com.Acrobot.Breeze.Utils;

import java.util.HashSet;
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

    private static HashSet<Material> SIGNS = new HashSet<>();
    private static HashSet<Material> SHULKER_BOXES = new HashSet<>();
    private static HashSet<Material> CONTAINERS = new HashSet<>();
    private static HashSet<Material> SIGN_EDIT_MATERIALS = new HashSet<>();
    static {
        for (Material m : Material.values()) {
            if (m.data == org.bukkit.block.data.type.Sign.class || m.data == WallSign.class) {
                SIGNS.add(m);
            }
        }

        SHULKER_BOXES.add(Material.SHULKER_BOX);
        SHULKER_BOXES.add(Material.BLACK_SHULKER_BOX);
        SHULKER_BOXES.add(Material.BLUE_SHULKER_BOX);
        SHULKER_BOXES.add(Material.BROWN_SHULKER_BOX);
        SHULKER_BOXES.add(Material.CYAN_SHULKER_BOX);
        SHULKER_BOXES.add(Material.GRAY_SHULKER_BOX);
        SHULKER_BOXES.add(Material.GREEN_SHULKER_BOX);
        SHULKER_BOXES.add(Material.LIGHT_BLUE_SHULKER_BOX);
        SHULKER_BOXES.add(Material.LIGHT_GRAY_SHULKER_BOX);
        SHULKER_BOXES.add(Material.LIME_SHULKER_BOX);
        SHULKER_BOXES.add(Material.MAGENTA_SHULKER_BOX);
        SHULKER_BOXES.add(Material.ORANGE_SHULKER_BOX);
        SHULKER_BOXES.add(Material.PINK_SHULKER_BOX);
        SHULKER_BOXES.add(Material.PURPLE_SHULKER_BOX);
        SHULKER_BOXES.add(Material.RED_SHULKER_BOX);
        SHULKER_BOXES.add(Material.WHITE_SHULKER_BOX);
        SHULKER_BOXES.add(Material.YELLOW_SHULKER_BOX);

        CONTAINERS.add(Material.CHEST);
        CONTAINERS.add(Material.TRAPPED_CHEST);
        CONTAINERS.add(Material.BARREL);
        CONTAINERS.addAll(SHULKER_BOXES);

        SIGN_EDIT_MATERIALS.add(Material.INK_SAC);
        SIGN_EDIT_MATERIALS.add(Material.GLOW_INK_SAC);
        SIGN_EDIT_MATERIALS.add(Material.BLACK_DYE);
        SIGN_EDIT_MATERIALS.add(Material.BLUE_DYE);
        SIGN_EDIT_MATERIALS.add(Material.BROWN_DYE);
        SIGN_EDIT_MATERIALS.add(Material.CYAN_DYE);
        SIGN_EDIT_MATERIALS.add(Material.GRAY_DYE);
        SIGN_EDIT_MATERIALS.add(Material.GREEN_DYE);
        SIGN_EDIT_MATERIALS.add(Material.LIGHT_BLUE_DYE);
        SIGN_EDIT_MATERIALS.add(Material.LIGHT_GRAY_DYE);
        SIGN_EDIT_MATERIALS.add(Material.LIME_DYE);
        SIGN_EDIT_MATERIALS.add(Material.MAGENTA_DYE);
        SIGN_EDIT_MATERIALS.add(Material.ORANGE_DYE);
        SIGN_EDIT_MATERIALS.add(Material.PINK_DYE);
        SIGN_EDIT_MATERIALS.add(Material.PURPLE_DYE);
        SIGN_EDIT_MATERIALS.add(Material.RED_DYE);
        SIGN_EDIT_MATERIALS.add(Material.WHITE_DYE);
        SIGN_EDIT_MATERIALS.add(Material.YELLOW_DYE);
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
     * Checks if the material is a shulker box
     *
     * @param type
     *            Material to check
     * @return Is this material a shulker box?
     */
    public static boolean isShulkerBox(Material type) {
        return SHULKER_BOXES.contains(type);
    }

    /**
     * Checks if the material can be stored in a shulker box
     *
     * @param type
     *            Material to check
     * @return If this item can be stored in a shulker box
     */
    public static boolean canBeStoredInShulkerBox(Material type) {
        return !SHULKER_BOXES.contains(type) && type != Material.FILLED_MAP;
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

    public static boolean isSignEditMaterial(Material m) {
        return SIGN_EDIT_MATERIALS.contains(m);
    }
}
