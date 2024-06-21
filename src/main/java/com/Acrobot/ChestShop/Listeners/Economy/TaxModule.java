package com.Acrobot.ChestShop.Listeners.Economy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAddEvent;
import com.Acrobot.ChestShop.UUIDs.NameManager;

/**
 * @author Acrobot
 */
public class TaxModule implements Listener {

    private static BigDecimal getTax(BigDecimal price, float taxAmount) {
        return price.multiply(BigDecimal.valueOf(taxAmount).divide(BigDecimal.valueOf(100), RoundingMode.DOWN));
    }

    private static boolean isServerAccount(UUID name) {
        return NameManager.isAdminShop(name);
    }

    @EventHandler(priority = EventPriority.LOW)
    public static void onCurrencyAdd(CurrencyAddEvent event) {
        if (event.isAdded()) {
            return;
        }

        UUID target = event.getTarget();

        if (NameManager.isServerAccount(target)) {
            return;
        }

        float taxAmount = isServerAccount(target) ? Properties.SERVER_TAX_AMOUNT : Properties.TAX_AMOUNT;

        if (taxAmount == 0) {
            return;
        }

        BigDecimal tax = getTax(event.getAmount(), taxAmount);

        if (!Economy.getServerAccountName().isEmpty()) {
            CurrencyAddEvent currencyAddEvent = new CurrencyAddEvent(tax, NameManager.getServerAccountUUID(), event.getWorld());
            ChestShop.callEvent(currencyAddEvent);
        }

        event.setAmount(event.getAmount().subtract(tax));
    }
}
