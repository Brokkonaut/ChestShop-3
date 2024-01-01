package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_ITEM;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;

/**
 * @author Acrobot
 */
public class ItemChecker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {

        ItemStack itemStack = event.getItemStack();
        if (itemStack == null) {
            event.setOutcome(INVALID_ITEM);
            return;
        }

        String itemName = MaterialUtil.getName(itemStack.getType());
        if (itemName == null) {
            event.setOutcome(INVALID_ITEM);
            return;
        }

        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta.hasDisplayName()) {
                itemName = ChatColor.stripColor(itemMeta.getDisplayName());
            } else if (!itemMeta.equals(new ItemStack(itemStack.getType()).getItemMeta())) {
                itemName = ChatColor.ITALIC + itemName;
            }
        }

        event.setItemLine(itemName);
    }
}
