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
