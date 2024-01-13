package com.Acrobot.Breeze.Utils;

import java.util.HashMap;

import org.bukkit.potion.PotionType;

public class PotionNames {
    private static final HashMap<PotionType, String> potionToName;
    static {
        potionToName = new HashMap<PotionType, String>();

        addEnchantment(PotionType.UNCRAFTABLE, "Uncraftable Potion");
        addEnchantment(PotionType.WATER, "Water Bottle");
        addEnchantment(PotionType.MUNDANE, "Mundane Potion");
        addEnchantment(PotionType.THICK, "Thick Potion");
        addEnchantment(PotionType.AWKWARD, "Awkward Potion");

        addEnchantment(PotionType.NIGHT_VISION, "Potion of Night Vision");
        addEnchantment(PotionType.LONG_NIGHT_VISION, "Extended Potion of Night Vision");
        addEnchantment(PotionType.INVISIBILITY, "Potion of Invisibility");
        addEnchantment(PotionType.LONG_INVISIBILITY, "Extended Potion of Invisibility");
        addEnchantment(PotionType.JUMP, "Potion of Leaping");
        addEnchantment(PotionType.LONG_LEAPING, "Extended Potion of Leaping");
        addEnchantment(PotionType.STRONG_LEAPING, "Potion of Leaping II");
        addEnchantment(PotionType.FIRE_RESISTANCE, "Potion of Fire Resistance");
        addEnchantment(PotionType.LONG_FIRE_RESISTANCE, "Extended Potion of Fire Resistance");
        addEnchantment(PotionType.SPEED, "Potion of Swiftness");
        addEnchantment(PotionType.LONG_SWIFTNESS, "Extended Potion of Swiftness");
        addEnchantment(PotionType.STRONG_SWIFTNESS, "Potion of Swiftness II");
        addEnchantment(PotionType.SLOWNESS, "Potion of Slowness");
        addEnchantment(PotionType.LONG_SLOWNESS, "Extended Potion of Slowness");
        addEnchantment(PotionType.STRONG_SLOWNESS, "Potion of Slowness IV");
        addEnchantment(PotionType.WATER_BREATHING, "Potion of Water Breathing");
        addEnchantment(PotionType.LONG_WATER_BREATHING, "Extended Potion of Water Breathing");
        addEnchantment(PotionType.INSTANT_HEAL, "Potion of Healing");
        addEnchantment(PotionType.STRONG_HEALING, "Potion of Healing II");
        addEnchantment(PotionType.INSTANT_DAMAGE, "Potion of Harming");
        addEnchantment(PotionType.STRONG_HARMING, "Potion of Harming II");
        addEnchantment(PotionType.POISON, "Potion of Poison");
        addEnchantment(PotionType.LONG_POISON, "Extended Potion of Poison");
        addEnchantment(PotionType.STRONG_POISON, "Potion of Poison II");
        addEnchantment(PotionType.REGEN, "Potion of Regeneration");
        addEnchantment(PotionType.LONG_REGENERATION, "Extended Potion of Regeneration");
        addEnchantment(PotionType.STRONG_REGENERATION, "Potion of Regeneration II");
        addEnchantment(PotionType.STRENGTH, "Potion of Strength");
        addEnchantment(PotionType.LONG_STRENGTH, "Extended Potion of Strength");
        addEnchantment(PotionType.STRONG_STRENGTH, "Potion of Strength II");
        addEnchantment(PotionType.WEAKNESS, "Potion of Weakness");
        addEnchantment(PotionType.LONG_WEAKNESS, "Extended Potion of Weakness");
        addEnchantment(PotionType.LUCK, "Potion of Luck");
        addEnchantment(PotionType.TURTLE_MASTER, "Potion of the Turtle Master");
        addEnchantment(PotionType.LONG_TURTLE_MASTER, "Extended Potion of the Turtle Master");
        addEnchantment(PotionType.STRONG_TURTLE_MASTER, "Potion of the Turtle Master II");
        addEnchantment(PotionType.SLOW_FALLING, "Potion of Slow Falling");
        addEnchantment(PotionType.LONG_SLOW_FALLING, "Extended Potion of Slow Falling");
    }

    private static void addEnchantment(PotionType potion, String name) {
        potionToName.put(potion, name);
    }

    public static String getName(PotionType potion) {
        if (potion == null) {
            return null;
        }
        String name = potionToName.get(potion);
        if (name != null) {
            return name;
        }
        return StringUtil.capitalizeFirstLetter(potion.name(), '_');
    }
}
