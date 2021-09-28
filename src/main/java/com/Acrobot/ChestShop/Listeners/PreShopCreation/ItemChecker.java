package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_ITEM;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;

/**
 * @author Acrobot
 */
public class ItemChecker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        String itemCode = ChestShopSign.getCorrectedItemCode(event.getSignLine(ITEM_LINE), event.getSign());
        if (itemCode == null) {
            event.setOutcome(INVALID_ITEM);
            return;
        }
        event.setSignLine(ITEM_LINE, itemCode);
    }
}
