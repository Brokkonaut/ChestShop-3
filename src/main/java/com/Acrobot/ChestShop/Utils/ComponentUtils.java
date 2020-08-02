package com.Acrobot.ChestShop.Utils;

import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;

public class ComponentUtils {
    private ComponentUtils() {
        // prevent instances
    }

    public static BaseComponent getLocalizedItemName(Material m) {
        if (Properties.SHOW_TRANSLATED_ITEM_NAMES) {
            return new TranslatableComponent((m.isBlock() ? "block.minecraft." : "item.minecraft.") + m.getKey().getKey());
        } else {
            return new TextComponent(StringUtil.capitalizeFirstLetter(m.name(), '_'));
        }
    }
}
