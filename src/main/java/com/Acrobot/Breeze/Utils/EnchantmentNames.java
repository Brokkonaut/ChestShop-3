package com.Acrobot.Breeze.Utils;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentNames {
    private static final HashMap<Enchantment, String> enchantmentToName;
    static {
        enchantmentToName = new HashMap<Enchantment, String>();

        addEnchantment(Enchantment.ARROW_DAMAGE, "Power");
        addEnchantment(Enchantment.ARROW_FIRE, "Flame");
        addEnchantment(Enchantment.ARROW_INFINITE, "Infinity");
        addEnchantment(Enchantment.ARROW_KNOCKBACK, "Punch");
        addEnchantment(Enchantment.BINDING_CURSE, ChatColor.RED + "Curse of Binding");
        addEnchantment(Enchantment.DAMAGE_ALL, "Sharpness");
        addEnchantment(Enchantment.DAMAGE_ARTHROPODS, "Bane of Arthropods");
        addEnchantment(Enchantment.DAMAGE_UNDEAD, "Smite");
        addEnchantment(Enchantment.DIG_SPEED, "Efficiency");
        addEnchantment(Enchantment.DURABILITY, "Unbreaking");
        addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, "Fortune");
        addEnchantment(Enchantment.LOOT_BONUS_MOBS, "Looting");
        addEnchantment(Enchantment.LUCK, "Luck of the Sea");
        addEnchantment(Enchantment.OXYGEN, "Respiration");
        addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection");
        addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, "Blast Protection");
        addEnchantment(Enchantment.PROTECTION_FALL, "Feather Falling");
        addEnchantment(Enchantment.PROTECTION_FIRE, "Fire Protection");
        addEnchantment(Enchantment.PROTECTION_PROJECTILE, "Projectile Protection");
        addEnchantment(Enchantment.VANISHING_CURSE, ChatColor.RED + "Curse of Vanishing");
        addEnchantment(Enchantment.WATER_WORKER, "Aqua Affinity");
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
