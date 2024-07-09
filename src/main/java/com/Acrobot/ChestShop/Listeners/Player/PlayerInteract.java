package com.Acrobot.ChestShop.Listeners.Player;

import static com.Acrobot.Breeze.Utils.BlockUtil.isChest;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.SELL;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Commands.ItemInfo;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Containers.AdminInventory;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Plugins.ChestShop;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.ChestShopMetaData;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.uBlock;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class PlayerInteract implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        Action action = event.getAction();
        Player player = event.getPlayer();

        if (Properties.USE_BUILT_IN_PROTECTION && isChest(block)) {
            if (Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
                return;
            }

            if (!canOpenOtherShops(player) && !ChestShop.canAccess(player, block)) {
                player.sendMessage(Messages.prefix(Messages.ACCESS_DENIED));
                event.setCancelled(true);
            }

            return;
        }

        if (!ChestShopSign.isChestShop(block)) {
            return;
        }

        Sign sign = (Sign) block.getState();
        ChestShopMetaData chestShopMetaData = ChestShopSign.getChestShopMetaData(sign);
        if (chestShopMetaData == null) {
            player.sendMessage(Messages.prefix(Messages.INVALID_SHOP_DETECTED));
            return;
        }

        if (BlockUtil.isSign(player.getInventory().getItemInMainHand().getType()) && (chestShopMetaData.isOwner(event.getPlayer()) || Permission.has(event.getPlayer(), Permission.ADMIN))) { // Blocking accidental sign edition
            return;
        }

        if (event.getPlayer().isSneaking() && Properties.ALLOW_SHOP_INFO_ON_SNEAK_CLICK) {
            if ((action == LEFT_CLICK_BLOCK || action == RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
                showShopInfo(player, sign);
            }
            if (action == RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
            }
            return;
        }

        boolean ownShop = chestShopMetaData.canAccess(player);
        if (ownShop && action == RIGHT_CLICK_BLOCK && event.getItem() != null && BlockUtil.isNotCurrentlyActiveSignEditMaterial(sign, sign.getTargetSide(player), event.getItem().getType())) {
            return;
        }
        if (ownShop && (!Properties.ALLOW_OWN_SHOP_TRANSACTIONS || player.isSneaking())) {
            if (!Properties.ALLOW_SIGN_CHEST_OPEN || player.isSneaking() || player.isInsideVehicle() || player.getGameMode() == GameMode.CREATIVE) {
                event.setCancelled(true);
                return;
            }

            if (!Properties.ALLOW_LEFT_CLICK_DESTROYING || action != LEFT_CLICK_BLOCK) {
                showChestGUI(player, sign);
            }

            event.setCancelled(true);
            return;
        }

        if (action == RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
        }

        PreTransactionEvent pEvent = preparePreTransactionEvent(sign, player, action);

        if (pEvent == null) {
            return;
        }

        Bukkit.getPluginManager().callEvent(pEvent);

        if (pEvent.isCancelled()) {
            return;
        }

        TransactionEvent tEvent = new TransactionEvent(pEvent, sign);
        Bukkit.getPluginManager().callEvent(tEvent);
    }

    private static void showShopInfo(Player player, Sign sign) {

        if (!ChestShopSign.isChestShop(sign)) {
            return;
        }

        ChestShopMetaData chestShopMetaData = ChestShopSign.getChestShopMetaData(sign);
        if (chestShopMetaData == null) {
            player.sendMessage(Messages.prefix(Messages.INVALID_SHOP_DETECTED));
            return;
        }

        ItemStack item = chestShopMetaData.getItemStack();

        if (Properties.SHOW_SHOP_INFORMATION_ON_SHIFT_CLICK) {
            if (!chestShopMetaData.isAdminshop()) {

                if (chestShopMetaData.isOwner(player) || Permission.has(player, Permission.MOD)) {
                    Collection<UUID> accessors = chestShopMetaData.getAccessors();
                    if (!accessors.isEmpty()) {
                        player.sendMessage(Messages.prefix(Messages.SHOP_OWNER_INFO));
                        StringBuilder accessorNames = new StringBuilder();
                        for (UUID accessorUUID : accessors) {
                            if (!accessorNames.isEmpty()) {
                                accessorNames.append(", ");
                            }
                            accessorNames.append(NameManager.getFullNameFor(accessorUUID));
                        }

                        player.sendMessage("  " + Messages.SHOP_ACCESSORS.replace("%accessor_list", accessorNames.toString()));
                    }
                }

                Container chest = uBlock.findConnectedChest(sign, true);
                if (chest != null) {
                    // do not allow shulker boxes inside of shulker boxes
                    if (chest instanceof ShulkerBox && BlockUtil.isShulkerBox(item.getType())) {
                        player.sendMessage(Messages.prefix(Messages.INVALID_SHOP_DETECTED));
                        return;
                    }

                    player.sendMessage(Messages.prefix(Messages.SHOP_INFO));
                    Inventory inventory = chest.getInventory();
                    if (chestShopMetaData.doesSell()) {
                        int free = InventoryUtil.getFreeSpace(item, inventory);
                        player.sendMessage("  " + Messages.AVAILABLE_SPACE.replace("%amount", Integer.toString(free)));
                    }
                    if (chestShopMetaData.doesBuy()) {
                        int available = InventoryUtil.getAmount(item, inventory);
                        player.sendMessage("  " + Messages.AVAILABLE_ITEMS.replace("%amount", Integer.toString(available)));
                    }
                }
            }
        }
        ItemInfo.showItemInfo(player, item);
    }

    private static PreTransactionEvent preparePreTransactionEvent(Sign sign, Player player, Action action) {

        if (!ChestShopSign.isChestShop(sign)) {
            return null;
        }

        ChestShopMetaData chestShopMetaData = ChestShopSign.getChestShopMetaData(sign);
        if (chestShopMetaData == null) {
            return null;
        }

        UUID uuid = chestShopMetaData.getOwner();

        OfflinePlayer owner = Bukkit.getOfflinePlayer(uuid);

        Action buy = Properties.REVERSE_BUTTONS ? LEFT_CLICK_BLOCK : RIGHT_CLICK_BLOCK;
        double price = (action == buy ? chestShopMetaData.getBuyPrice() : chestShopMetaData.getSellPrice());
        if (player.getUniqueId().equals(uuid)) { // own shop
            price = PriceUtil.FREE;
        }

        Container chest = uBlock.findConnectedChest(sign, true);
        Inventory ownerInventory = (chestShopMetaData.isAdminshop() ? new AdminInventory() : chest != null ? chest.getInventory() : null);

        ItemStack item = chestShopMetaData.getItemStack();

        // do not allow shulker boxes inside of shulker boxes
        if (!chestShopMetaData.isAdminshop() && chest instanceof ShulkerBox && BlockUtil.isShulkerBox(item.getType())) {
            player.sendMessage(Messages.prefix(Messages.INVALID_SHOP_DETECTED));
            return null;
        }

        int amount = chestShopMetaData.getQuantity();

        if (Properties.SHIFT_SELLS_IN_STACKS && player.isSneaking() && price != PriceUtil.NO_PRICE && isAllowedForShift(action == buy)) {
            int newAmount = getStackAmount(item, ownerInventory, player, action);
            if (newAmount > 0) {
                price = (price / amount) * newAmount;
                amount = newAmount;
            }
        }

        item.setAmount(amount);

        TransactionType transactionType = (action == buy ? BUY : SELL);
        return new PreTransactionEvent(ownerInventory, player.getInventory(), item, price, player, owner, sign, transactionType, chestShopMetaData);
    }

    private static boolean isAllowedForShift(boolean buyTransaction) {
        String allowed = Properties.SHIFT_ALLOWS;

        if (allowed.equalsIgnoreCase("ALL")) {
            return true;
        }

        return allowed.equalsIgnoreCase(buyTransaction ? "BUY" : "SELL");
    }

    private static int getStackAmount(ItemStack item, Inventory inventory, Player player, Action action) {
        Action buy = Properties.REVERSE_BUTTONS ? LEFT_CLICK_BLOCK : RIGHT_CLICK_BLOCK;
        Inventory checkedInventory = (action == buy ? inventory : player.getInventory());

        if (checkedInventory.containsAtLeast(item, item.getMaxStackSize())) {
            return item.getMaxStackSize();
        } else {
            return InventoryUtil.getAmount(item, checkedInventory);
        }
    }

    public static boolean canOpenOtherShops(Player player) {
        return Permission.has(player, Permission.ADMIN) || Permission.has(player, Permission.MOD);
    }

    private static void showChestGUI(Player player, Sign sign) {
        Container chest = uBlock.findConnectedChest(sign);

        if (chest == null) {
            player.sendMessage(Messages.prefix(Messages.NO_CHEST_DETECTED));
            return;
        }

        if (!canOpenOtherShops(player) && !Security.canAccess(player, sign.getBlock())) {
            return;
        }

        BlockUtil.openBlockGUI(chest, player);
    }
}
