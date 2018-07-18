package com.Acrobot.Breeze.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.Acrobot.ChestShop.ChestShop;

/**
 * @author Acrobot
 */
public class MaterialUtil {
    private static final Map<String, Material> MATERIAL_CACHE = new HashMap<String, Material>();

    /**
     * Checks if the itemStack is empty or null
     *
     * @param item
     *            Item to check
     * @return Is the itemStack empty?
     */
    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    /**
     * Gives you a Material from a String (doesn't have to be fully typed in)
     *
     * @param name
     *            Name of the material
     * @return Material found
     */
    private static Material getMaterial(String name) {
        String formatted = name.replaceAll(" |_", "").toUpperCase();

        if (MATERIAL_CACHE.containsKey(formatted)) {
            return MATERIAL_CACHE.get(formatted);
        }

        Material material = AlternativeItemNames.getItem(formatted);
        if (material == null) {
            material = Material.matchMaterial(name);
        }
        if (material != null) {
            MATERIAL_CACHE.put(formatted, material);
            return material;
        }

        short length = Short.MAX_VALUE;

        for (Entry<String, Material> e : AlternativeItemNames.NAME_TO_ITEM.entrySet()) {
            String matName = e.getKey();
            if (matName.length() < length && matName.replace("_", "").startsWith(formatted)) {
                length = (short) matName.length();
                material = e.getValue();
            }
        }
        for (Material currentMaterial : Material.values()) {
            String matName = currentMaterial.name();

            if (matName.length() < length && matName.replace("_", "").startsWith(formatted)) {
                length = (short) matName.length();
                material = currentMaterial;
            }
        }

        MATERIAL_CACHE.put(formatted, material);

        return material;
    }

    /**
     * Returns item's name
     *
     * @param itemStack
     *            ItemStack to name
     * @param showDataValue
     *            Should we also show the data value?
     * @return ItemStack's name
     */
    public static String getName(Material material) {
        String name = AlternativeItemNames.getName(material);
        if (name == null) {
            name = material.name();
        }
        return StringUtil.capitalizeFirstLetter(name, '_');
    }

    /**
     * Returns item's name, just like on the sign
     *
     * @param itemStack
     *            ItemStack to name
     * @return ItemStack's name
     */
    public static String getSignName(ItemStack itemStack) {
        StringBuilder name = new StringBuilder(15);

        name.append(getName(itemStack.getType()));
        if (itemStack.hasItemMeta()) {
            name.append('#').append(Metadata.getItemCode(itemStack));
        }

        return name.toString();
    }

    /**
     * Gives you an ItemStack from a String
     *
     * @param itemName
     *            Item name
     * @return ItemStack
     */
    public static ItemStack getItem(String itemName) {
        ItemStack itemStack;

        String materialString = itemName;
        String metaString = null;
        int metaStart = itemName.indexOf('#');
        if (metaStart >= 0) {
            materialString = itemName.substring(0, metaStart);
            metaString = itemName.substring(metaStart + 1);
        }

        Material material = getMaterial(materialString);

        if (material == null) {
            return null;
        }

        itemStack = new ItemStack(material);
        if (metaString != null) {
            ItemMeta meta = Metadata.getFromCode(metaString);
            if (meta != null) {
                itemStack.setItemMeta(meta);
            }
        }

        return itemStack;
    }

    public static class Metadata {
        public static String getMetaCodeFromItemCode(String itemName) {
            int metaStart = itemName.indexOf('#');
            if (metaStart >= 0) {
                return itemName.substring(metaStart + 1);
            }
            return null;
        }

        /**
         * Returns the ItemStack represented by this code
         *
         * @param code
         *            Code representing the item
         * @return Item represented by code
         */
        public static ItemMeta getFromCode(String code) {
            ItemStack item = ChestShop.getItemDatabase().getFromCode(code);

            if (item == null) {
                return null;
            } else {
                return item.getItemMeta();
            }
        }

        /**
         * Returns the code for this item
         *
         * @param item
         *            Item being represented
         * @return Code representing the item
         */
        public static String getItemCode(ItemStack item) {
            return ChestShop.getItemDatabase().getItemCode(item);
        }
    }
}
