package com.Acrobot.Breeze.Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public class AlternativeItemNames {
    private static final HashMap<String, Material> nameToItem;
    private static final HashMap<Material, String> itemToName;
    public static final Map<String, Material> NAME_TO_ITEM;
    static {
        nameToItem = new HashMap<>();
        itemToName = new HashMap<>();
        NAME_TO_ITEM = Collections.unmodifiableMap(nameToItem);

        addItem(Material.WITHER_SKELETON_SKULL, "WITHER_SKEL_HEAD");
        addItem(Material.STRIPPED_DARK_OAK_LOG, "STR_DARK_OAK_LOG");
        addItem(Material.STRIPPED_DARK_OAK_WOOD, "STR_DARK_OAK_WOOD");

        addItem(Material.LIGHT_GRAY_CONCRETE_POWDER, "LIGHT_GRAY_CON_POWDER");
        addItem(Material.LIGHT_BLUE_CONCRETE_POWDER, "LIGHT_BLUE_CON_POWDER");
        addItem(Material.MAGENTA_CONCRETE_POWDER, "MAGENTA_CON_POWDER");

        addItem(Material.TROPICAL_FISH_BUCKET, "TRO_FISH_BUCKET");

        addItem(Material.PRISMARINE_BRICK_SLAB, "PRISMARINE_BR_SLAB");
        addItem(Material.PRISMARINE_BRICK_STAIRS, "PRISMARINE_BR_STAIRS");
        addItem(Material.DARK_PRISMARINE_STAIRS, "DARK_PRISM_STAIRS");
        addItem(Material.DARK_PRISMARINE_SLAB, "DARK_PRISM_SLAB");

        addItem(Material.POLISHED_BLACKSTONE_WALL, "POL_BLACKSTONE_WALL");
        addItem(Material.POLISHED_BLACKSTONE_BRICK_WALL, "POL_BLACKST_B_WALL");
        addItem(Material.POLISHED_BLACKSTONE_BUTTON, "POL_BLACKST_BUTTON");
        addItem(Material.POLISHED_BLACKSTONE_SLAB, "POL_BLACKST_SLAB");
        addItem(Material.POLISHED_BLACKSTONE_STAIRS, "POL_BLACKST_STAIRS");
        addItem(Material.POLISHED_BLACKSTONE_BRICK_SLAB, "POL_BLACKST_B_SLAB");
        addItem(Material.POLISHED_BLACKSTONE_BRICK_STAIRS, "POL_BLACKST_B_STAIRS");
        addItem(Material.POLISHED_BLACKSTONE_BRICKS, "POL_BLACKST_BRICKS");
        addItem(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS, "CRACKED_POL_BLST_BRICKS");

        addItem(Material.FLOWERING_AZALEA_LEAVES, "FLOWER_AZALEA_LEAVES");
        addItem(Material.CRACKED_DEEPSLATE_BRICKS, "CR_DEEPSLATE_BRICKS");
        addItem(Material.CRACKED_DEEPSLATE_BRICKS, "CR_DEEPSLATE_BRICKS");
        addItem(Material.CRACKED_DEEPSLATE_TILES, "CR_DEEPSLATE_TILES");
        addItem(Material.COBBLED_DEEPSLATE_SLAB, "COB_DEEPSLATE_SLAB");
        addItem(Material.COBBLED_DEEPSLATE_STAIRS, "COB_DEEPSLATE_STAIRS");
        addItem(Material.COBBLED_DEEPSLATE_WALL, "COB_DEEPSLATE_WALL");
        addItem(Material.POLISHED_DEEPSLATE_SLAB, "POL_DEEPSLATE_SLAB");
        addItem(Material.POLISHED_DEEPSLATE_STAIRS, "POL_DEEPSLATE_STAIRS");
        addItem(Material.POLISHED_DEEPSLATE_WALL, "POL_DEEPSLATE_WALL");
        addItem(Material.DEEPSLATE_BRICK_SLAB, "DEEPSLATE_BR_SLAB");
        addItem(Material.DEEPSLATE_BRICK_STAIRS, "DEEPSLATE_BR_STAIRS");
        addItem(Material.EXPOSED_CUT_COPPER_STAIRS, "EXP_CUT_COPPER_STAIRS");
        addItem(Material.WEATHERED_CUT_COPPER_STAIRS, "WEA_CUT_COPPER_STAIRS");
        addItem(Material.OXIDIZED_CUT_COPPER_STAIRS, "OXI_CUT_COPPER_STAIRS");
        addItem(Material.EXPOSED_CUT_COPPER_SLAB, "EXP_CUT_COPPER_SLAB");
        addItem(Material.WEATHERED_CUT_COPPER_SLAB, "WEA_CUT_COPPER_SLAB");
        addItem(Material.OXIDIZED_CUT_COPPER_SLAB, "OXI_CUT_COPPER_SLAB");

        addItem(Material.WAXED_EXPOSED_CUT_COPPER, "WAX_EXP_CUT_COPPER");
        addItem(Material.WAXED_WEATHERED_CUT_COPPER, "WAX_WEA_CUT_COPPER");
        addItem(Material.WAXED_OXIDIZED_CUT_COPPER, "WAX_OXI_CUT_COPPER");
        addItem(Material.WAXED_CUT_COPPER_STAIRS, "WAX_CUT_COPPER_STAIRS");
        addItem(Material.WAXED_EXPOSED_CUT_COPPER_STAIRS, "WAX_EXP_CUT_COPPER_STAIRS");
        addItem(Material.WAXED_WEATHERED_CUT_COPPER_STAIRS, "WAX_WEA_CUT_COPPER_STAIRS");
        addItem(Material.WAXED_OXIDIZED_CUT_COPPER_STAIRS, "WAX_OXI_CUT_COPPER_STAIRS");
        addItem(Material.WAXED_CUT_COPPER_SLAB, "WAX_CUT_COPPER_SLAB");
        addItem(Material.WAXED_EXPOSED_CUT_COPPER_SLAB, "WAX_EXP_CUT_COPPER_SLAB");
        addItem(Material.WAXED_WEATHERED_CUT_COPPER_SLAB, "WAX_WEA_CUT_COPPER_SLAB");
        addItem(Material.WAXED_OXIDIZED_CUT_COPPER_SLAB, "WAX_OXI_CUT_COPPER_SLAB");

        for (Material mat : Material.values()) {
            String name = mat.name();
            if (!name.startsWith("LEGACY_")) {
                if (name.endsWith("_STAINED_GLASS") || name.endsWith("_STAINED_GLASS_PANE")) {
                    addItem(mat, mat.name().replace("STAINED_", ""));
                }
                if (name.startsWith("DEAD_") && name.contains("_CORAL_")) { // DEAD_*_CORAL_*
                    addItem(mat, mat.name().replace("DEAD_", "DE_"));
                }
            }
        }
    }

    private static void addItem(Material material, String name) {
        itemToName.put(material, name);
        nameToItem.put(name.toUpperCase().replace(" ", ""), material);
    }

    public static String getName(Material material) {
        return itemToName.get(material);
    }

    public static Material getItem(String uppercaseName) {
        return nameToItem.get(uppercaseName);
    }
}
