package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Plugins.ChestShop;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * @author Acrobot
 */
public class PlayerInventory implements Listener {
    @EventHandler
    public static void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.CHEST) {
            return;
        }

        if (!Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
            return;
        }

        HumanEntity entity = event.getPlayer();

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        Block chest = uBlock.getInventoryHolderBlock(event.getInventory().getHolder());
        if (!uBlock.isShopChest(chest)) {
            return;
        }

        if (!PlayerInteract.canOpenOtherShops(player) && !ChestShop.canAccess(player, chest)) {
            player.sendMessage(Messages.prefix(Messages.ACCESS_DENIED));
            event.setCancelled(true);
        }
    }
}
