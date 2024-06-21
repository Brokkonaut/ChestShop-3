package com.Acrobot.Breeze.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class MaterialUtil {
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
}
