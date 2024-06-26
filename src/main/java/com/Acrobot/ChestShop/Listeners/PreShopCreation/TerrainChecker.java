package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.NO_PERMISSION_FOR_TERRAIN;
import static com.Acrobot.ChestShop.Permission.ADMIN;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.Protection.BuildPermissionEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class TerrainChecker implements Listener {

    @EventHandler
    public static void onPreShopCreation(PreShopCreationEvent event) {

        if (ChestShopSign.isAdminshopLine(event.getOwnerName())) {
            return;
        }

        Player player = event.getPlayer();

        if (Permission.has(player, ADMIN)) {
            return;
        }

        if (!Security.canPlaceSign(player, event.getSign())) {
            event.setOutcome(NO_PERMISSION_FOR_TERRAIN);
            return;
        }

        Container connectedChest = uBlock.findConnectedChest(event.getSign());
        Location chestLocation = (connectedChest != null ? connectedChest.getLocation() : null);

        BuildPermissionEvent bEvent = new BuildPermissionEvent(player, chestLocation, event.getSign().getLocation());
        ChestShop.callEvent(bEvent);

        if (!bEvent.isAllowed()) {
            event.setOutcome(NO_PERMISSION_FOR_TERRAIN);
        }

    }
}
