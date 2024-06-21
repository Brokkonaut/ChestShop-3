package com.Acrobot.ChestShop.Utils;

import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ComponentUtils {
    private ComponentUtils() {
        // prevent instances
    }

    public static BaseComponent getLocalizedItemName(ItemStack stack) {
        if (stack == null) {
            stack = new ItemStack(Material.AIR);
        }
        if (Properties.SHOW_TRANSLATED_ITEM_NAMES) {
            TranslatableComponent suffix = null;
            String materialName = stack.getType().name();
            if (stack.getType() == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) {
                suffix = new TranslatableComponent("upgrade.minecraft.netherite_upgrade");
            } else if (materialName.endsWith("_SMITHING_TEMPLATE")) {
                suffix = new TranslatableComponent("trim_pattern.minecraft." + materialName.replace("_ARMOR_TRIM_SMITHING_TEMPLATE", "").toLowerCase());
            } else if (materialName.startsWith("MUSIC_DISC_")) {
                suffix = new TranslatableComponent("item.minecraft." + materialName.toLowerCase() + ".desc");
            }
            TranslatableComponent main = new TranslatableComponent(stack.translationKey());
            if (suffix == null) {
                return main;
            }
            return new TextComponent(main, new TextComponent(" "), suffix);
        } else {
            return new TextComponent(StringUtil.capitalizeFirstLetter(stack.getType().name(), '_'));
        }
    }
}
