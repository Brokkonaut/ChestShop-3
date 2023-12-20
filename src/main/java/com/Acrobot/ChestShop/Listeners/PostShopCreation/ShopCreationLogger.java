package com.Acrobot.ChestShop.Listeners.PostShopCreation;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.PRICE_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.QUANTITY_LINE;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.Acrobot.Breeze.Utils.LocationUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;

/**
 * @author Acrobot
 */
public class ShopCreationLogger implements Listener {
    private static final String CREATION_MESSAGE = "%1$s created %2$s - %3$s - %4$s - at %5$s";

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onShopCreation(final ShopCreatedEvent event) {
        String creator = event.getPlayer().getName();
        String typeOfShop = ChestShopSign.isAdminShop(event.getSign()) ? "an Admin Shop" : "a shop";

        String item = event.getSignLine(QUANTITY_LINE) + ' ' + event.getSignLine(ITEM_LINE);
        String prices = event.getSignLine(PRICE_LINE);
        String location = LocationUtil.locationToString(event.getSign().getLocation());

        String message = String.format(CREATION_MESSAGE, creator, typeOfShop, item, prices, location);

        ChestShop.getBukkitServer().getScheduler().runTaskAsynchronously(ChestShop.getPlugin(), new Runnable() {
            @Override
            public void run() {
                ChestShop.getBukkitLogger().info(message);
            }
        });
    }
}
