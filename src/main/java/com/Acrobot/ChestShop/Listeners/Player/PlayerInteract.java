package com.Acrobot.ChestShop.Listeners.Player;

import static com.Acrobot.Breeze.Utils.BlockUtil.isChest;
import static com.Acrobot.Breeze.Utils.BlockUtil.isSign;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.SELL;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.PRICE_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.QUANTITY_LINE;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.NumberUtil;
import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Commands.ItemInfo;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Containers.AdminInventory;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType;
import com.Acrobot.ChestShop.Plugins.ChestShop;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.uBlock;
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

        if (!isSign(block) || BlockUtil.isSign(player.getInventory().getItemInMainHand().getType())) { // Blocking accidental sign edition
            return;
        }

        Sign sign = (Sign) block.getState();

        if (!ChestShopSign.isValid(sign)) {
            return;
        }

        if (event.getPlayer().isSneaking()) {
            if ((action == LEFT_CLICK_BLOCK || action == RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
                showShopInfo(player, sign);
            }
            return;
        }

        if (ChestShopSign.canAccess(player, sign)) {
            if (!Properties.ALLOW_SIGN_CHEST_OPEN || player.isSneaking() || player.isInsideVehicle() || player.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            if (!Properties.ALLOW_LEFT_CLICK_DESTROYING || action != LEFT_CLICK_BLOCK) {
                event.setCancelled(true);
                showChestGUI(player, sign);
            }

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
        String material = sign.getLine(ITEM_LINE);
        ItemStack item = MaterialUtil.getItem(material);

        if (MaterialUtil.isEmpty(item)) {
            player.sendMessage(Messages.prefix(Messages.INVALID_SHOP_DETECTED));
            return;
        }

        if (Properties.SHOW_SHOP_INFORMATION_ON_SHIFT_CLICK) {
            if (!ChestShopSign.isAdminShop(sign)) {
                Container chest = uBlock.findConnectedChest(sign, true);
                if (chest != null) {
                    // do not allow shulker boxes inside of shulker boxes
                    if (chest instanceof ShulkerBox && BlockUtil.isShulkerBox(item.getType())) {
                        player.sendMessage(Messages.prefix(Messages.INVALID_SHOP_DETECTED));
                        return;
                    }

                    player.sendMessage(Messages.prefix(Messages.SHOP_INFO));
                    String prices = sign.getLine(PRICE_LINE);
                    Inventory inventory = chest.getInventory();
                    if (PriceUtil.getSellPrice(prices) != PriceUtil.NO_PRICE) {
                        int free = InventoryUtil.getFreeSpace(item, inventory);
                        player.sendMessage("  " + Messages.AVAILABLE_SPACE.replace("%amount", Integer.toString(free)));
                    }
                    if (PriceUtil.getBuyPrice(prices) != PriceUtil.NO_PRICE) {
                        int available = InventoryUtil.getAmount(item, inventory);
                        player.sendMessage("  " + Messages.AVAILABLE_ITEMS.replace("%amount", Integer.toString(available)));
                    }
                }
            }
        }
        ItemInfo.showItemInfo(player, item);
    }

    private static PreTransactionEvent preparePreTransactionEvent(Sign sign, Player player, Action action) {
        String name = sign.getLine(NAME_LINE);
        String quantity = sign.getLine(QUANTITY_LINE);
        String prices = sign.getLine(PRICE_LINE);
        String material = sign.getLine(ITEM_LINE);

        UUID uuid = NameManager.getUUIDFor(name);

        if (uuid == null) {
            return null;
        }

        OfflinePlayer owner = Bukkit.getOfflinePlayer(uuid);

        Action buy = Properties.REVERSE_BUTTONS ? LEFT_CLICK_BLOCK : RIGHT_CLICK_BLOCK;
        double price = (action == buy ? PriceUtil.getBuyPrice(prices) : PriceUtil.getSellPrice(prices));

        Container chest = uBlock.findConnectedChest(sign, true);
        Inventory ownerInventory = (ChestShopSign.isAdminShop(sign) ? new AdminInventory() : chest != null ? chest.getInventory() : null);

        ItemStack item = MaterialUtil.getItem(material);

        if (item == null || !NumberUtil.isInteger(quantity)) {
            player.sendMessage(Messages.prefix(Messages.INVALID_SHOP_DETECTED));
            return null;
        }
        // do not allow shulker boxes inside of shulker boxes
        if (!ChestShopSign.isAdminShop(sign) && chest instanceof ShulkerBox && BlockUtil.isShulkerBox(item.getType())) {
            player.sendMessage(Messages.prefix(Messages.INVALID_SHOP_DETECTED));
            return null;
        }

        int amount = Integer.parseInt(quantity);

        if (amount < 1) {
            amount = 1;
        }

        if (Properties.SHIFT_SELLS_IN_STACKS && player.isSneaking() && price != PriceUtil.NO_PRICE && isAllowedForShift(action == buy)) {
            int newAmount = getStackAmount(item, ownerInventory, player, action);
            if (newAmount > 0) {
                price = (price / amount) * newAmount;
                amount = newAmount;
            }
        }

        item.setAmount(amount);

        ItemStack[] items = { item };

        TransactionType transactionType = (action == buy ? BUY : SELL);
        return new PreTransactionEvent(ownerInventory, player.getInventory(), items, price, player, owner, sign, transactionType);
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
