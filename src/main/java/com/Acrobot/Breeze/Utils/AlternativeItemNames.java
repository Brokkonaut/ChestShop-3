package com.Acrobot.Breeze.Utils;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AlternativeItemNames {
    private static final HashMap<String, ItemStack> nameToItem;
    private static final HashMap<String, String> itemIdToName;
    static {
        nameToItem = new HashMap<String, ItemStack>();
        itemIdToName = new HashMap<String, String>();

        addItem(Material.STONE, 0, "Stone");
        addItem(Material.STONE, 1, "Granite");
        addItem(Material.STONE, 2, "Polished Granite");
        addItem(Material.STONE, 3, "Diorite");
        addItem(Material.STONE, 4, "Polished Diorite");
        addItem(Material.STONE, 5, "Andesite");
        addItem(Material.STONE, 6, "Polished Andesite");

        addItem(Material.DIRT, 0, "Dirt");
        addItem(Material.DIRT, 1, "Coarse Dirt");
        addItem(Material.DIRT, 2, "Podzol");

        addItem(Material.LOG, 0, "Oak Log");
        addItem(Material.LOG, 1, "Spruce Log");
        addItem(Material.LOG, 2, "Birch Log");
        addItem(Material.LOG, 3, "Jungle Log");
        addItem(Material.LOG_2, 0, "Acacia Log");
        addItem(Material.LOG_2, 1, "Dark Oak Log");

        addItem(Material.LEAVES, 0, "Oak Leaves");
        addItem(Material.LEAVES, 1, "Spruce Leaves");
        addItem(Material.LEAVES, 2, "Birch Leaves");
        addItem(Material.LEAVES, 3, "Jungle Leaves");
        addItem(Material.LEAVES_2, 0, "Acacia Leaves");
        addItem(Material.LEAVES_2, 1, "Dark Oak Leaves");

        addItem(Material.SAPLING, 0, "Oak Sapling");
        addItem(Material.SAPLING, 1, "Spruce Sapling");
        addItem(Material.SAPLING, 2, "Birch Sapling");
        addItem(Material.SAPLING, 3, "Jungle Sapling");
        addItem(Material.SAPLING, 4, "Acacia Sapling");
        addItem(Material.SAPLING, 5, "Dark Oak Sapling");

        addItem(Material.WOOD, 0, "Oak Wood");
        addItem(Material.WOOD, 1, "Spruce Wood");
        addItem(Material.WOOD, 2, "Birch Wood");
        addItem(Material.WOOD, 3, "Jungle Wood");
        addItem(Material.WOOD, 4, "Acacia Wood");
        addItem(Material.WOOD, 5, "Dark Oak Wood");

        addItem(Material.SAND, 0, "Sand");
        addItem(Material.SAND, 1, "Red Sand");

        addItem(Material.PRISMARINE, 0, "Prismarine");
        addItem(Material.PRISMARINE, 1, "Prismarine Bricks");
        addItem(Material.PRISMARINE, 2, "Dark Prismarine");

        addItem(Material.YELLOW_FLOWER, 0, "Dandelion");
        addItem(Material.RED_ROSE, 0, "Poppy");
        addItem(Material.RED_ROSE, 1, "Blue Orchid");
        addItem(Material.RED_ROSE, 2, "Allium");
        addItem(Material.RED_ROSE, 3, "Azure Bluet");
        addItem(Material.RED_ROSE, 4, "Red Tulip");
        addItem(Material.RED_ROSE, 5, "Orange Tulip");
        addItem(Material.RED_ROSE, 6, "White Tulip");
        addItem(Material.RED_ROSE, 7, "Pink Tulip");
        addItem(Material.RED_ROSE, 8, "Oxeye Daisy");

        addItem(Material.DOUBLE_PLANT, 0, "Sunflower");
        addItem(Material.DOUBLE_PLANT, 1, "Lilac");
        addItem(Material.DOUBLE_PLANT, 2, "Double Tallgrass");
        addItem(Material.DOUBLE_PLANT, 3, "Large Fern");
        addItem(Material.DOUBLE_PLANT, 4, "Rose Bush");
        addItem(Material.DOUBLE_PLANT, 5, "Peony");

        addItem(Material.LONG_GRASS, 0, "Shrub");
        addItem(Material.LONG_GRASS, 1, "Tall Grass");
        addItem(Material.LONG_GRASS, 2, "Fern");

        addItem(Material.SPONGE, 0, "Sponge");
        addItem(Material.SPONGE, 1, "Wet Sponge");

        addItem(Material.INK_SACK, 1, "Red Dye");
        addItem(Material.INK_SACK, 2, "Green Dye");
        addItem(Material.INK_SACK, 3, "Cocoa Beans");
        addItem(Material.INK_SACK, 4, "Lapis Lazuli");
        addItem(Material.INK_SACK, 5, "Purple Dye");
        addItem(Material.INK_SACK, 6, "Cyan Dye");
        addItem(Material.INK_SACK, 7, "Light Gray Dye");
        addItem(Material.INK_SACK, 8, "Gray Dye");
        addItem(Material.INK_SACK, 9, "Pink Dye");
        addItem(Material.INK_SACK, 10, "Lime Dye");
        addItem(Material.INK_SACK, 11, "Yellow Dye");
        addItem(Material.INK_SACK, 12, "Light Blue Dye");
        addItem(Material.INK_SACK, 13, "Magenta Dye");
        addItem(Material.INK_SACK, 14, "Orange Dye");
        addItem(Material.INK_SACK, 15, "Bone Meal");

        addItem(Material.COAL, 1, "Charcoal");

        addItem(Material.SKULL_ITEM, 1, "Witherskel Head");
        addItem(Material.SKULL_ITEM, 5, "Dragon Head");
    }

    private static void addItem(Material material, int data, String name) {
        String idString = material.getId() + ":" + data;
        if (!itemIdToName.containsKey(idString)) {
            itemIdToName.put(idString, name);
        }
        ItemStack is = new ItemStack(material, 1, (short) data);
        nameToItem.put(name.toLowerCase().replace(" ", ""), is);
    }

    public static String getName(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        String idString = stack.getType().getId() + ":" + stack.getData().getData();
        return itemIdToName.get(idString);
    }

    public static ItemStack getItem(String name) {
        if (name == null) {
            return null;
        }
        ItemStack is = nameToItem.get(name.toLowerCase().replace(" ", ""));
        if (is == null) {
            return null;
        }
        return is.clone();
    }
}
