package com.Acrobot.ChestShop.Listeners.PostTransaction;

import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.SELL;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.TransactionEvent;

/**
 * @author Acrobot
 */
public class ItemManager implements Listener {
    @EventHandler
    public static void shopItemRemover(TransactionEvent event) {
        if (event.getTransactionType() != BUY) {
            return;
        }

        removeItems(event.getOwnerInventory(), event.getStock(), event.getAmount());
        addItems(event.getClientInventory(), event.getStock(), event.getAmount());

        event.getClient().updateInventory();
    }

    @EventHandler
    public static void inventoryItemRemover(TransactionEvent event) {
        if (event.getTransactionType() != SELL) {
            return;
        }

        removeItems(event.getClientInventory(), event.getStock(), event.getAmount());
        addItems(event.getOwnerInventory(), event.getStock(), event.getAmount());

        event.getClient().updateInventory();
    }

    private static void removeItems(Inventory inventory, ItemStack item, int amount) {
        InventoryUtil.remove(item, amount, inventory);
    }

    private static void addItems(Inventory inventory, ItemStack item, int amount) {
        InventoryUtil.add(item, amount, inventory, Properties.STACK_TO_64);
    }
}
