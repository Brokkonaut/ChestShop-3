package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.NO_CHEST;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.NO_PERMISSION_FOR_CHEST;
import com.Acrobot.ChestShop.Permission;
import static com.Acrobot.ChestShop.Permission.ADMIN;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class ChestChecker implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public static void onPreShopCreation(PreShopCreationEvent event) {

        if (ChestShopSign.isAdminshopLine(event.getOwnerName())) {
            return;
        }

        Container connectedChest = uBlock.findConnectedChest(event.getSign());

        if (connectedChest == null) {
            event.setOutcome(NO_CHEST);
            return;
        }

        Player player = event.getPlayer();

        if (Permission.has(player, ADMIN)) {
            return;
        }

        if (!Security.canAccess(player, connectedChest.getBlock())) {
            event.setOutcome(NO_PERMISSION_FOR_CHEST);
        }
    }
}
