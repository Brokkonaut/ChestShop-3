package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.UUIDs.NameManager;

/**
 * @author Acrobot
 */
public class NameChecker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {

        String name = event.getOwnerName();
        Player player = event.getPlayer();
        if (Properties.ADMIN_SHOP_NAME.equalsIgnoreCase(name)) {
            name = Properties.ADMIN_SHOP_NAME;
        }
        if (name.isEmpty() || (!NameManager.canUseName(player, name) && !Permission.has(player, Permission.ADMIN))) {
            name = NameManager.getNameFor(player);
        }
        event.setOwnerName(name);
    }
}
