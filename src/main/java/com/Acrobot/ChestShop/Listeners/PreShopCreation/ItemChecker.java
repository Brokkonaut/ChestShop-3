package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_ITEM;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.PreShopCreationItemDisplayNameEvent;

/**
 * @author Acrobot
 */
public class ItemChecker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {

        String signItemName = getSignItemName(event.getItemStack());
        if (signItemName == null) {
            event.setOutcome(INVALID_ITEM);
            return;
        }

        event.setItemLine(signItemName);
    }

    public static String getSignItemName(ItemStack itemStack) {

        if (itemStack == null) {
            return null;
        }

        String itemName = MaterialUtil.getSignMaterialName(itemStack.getType());
        boolean needsItalicEffect = false;

        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (!itemMeta.equals(new ItemStack(itemStack.getType()).getItemMeta())) {
                needsItalicEffect = true;
            }
        }

        PreShopCreationItemDisplayNameEvent preShopCreationItemDisplayNameEvent = new PreShopCreationItemDisplayNameEvent(itemStack,
                itemName);
        ChestShop.callEvent(preShopCreationItemDisplayNameEvent);
        itemName = preShopCreationItemDisplayNameEvent.getDisplayName();

        return (needsItalicEffect ? ChatColor.ITALIC : "") + itemName.substring(0, Math.min(itemName.length(), 15));
    }
}
