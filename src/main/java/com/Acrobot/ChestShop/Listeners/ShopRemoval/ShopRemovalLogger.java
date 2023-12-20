package com.Acrobot.ChestShop.Listeners.ShopRemoval;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.PRICE_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.QUANTITY_LINE;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.Acrobot.Breeze.Utils.LocationUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;

/**
 * @author Acrobot
 */
public class ShopRemovalLogger implements Listener {
    private static final String REMOVAL_MESSAGE = "%1$s was removed - %2$s - %3$s - at %4$s";

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onShopRemoval(final ShopDestroyedEvent event) {
        if (event.getDestroyer() != null) {
            return;
        }

        String shopOwner = NameManager.getFullNameFor(ChestShopSign.getOwnerUUID(event.getSign()));
        String typeOfShop = ChestShopSign.isAdminShop(event.getSign()) ? "An Admin Shop" : "A shop belonging to " + shopOwner;

        String item = event.getSign().getLine(QUANTITY_LINE) + ' ' + event.getSign().getLine(ITEM_LINE);
        String prices = event.getSign().getLine(PRICE_LINE);
        String location = LocationUtil.locationToString(event.getSign().getLocation());

        String message = String.format(REMOVAL_MESSAGE, typeOfShop, item, prices, location);

        ChestShop.getBukkitServer().getScheduler().runTaskAsynchronously(ChestShop.getPlugin(), new Runnable() {
            @Override
            public void run() {
                ChestShop.getBukkitLogger().info(message);
            }
        });
    }
}
