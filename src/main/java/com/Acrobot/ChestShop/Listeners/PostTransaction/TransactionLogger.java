package com.Acrobot.ChestShop.Listeners.PostTransaction;

import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.Breeze.Utils.LocationUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.ItemNamingUtils;

/**
 * @author Acrobot
 */
public class TransactionLogger implements Listener {
    private static final String BUY_MESSAGE = "%1$s (%2$s) bought %3$s for %4$.2f from %5$s (%6$s) at %7$s";
    private static final String SELL_MESSAGE = "%1$s (%2$s) sold %3$s for %4$.2f to %5$s (%6$s) at %7$s";

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onTransaction(final TransactionEvent event) {
        String template = (event.getTransactionType() == BUY ? BUY_MESSAGE : SELL_MESSAGE);

        StringBuilder items = new StringBuilder(50);

        ItemStack item = event.getStock();
        items.append(event.getAmount()).append(' ').append(ItemNamingUtils.getDisplayName(item));

        String message = String.format(template, event.getClient().getUniqueId().toString(), event.getClient().getName(), items.toString(), event.getPrice(), event.getOwner().getUniqueId().toString(), NameManager.getFullNameFor(event.getOwner().getUniqueId()),
                LocationUtil.locationToString(event.getSign().getLocation()));

        ChestShop.getBukkitServer().getScheduler().runTaskAsynchronously(ChestShop.getPlugin(), new Runnable() {
            @Override
            public void run() {
                ChestShop.getBukkitLogger().info(message);
            }
        });
    }
}
