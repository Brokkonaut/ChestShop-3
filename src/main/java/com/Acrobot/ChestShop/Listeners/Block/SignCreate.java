package com.Acrobot.ChestShop.Listeners.Block;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.ItemNaming.ItemNamingUtils;
import com.Acrobot.ChestShop.Signs.ChestShopMetaData;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Acrobot
 */
public class SignCreate implements Listener {
    public static final String AUTOFILL_CODE = "?";
    public static final String AUTOFILL_SHULKER_CONTENT_CODE = "??";

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public static void onSignChange(SignChangeEvent event) {
        Block signBlock = event.getBlock();
        boolean wasChestShop = ChestShopSign.isChestShop(signBlock);
        if (event.getSide() == Side.BACK) {
            if (wasChestShop) {
                event.setCancelled(true);
            }
            return;
        }
        String[] line = StringUtil.stripColourCodes(event.getLines());

        if (!BlockUtil.isSign(signBlock)) {
            if (wasChestShop) {
                ChestShopSign.removeChestShopMetaData(signBlock, true);
            }
            return;
        }

        if (!ChestShopSign.isValidPreparedSign(line)) {
            if (wasChestShop) {
                ChestShopSign.removeChestShopMetaData(signBlock, true);
            }
            return;
        }

        ItemStack itemStack = getItemStack(event.getLine(ChestShopSign.ITEM_LINE), (Sign) event.getBlock().getState());
        if (itemStack == null || itemStack.isEmpty()) {
            if (wasChestShop) {
                ChestShopSign.removeChestShopMetaData(signBlock, true);
            }
            return;
        }
        itemStack.setAmount(1);

        Sign sign = (Sign) signBlock.getState();
        ChestShopMetaData oldData = wasChestShop ? ChestShopSign.getChestShopMetaData(sign) : null;

        PreShopCreationEvent preEvent = new PreShopCreationEvent(event.getPlayer(), (Sign) signBlock.getState(), line, itemStack);
        if (oldData != null) {
            preEvent.setAmount(oldData.getQuantity());
            preEvent.setEnforceAmount(oldData.isEnforceQuantity());
            preEvent.setNoAutofill(oldData.isNoAutofill());
        }
        ChestShop.callEvent(preEvent);

        if (preEvent.isCancelled()) {
            if (wasChestShop) {
                ChestShopSign.removeChestShopMetaData(signBlock, true);
            }
            return;
        }

        SignSide side = sign.getSide(event.getSide());
        for (byte i = 0; i < event.getLines().length; ++i) {
            event.setLine(i, preEvent.getSignLine(i));
            side.setLine(i, preEvent.getSignLine(i));
        }

        ChestShopMetaData chestShopMetaData = createShopMetaData(event.getPlayer(), preEvent.getShopOwnerId(), event.getLines(), itemStack, preEvent.getAmount(), preEvent.isEnforceAmount(), preEvent.isNoAutofill());

        ShopCreatedEvent postEvent = new ShopCreatedEvent(preEvent.getPlayer(), preEvent.getSign(), uBlock.findConnectedChest(preEvent.getSign()), preEvent.getSignLines(), chestShopMetaData);
        ChestShop.callEvent(postEvent);

        // clear back side
        SignSide signSide = sign.getSide(Side.BACK);
        for (byte i = 0; i < 4; ++i) {
            signSide.setLine(i, "");
        }

        ChestShopSign.saveChestShopMetaData(sign, chestShopMetaData, true);
    }

    public static ChestShopMetaData createShopMetaData(Player creator, UUID shopOwnerId, String[] signLines, ItemStack itemStack, int quantity, boolean enforceQuantity, boolean noAutofill) {
        String priceLine = signLines[ChestShopSign.PRICE_LINE];
        double sellPrice = PriceUtil.getSellPrice(priceLine);
        double buyPrice = PriceUtil.getBuyPrice(priceLine);

        return new ChestShopMetaData(shopOwnerId, quantity, enforceQuantity, sellPrice, buyPrice, itemStack, noAutofill);
    }

    private static ItemStack getItemStack(String itemLine, Sign sign) {
        if (ChestShopSign.isChestShop(sign)) { // If it already was an Chest Shop.
            ChestShopMetaData chestShopMetaData = ChestShopSign.getChestShopMetaData(sign);
            if (chestShopMetaData != null) {
                ItemStack itemStack = chestShopMetaData.getItemStack();
                String oldItemDisplayName = ChatColor.stripColor(ItemNamingUtils.getSignItemName(itemStack));
                if (oldItemDisplayName.equals(ChatColor.stripColor(itemLine))) { // If thats true Sign got edited, but item stayed the same
                    return itemStack;
                }
            }
        }

        ItemStack item = null;
        if (Properties.ALLOW_AUTO_ITEM_FILL && (itemLine.equals(AUTOFILL_CODE) || itemLine.equals(AUTOFILL_SHULKER_CONTENT_CODE))) {
            item = autoFillItemStack(sign, itemLine);
        } else {
            Material material = ItemNamingUtils.getItemFromSignName(itemLine);
            if (material != null) {
                item = new ItemStack(material);
            }
        }

        return item;
    }

    private static ItemStack autoFillItemStack(Sign sign, String itemLine) {
        Container connectedChest = uBlock.findConnectedChest(sign, true);
        if (connectedChest != null) {
            return itemLine.equals(AUTOFILL_SHULKER_CONTENT_CODE) ? autoFillItemStackFromShulker(connectedChest) : autoFillItemStackFromChest(connectedChest);
        }

        return null;
    }

    private static ItemStack autoFillItemStackFromChest(Container connectedChest) {
        for (ItemStack stack : connectedChest.getInventory().getContents()) {
            if (!MaterialUtil.isEmpty(stack)) {
                stack = stack.clone();
                stack.setAmount(1);
                return stack;
            }
        }
        return null;
    }

    private static ItemStack autoFillItemStackFromShulker(Container connectedChest) {
        for (ItemStack stack : connectedChest.getInventory().getContents()) {
            if (!MaterialUtil.isEmpty(stack) && BlockUtil.isShulkerBox(stack.getType())) {
                ItemMeta meta = stack.getItemMeta();
                if (meta instanceof BlockStateMeta bsm) {
                    BlockState blockState = bsm.getBlockState();
                    if (blockState instanceof ShulkerBox shulkerBox) {
                        for (ItemStack shulkerContent : shulkerBox.getSnapshotInventory().getStorageContents()) {
                            if (!MaterialUtil.isEmpty(shulkerContent)) {
                                shulkerContent = shulkerContent.clone();
                                shulkerContent.setAmount(1);
                                return shulkerContent;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }
}
