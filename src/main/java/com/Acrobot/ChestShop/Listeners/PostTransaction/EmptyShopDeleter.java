package com.Acrobot.ChestShop.Listeners.PostTransaction;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Signs.ChestShopMetaData;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class EmptyShopDeleter implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onTransaction(TransactionEvent event) {
        if (event.getTransactionType() != TransactionEvent.TransactionType.BUY) {
            return;
        }

        Inventory ownerInventory = event.getOwnerInventory();
        Sign sign = event.getSign();
        Container connectedChest = uBlock.findConnectedChest(sign);

        if (!shopShouldBeRemoved(sign, ownerInventory, event.getStock())) {
            return;
        }

        ChestShopMetaData chestShopMetaData = event.getChestShopMetaData();
        ShopDestroyedEvent destroyedEvent = new ShopDestroyedEvent(null, event.getSign(), connectedChest, chestShopMetaData);
        ChestShop.callEvent(destroyedEvent);

        Material signMaterial = sign.getBlock().getType();
        if (signMaterial == Material.ACACIA_WALL_SIGN) {
            signMaterial = Material.ACACIA_SIGN;
        } else if (signMaterial == Material.BIRCH_WALL_SIGN) {
            signMaterial = Material.BIRCH_SIGN;
        } else if (signMaterial == Material.DARK_OAK_WALL_SIGN) {
            signMaterial = Material.DARK_OAK_SIGN;
        } else if (signMaterial == Material.JUNGLE_WALL_SIGN) {
            signMaterial = Material.JUNGLE_SIGN;
        } else if (signMaterial == Material.OAK_WALL_SIGN) {
            signMaterial = Material.OAK_SIGN;
        } else if (signMaterial == Material.SPRUCE_WALL_SIGN) {
            signMaterial = Material.SPRUCE_SIGN;
        } else if (signMaterial == Material.MANGROVE_WALL_SIGN) {
            signMaterial = Material.MANGROVE_SIGN;
        } else if (signMaterial == Material.CRIMSON_WALL_SIGN) {
            signMaterial = Material.CRIMSON_SIGN;
        } else if (signMaterial == Material.WARPED_WALL_SIGN) {
            signMaterial = Material.WARPED_SIGN;
        }
        sign.getBlock().setType(Material.AIR);

        if (Properties.REMOVE_EMPTY_CHESTS && !chestShopMetaData.isAdminshop() && InventoryUtil.isEmpty(ownerInventory)) {
            connectedChest.getBlock().setType(Material.AIR);
        } else {
            ownerInventory.addItem(new ItemStack(signMaterial, 1));
        }
    }

    private static boolean shopShouldBeRemoved(Sign sign, Inventory inventory, ItemStack stock) {
        return Properties.REMOVE_EMPTY_SHOPS && !ChestShopSign.isAdminShop(sign) && !InventoryUtil.hasItems(stock, 1, inventory);
    }
}
