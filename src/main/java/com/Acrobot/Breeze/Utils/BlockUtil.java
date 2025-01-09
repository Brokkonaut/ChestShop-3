package com.Acrobot.Breeze.Utils;

import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @author Acrobot
 */
public class BlockUtil {

    private static HashSet<Material> SIGNS = new HashSet<>();
    private static HashSet<Material> SHULKER_BOXES = new HashSet<>();
    private static HashSet<Material> BUNDLES = new HashSet<>();
    private static HashSet<Material> CONTAINERS = new HashSet<>();
    private static HashSet<Material> SIGN_EDIT_MATERIALS = new HashSet<>();
    private static HashMap<Material, DyeColor> DYE_MATERIAL_TO_COLOR = new HashMap<>();
    static {
        for (Material m : Material.values()) {
            if (m.data == org.bukkit.block.data.type.Sign.class || m.data == WallSign.class) {
                SIGNS.add(m);
            }
        }

        SHULKER_BOXES.addAll(Tag.SHULKER_BOXES.getValues());
        BUNDLES.addAll(Tag.ITEMS_BUNDLES.getValues());

        CONTAINERS.add(Material.CHEST);
        CONTAINERS.add(Material.TRAPPED_CHEST);
        CONTAINERS.add(Material.BARREL);
        CONTAINERS.addAll(SHULKER_BOXES);

        SIGN_EDIT_MATERIALS.add(Material.HONEYCOMB);
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

        DYE_MATERIAL_TO_COLOR.put(Material.BLACK_DYE, DyeColor.BLACK);
        DYE_MATERIAL_TO_COLOR.put(Material.BLUE_DYE, DyeColor.BLUE);
        DYE_MATERIAL_TO_COLOR.put(Material.BROWN_DYE, DyeColor.BROWN);
        DYE_MATERIAL_TO_COLOR.put(Material.CYAN_DYE, DyeColor.CYAN);
        DYE_MATERIAL_TO_COLOR.put(Material.GRAY_DYE, DyeColor.GRAY);
        DYE_MATERIAL_TO_COLOR.put(Material.GREEN_DYE, DyeColor.GREEN);
        DYE_MATERIAL_TO_COLOR.put(Material.LIGHT_BLUE_DYE, DyeColor.LIGHT_BLUE);
        DYE_MATERIAL_TO_COLOR.put(Material.LIGHT_GRAY_DYE, DyeColor.LIGHT_GRAY);
        DYE_MATERIAL_TO_COLOR.put(Material.LIME_DYE, DyeColor.LIME);
        DYE_MATERIAL_TO_COLOR.put(Material.MAGENTA_DYE, DyeColor.MAGENTA);
        DYE_MATERIAL_TO_COLOR.put(Material.ORANGE_DYE, DyeColor.ORANGE);
        DYE_MATERIAL_TO_COLOR.put(Material.PINK_DYE, DyeColor.PINK);
        DYE_MATERIAL_TO_COLOR.put(Material.PURPLE_DYE, DyeColor.PURPLE);
        DYE_MATERIAL_TO_COLOR.put(Material.RED_DYE, DyeColor.RED);
        DYE_MATERIAL_TO_COLOR.put(Material.WHITE_DYE, DyeColor.WHITE);
        DYE_MATERIAL_TO_COLOR.put(Material.YELLOW_DYE, DyeColor.YELLOW);
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
     * Checks if the material is a bundle
     *
     * @param type
     *            Material to check
     * @return Is this material a bundle?
     */
    public static boolean isBundle(Material type) {
        return BUNDLES.contains(type);
    }

    /**
     * Checks if the material is a shulker box or bundle
     *
     * @param type
     *            Material to check
     * @return Is this material a shulker box or bundle?
     */
    public static boolean isShulkerBoxOrBundle(Material type) {
        return isShulkerBox(type) || isBundle(type);
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

    public static boolean isNotCurrentlyActiveSignEditMaterial(Sign sign, SignSide signSide, Material m) {
        if (m == Material.GLOW_INK_SAC && signSide.isGlowingText()) {
            return false;
        }
        if (m == Material.INK_SAC && !signSide.isGlowingText()) {
            return false;
        }
        if (m == Material.HONEYCOMB && sign.isWaxed()) {
            return false;
        }
        DyeColor dyeColor = DYE_MATERIAL_TO_COLOR.get(m);
        DyeColor signColor = signSide.getColor();
        if (signColor == null) {
            signColor = DyeColor.BLACK;
        }
        if (dyeColor != null && dyeColor == signColor) {
            return false;
        }
        return SIGN_EDIT_MATERIALS.contains(m);
    }
}
