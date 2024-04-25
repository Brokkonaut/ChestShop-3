package com.Acrobot.Breeze.Utils;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentNames {
    private static final HashMap<Enchantment, String> enchantmentToName;
    static {
        enchantmentToName = new HashMap<Enchantment, String>();

        addEnchantment(Enchantment.POWER, "Power");
        addEnchantment(Enchantment.FLAME, "Flame");
        addEnchantment(Enchantment.INFINITY, "Infinity");
        addEnchantment(Enchantment.PUNCH, "Punch");
        addEnchantment(Enchantment.BINDING_CURSE, ChatColor.RED + "Curse of Binding");
        addEnchantment(Enchantment.SHARPNESS, "Sharpness");
        addEnchantment(Enchantment.BANE_OF_ARTHROPODS, "Bane of Arthropods");
        addEnchantment(Enchantment.SMITE, "Smite");
        addEnchantment(Enchantment.EFFICIENCY, "Efficiency");
        addEnchantment(Enchantment.UNBREAKING, "Unbreaking");
        addEnchantment(Enchantment.FORTUNE, "Fortune");
        addEnchantment(Enchantment.LOOTING, "Looting");
        addEnchantment(Enchantment.LUCK_OF_THE_SEA, "Luck of the Sea");
        addEnchantment(Enchantment.RESPIRATION, "Respiration");
        addEnchantment(Enchantment.PROTECTION, "Protection");
        addEnchantment(Enchantment.BLAST_PROTECTION, "Blast Protection");
        addEnchantment(Enchantment.FEATHER_FALLING, "Feather Falling");
        addEnchantment(Enchantment.FIRE_PROTECTION, "Fire Protection");
        addEnchantment(Enchantment.PROJECTILE_PROTECTION, "Projectile Protection");
        addEnchantment(Enchantment.VANISHING_CURSE, ChatColor.RED + "Curse of Vanishing");
        addEnchantment(Enchantment.AQUA_AFFINITY, "Aqua Affinity");
        addEnchantment(Enchantment.LOYALTY, "Loyalty");
        addEnchantment(Enchantment.IMPALING, "Impaling");
        addEnchantment(Enchantment.RIPTIDE, "Riptide");
        addEnchantment(Enchantment.CHANNELING, "Channeling");
        addEnchantment(Enchantment.SWEEPING_EDGE, "Sweeping Edge");
        addEnchantment(Enchantment.DEPTH_STRIDER, "Depth Strider");
        addEnchantment(Enchantment.FROST_WALKER, "Frost Walker");
        addEnchantment(Enchantment.QUICK_CHARGE, "Quick Charge");
        addEnchantment(Enchantment.SOUL_SPEED, "Soul Speed");
        addEnchantment(Enchantment.SWIFT_SNEAK, "Swift Sneak");
    }

    private static void addEnchantment(Enchantment enchantment, String name) {
        enchantmentToName.put(enchantment, name);
    }

    public static String getName(Enchantment enchantment) {
        if (enchantment == null) {
            return null;
        }
        String name = enchantmentToName.get(enchantment);
        if (name != null) {
            return name;
        }
        return StringUtil.capitalizeFirstLetter(enchantment.getKey().getKey(), '_');
    }
}
