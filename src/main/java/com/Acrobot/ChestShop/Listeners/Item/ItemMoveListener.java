package com.Acrobot.ChestShop.Listeners.Item;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import com.Acrobot.ChestShop.Signs.ChestShopSign;

/**
 * @author Acrobot
 */
public class ItemMoveListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public static void onItemMove(InventoryMoveItemEvent event) {
        if (event.getSource() == null) {
            return;
        }

        if (!ChestShopSign.isShopChest(event.getSource().getHolder())) {
            return;
        }

        event.setCancelled(true);
    }
}
