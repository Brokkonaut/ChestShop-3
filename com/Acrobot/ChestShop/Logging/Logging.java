package com.Acrobot.ChestShop.Logging;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.DB.Queue;
import com.Acrobot.ChestShop.DB.Transaction;
import com.Acrobot.ChestShop.Shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Acrobot
 */
public class Logging {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static String getDateAndTime() {
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void log(String string) {
        if (Config.getBoolean("logToConsole")) {
            System.out.println("[ChestShop] " + string);
        }
        FileWriterQueue.addToQueue(getDateAndTime() + ' ' + string);
    }

    public static void logTransaction(boolean isBuying, Shop shop, Player player) {
        log(player.getName() + (isBuying ? " bought " : " sold ") + shop.stockAmount + ' ' + shop.stock.getType() + " for " + (isBuying ? shop.buyPrice + " from " : shop.sellPrice + " to ") + shop.owner);
        if (!Config.getBoolean("useDB")) {
            return;
        }
        Transaction transaction = new Transaction();

        transaction.setAmount(shop.stockAmount);
        transaction.setBuy(isBuying);

        ItemStack stock = shop.stock;

        transaction.setItemDurability(stock.getDurability());
        transaction.setItemID(stock.getTypeId());
        transaction.setPrice((isBuying ? shop.buyPrice : shop.sellPrice));
        transaction.setSec(System.currentTimeMillis() / 1000);
        transaction.setShopOwner(shop.owner);
        transaction.setShopUser(player.getName());

        Queue.addToQueue(transaction);
    }
}