package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.Breeze.Utils.NumberUtil;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_QUANTITY;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class QuantityChecker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        String quantity = event.getQuantityLine();

        if (!NumberUtil.isInteger(quantity)) {
            event.setOutcome(INVALID_QUANTITY);
        }
    }
}
