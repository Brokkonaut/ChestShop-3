package com.Acrobot.ChestShop.Listeners.ShopRemoval;

import com.Acrobot.Breeze.Utils.LocationUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopMetaData;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

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

        ChestShopMetaData chestShopMetaData = ChestShopSign.getChestShopMetaData(event.getSign());
        if (chestShopMetaData == null)
            return;

        String shopOwner = NameManager.getFullNameFor(chestShopMetaData.getOwner());
        String typeOfShop = ChestShopSign.isAdminShop(event.getSign()) ? "An Admin Shop" : "A shop belonging to " + shopOwner;

        String item = chestShopMetaData.getQuantity() + ' ' + chestShopMetaData.getItemStack().getType().toString();
        String prices = "B " + chestShopMetaData.getBuyPrice() + " : " + chestShopMetaData.getSellPrice() + " S";
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
