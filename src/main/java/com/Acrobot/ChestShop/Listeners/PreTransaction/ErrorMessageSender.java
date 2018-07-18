package com.Acrobot.ChestShop.Listeners.PreTransaction;

import static com.Acrobot.ChestShop.Configuration.Messages.CLIENT_DEPOSIT_FAILED;
import static com.Acrobot.ChestShop.Configuration.Messages.FULL_SHOP_TO_OWNER;
import static com.Acrobot.ChestShop.Configuration.Messages.NOT_ENOUGH_STOCK_IN_YOUR_SHOP;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.Commands.Toggle;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;

/**
 * @author Acrobot
 */
public class ErrorMessageSender implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onMessage(PreTransactionEvent event) {
        if (!event.isCancelled()) {
            return;
        }

        String message = null;

        switch (event.getTransactionOutcome()) {
            case SHOP_DOES_NOT_BUY_THIS_ITEM:
                message = Messages.NO_BUYING_HERE;
                break;
            case SHOP_DOES_NOT_SELL_THIS_ITEM:
                message = Messages.NO_SELLING_HERE;
                break;
            case CLIENT_DOES_NOT_HAVE_PERMISSION:
                message = Messages.NO_PERMISSION;
                break;
            case CLIENT_DOES_NOT_HAVE_ENOUGH_MONEY:
                message = Messages.NOT_ENOUGH_MONEY;
                break;
            case SHOP_DOES_NOT_HAVE_ENOUGH_MONEY:
                message = Messages.NOT_ENOUGH_MONEY_SHOP;
                break;
            case NOT_ENOUGH_SPACE_IN_CHEST:
                if (!Toggle.isIgnoring(event.getOwner())) {
                    Location loc = event.getSign().getLocation();
                    String messageFull = Messages.prefix(FULL_SHOP_TO_OWNER).replace("%material", getItemNames(event.getStock())).replace("%buyer", event.getClient().getName()).replace("%location", loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
                    sendMessageToOwner(event.getOwner(), messageFull);
                }
                message = Messages.NOT_ENOUGH_SPACE_IN_CHEST;
                break;
            case NOT_ENOUGH_SPACE_IN_INVENTORY:
                message = Messages.NOT_ENOUGH_SPACE_IN_INVENTORY;
                break;
            case NOT_ENOUGH_STOCK_IN_INVENTORY:
                message = Messages.NOT_ENOUGH_ITEMS_TO_SELL;
                break;
            case NOT_ENOUGH_STOCK_IN_CHEST:
                if (!Toggle.isIgnoring(event.getOwner())) {
                    Location loc = event.getSign().getLocation();
                    String messageOutOfStock = Messages.prefix(NOT_ENOUGH_STOCK_IN_YOUR_SHOP).replace("%material", getItemNames(event.getStock())).replace("%buyer", event.getClient().getName()).replace("%location", loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
                    sendMessageToOwner(event.getOwner(), messageOutOfStock);
                }
                message = Messages.NOT_ENOUGH_STOCK;
                break;
            case CLIENT_DEPOSIT_FAILED:
                message = Messages.CLIENT_DEPOSIT_FAILED;
                break;
            case SHOP_DEPOSIT_FAILED:
                if (!Toggle.isIgnoring(event.getOwner())) {
                    String messageDepositFailed = Messages.prefix(CLIENT_DEPOSIT_FAILED);
                    sendMessageToOwner(event.getOwner(), messageDepositFailed);
                }
                message = Messages.SHOP_DEPOSIT_FAILED;
                break;
            case SHOP_IS_RESTRICTED:
                message = Messages.ACCESS_DENIED;
                break;
            case INVALID_SHOP:
                message = Messages.INVALID_SHOP_DETECTED;
                break;
            default:
                break;
        }

        if (message != null) {
            event.getClient().sendMessage(Messages.prefix(message));
        }
    }

    private static String getItemNames(ItemStack[] stock) {
        ItemStack[] items = InventoryUtil.mergeSimilarStacks(stock);

        StringBuilder names = new StringBuilder(50);

        for (ItemStack item : items) {
            if (names.length() > 0) {
                names.append(',').append(' ');
            }
            names.append(MaterialUtil.getName(item.getType()));
        }

        return names.toString();
    }

    private static void sendMessageToOwner(OfflinePlayer owner, String message) {
        if (owner.isOnline() && Properties.SHOW_MESSAGE_OUT_OF_STOCK) {
            Player player = (Player) owner;
            player.sendMessage(message);
        }
    }
}
