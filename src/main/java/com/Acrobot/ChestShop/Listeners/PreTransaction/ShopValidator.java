package com.Acrobot.ChestShop.Listeners.PreTransaction;

import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.INVALID_SHOP;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class ShopValidator implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public static void onCheck(PreTransactionEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getStock() == null) {
            event.setCancelled(INVALID_SHOP);
            return;
        }

        if (!event.isAdminShop() && event.getOwnerInventory() == null) {
            event.setCancelled(INVALID_SHOP);
        }
    }
}
