package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_QUANTITY;

import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class QuantityChecker implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        String quantity = event.getQuantityLine().trim();
        if (quantity.contains("!")) {
            quantity = quantity.replace("!", "").trim();
            event.setEnforceAmount(true);
        }
        if (quantity.contains("x")) {
            quantity = quantity.replace("x", "").trim();
            event.setNoAutofill(true);
        }

        try {
            int amount = Integer.parseInt(quantity);
            if (amount <= 0 || amount > 64 * 64 * 64) {
                event.setOutcome(CreationOutcome.INVALID_QUANTITY);
            } else {
                event.setAmount(amount);
                event.setQuantityLine(Integer.toString(amount));
            }
        } catch (NumberFormatException e) {
            event.setOutcome(INVALID_QUANTITY);
        }
    }
}
