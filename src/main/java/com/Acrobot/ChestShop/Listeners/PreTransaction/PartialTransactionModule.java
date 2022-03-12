package com.Acrobot.ChestShop.Listeners.PreTransaction;

import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.CLIENT_DEPOSIT_FAILED;
import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.CLIENT_DOES_NOT_HAVE_ENOUGH_MONEY;
import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.NOT_ENOUGH_STOCK_IN_CHEST;
import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.NOT_ENOUGH_STOCK_IN_INVENTORY;
import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.SHOP_DEPOSIT_FAILED;
import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.SHOP_DOES_NOT_HAVE_ENOUGH_MONEY;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.SELL;

import java.math.BigDecimal;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAmountEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyCheckEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyHoldEvent;

/**
 * @author Acrobot
 */
public class PartialTransactionModule implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public static void onPreBuyTransaction(PreTransactionEvent event) {
        if (event.isCancelled() || event.getTransactionType() != BUY) {
            return;
        }

        Player client = event.getClient();
        ItemStack stock = event.getStock();

        double price = event.getPrice();
        double pricePerItem = event.getPrice() / InventoryUtil.countItems(stock);

        CurrencyAmountEvent currencyAmountEvent = new CurrencyAmountEvent(client);
        ChestShop.callEvent(currencyAmountEvent);

        BigDecimal walletMoney = currencyAmountEvent.getAmount();

        CurrencyCheckEvent currencyCheckEvent = new CurrencyCheckEvent(BigDecimal.valueOf(price), client);
        ChestShop.callEvent(currencyCheckEvent);

        if (!currencyCheckEvent.hasEnough()) {
            int amountAffordable = getAmountOfAffordableItems(walletMoney, pricePerItem);

            if (amountAffordable < 1) {
                event.setCancelled(CLIENT_DOES_NOT_HAVE_ENOUGH_MONEY);
                return;
            }

            event.setPrice(amountAffordable * pricePerItem);
            stock = new ItemStack(stock);
            stock.setAmount(amountAffordable);
            event.setStock(stock);
        }

        UUID seller = event.getOwner().getUniqueId();

        CurrencyHoldEvent currencyHoldEvent = new CurrencyHoldEvent(BigDecimal.valueOf(price), seller, client.getWorld());
        ChestShop.callEvent(currencyHoldEvent);

        if (!currencyHoldEvent.canHold()) {
            event.setCancelled(SHOP_DEPOSIT_FAILED);
            return;
        }

        stock = event.getStock();

        if (!InventoryUtil.hasItems(stock, event.getOwnerInventory())) {
            int posessedItemCount = InventoryUtil.getAmount(stock, event.getClientInventory());
            if (posessedItemCount <= 0) {
                event.setCancelled(NOT_ENOUGH_STOCK_IN_CHEST);
                return;
            }

            ItemStack itemsHad = new ItemStack(stock);
            itemsHad.setAmount(posessedItemCount);

            event.setPrice(pricePerItem * posessedItemCount);
            event.setStock(itemsHad);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public static void onPreSellTransaction(PreTransactionEvent event) {
        if (event.isCancelled() || event.getTransactionType() != SELL) {
            return;
        }

        Player client = event.getClient();
        UUID owner = event.getOwner().getUniqueId();
        ItemStack stock = event.getStock();

        double price = event.getPrice();
        double pricePerItem = event.getPrice() / InventoryUtil.countItems(stock);

        CurrencyAmountEvent currencyAmountEvent = new CurrencyAmountEvent(owner, client.getWorld());
        ChestShop.callEvent(currencyAmountEvent);

        BigDecimal walletMoney = currencyAmountEvent.getAmount();

        if (Economy.isOwnerEconomicallyActive(event.getOwnerInventory())) {
            CurrencyCheckEvent currencyCheckEvent = new CurrencyCheckEvent(BigDecimal.valueOf(price), owner, client.getWorld());
            ChestShop.callEvent(currencyCheckEvent);

            if (!currencyCheckEvent.hasEnough()) {
                int amountAffordable = getAmountOfAffordableItems(walletMoney, pricePerItem);

                if (amountAffordable < 1) {
                    event.setCancelled(SHOP_DOES_NOT_HAVE_ENOUGH_MONEY);
                    return;
                }

                event.setPrice(amountAffordable * pricePerItem);
                stock = new ItemStack(stock);
                stock.setAmount(amountAffordable);
                event.setStock(stock);
            }
        }

        stock = event.getStock();

        CurrencyHoldEvent currencyHoldEvent = new CurrencyHoldEvent(BigDecimal.valueOf(price), client);
        ChestShop.callEvent(currencyHoldEvent);

        if (!currencyHoldEvent.canHold()) {
            event.setCancelled(CLIENT_DEPOSIT_FAILED);
            return;
        }

        if (!InventoryUtil.hasItems(stock, event.getClientInventory())) {
            int posessedItemCount = InventoryUtil.getAmount(stock, event.getClientInventory());
            if (posessedItemCount <= 0) {
                event.setCancelled(NOT_ENOUGH_STOCK_IN_INVENTORY);
                return;
            }

            ItemStack itemsHad = new ItemStack(stock);
            itemsHad.setAmount(posessedItemCount);

            event.setPrice(pricePerItem * posessedItemCount);
            event.setStock(itemsHad);
        }
    }

    private static int getAmountOfAffordableItems(BigDecimal walletMoney, double pricePerItem) {
        return (int) Math.floor(walletMoney.doubleValue() / pricePerItem);
    }
}
