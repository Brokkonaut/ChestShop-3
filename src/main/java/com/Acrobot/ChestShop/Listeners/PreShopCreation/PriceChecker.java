package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.Acrobot.Breeze.Utils.PriceUtil.isPrice;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_PRICE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.PRICE_LINE;

/**
 * @author Acrobot
 */
public class PriceChecker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        String line = event.getSignLine(PRICE_LINE).toUpperCase();
        line = line.replaceAll("(\\.\\d*?[1-9])0+", "$1"); // remove trailing zeroes

        String[] part = line.split(":");

        if (part.length > 2) {
            event.setOutcome(INVALID_PRICE);
            return;
        }

        String buyPriceString = null;
        String sellPriceString = null;
        if (part[0].contains("S")) {
            sellPriceString = part[0].trim();
            if (part.length > 1) {
                buyPriceString = part[1].trim();
            }
        } else {
            buyPriceString = part[0].trim();
            if (part.length > 1) {
                sellPriceString = part[1].trim();
            }
        }

        if (buyPriceString != null) {
            if (buyPriceString.isEmpty()) {
                buyPriceString = null;
            } else {
                buyPriceString = buyPriceString.replace("B", "").trim();
                if (!isPrice(buyPriceString)) {
                    event.setOutcome(INVALID_PRICE);
                    return;
                }
            }
        }

        if (sellPriceString != null) {
            if (sellPriceString.isEmpty()) {
                sellPriceString = null;
            } else {
                sellPriceString = sellPriceString.replace("S", "").trim();
                if (!isPrice(sellPriceString)) {
                    event.setOutcome(INVALID_PRICE);
                    return;
                }
            }
        }

        if (buyPriceString != null && sellPriceString != null) {
            line = "B " + buyPriceString + " : " + sellPriceString + " S";
            if (line.length() > 16) {
                line = "B" + buyPriceString + " : " + sellPriceString + "S";
            }
        } else if (buyPriceString != null) {
            line = "B " + buyPriceString;
        } else if (sellPriceString != null) {
            line = sellPriceString + " S";
        } else {
            event.setOutcome(INVALID_PRICE);
            return;
        }

        if (line.length() > 16) {
            line = line.replace(" ", "");
        }

        if (line.length() > 16) {
            event.setOutcome(INVALID_PRICE);
            return;
        }

        if (!PriceUtil.hasBuyPrice(line) && !PriceUtil.hasSellPrice(line)) {
            event.setOutcome(INVALID_PRICE);
        }

        event.setSignLine(PRICE_LINE, line);
    }
}
