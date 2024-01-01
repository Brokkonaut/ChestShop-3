package com.Acrobot.ChestShop.Listeners.Block;

import java.util.UUID;

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
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Listeners.PreShopCreation.ItemChecker;
import com.Acrobot.ChestShop.Signs.ChestShopMetaData;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.uBlock;

/**
 * @author Acrobot
 */
public class SignCreate implements Listener {
    public static final String AUTOFILL_CODE = "?";
    public static final String AUTOFILL_SHULKER_CONTENT_CODE = "??";

    @EventHandler
    public static void onSignChange(SignChangeEvent event) {
        Block signBlock = event.getBlock();
        if (event.getSide() == Side.BACK) {
            if (ChestShopSign.isChestShop(signBlock)) {
                event.setCancelled(true);
            }
            return;
        }
        String[] line = StringUtil.stripColourCodes(event.getLines());

        if (!BlockUtil.isSign(signBlock)) {
            return;
        }

        if (!ChestShopSign.isValidPreparedSign(line)) {
            return;
        }

        ItemStack itemStack = getItemStack(event.getLine(3), (Sign) event.getBlock().getState());
        PreShopCreationEvent preEvent = new PreShopCreationEvent(event.getPlayer(), (Sign) signBlock.getState(), line, itemStack);
        ChestShop.callEvent(preEvent);

        if (preEvent.isCancelled()) {
            return;
        }

        for (byte i = 0; i < event.getLines().length; ++i) {
            event.setLine(i, preEvent.getSignLine(i));
        }

        ChestShopMetaData chestShopMetaData = createShopMetaData(event.getPlayer(), event.getLines(), itemStack);

        ShopCreatedEvent postEvent = new ShopCreatedEvent(preEvent.getPlayer(), preEvent.getSign(),
                uBlock.findConnectedChest(preEvent.getSign()), preEvent.getSignLines(), chestShopMetaData);
        ChestShop.callEvent(postEvent);

        // clear back side
        Sign sign = (Sign) signBlock.getState();
        SignSide signSide = sign.getSide(Side.BACK);
        for (byte i = 0; i < 4; ++i) {
            signSide.setLine(i, "");
        }

        ChestShopSign.saveChestShopMetaData(sign, chestShopMetaData);
    }

    public static ChestShopMetaData createShopMetaData(Player creator, String[] signLines, ItemStack itemStack) {

        int quantity = Integer.parseInt(signLines[1].replaceAll("[^0-9]", ""));

        String priceLine = signLines[2];
        double sellPrice = PriceUtil.getSellPrice(priceLine);
        double buyPrice = PriceUtil.getBuyPrice(priceLine);

        String ownerLine = signLines[0];
        boolean isAdminShop = ChestShopSign.isAdminshopLine(ownerLine) && Permission.has(creator, Permission.ADMIN);

        if (isAdminShop) {
            return createAdminChestShop(quantity, sellPrice, buyPrice, itemStack);
        } else {
            return createChestShop(creator.getUniqueId(), quantity, sellPrice, buyPrice, itemStack);
        }

    }

    private static ChestShopMetaData createChestShop(UUID owner, int quantity, double sellPrice, double buyPrice,
            ItemStack itemStack) {

        return new ChestShopMetaData(owner, quantity, sellPrice, buyPrice, itemStack);
    }

    private static ChestShopMetaData createAdminChestShop(int quantity, double sellPrice, double buyPrice, ItemStack itemStack) {

        return new ChestShopMetaData(NameManager.getAdminShopUUID(), quantity, sellPrice, buyPrice, itemStack);
    }

    private static ItemStack getItemStack(String itemLine, Sign sign) {
        Material material = MaterialUtil.getMaterial(itemLine);
        if (material != null)
            return new ItemStack(material);

        ItemStack item = null;
        if (Properties.ALLOW_AUTO_ITEM_FILL && (itemLine.equals(AUTOFILL_CODE) || itemLine.equals(AUTOFILL_SHULKER_CONTENT_CODE))) {
            item = autoFillItemStack(sign, itemLine);
        } else if (ChestShopSign.isChestShop(sign)) { // If it already was an Chest Shop.

            ChestShopMetaData chestShopMetaData = ChestShopSign.getChestShopMetaData(sign);
            ItemStack itemStack = chestShopMetaData.getItemStack();
            String oldItemDisplayName = ItemChecker.getSignItemName(itemStack);
            if (oldItemDisplayName.equals(itemLine)) // If thats true Sign got edited, but item stayed the same
                item = itemStack;
        }

        return item;
    }

    private static ItemStack autoFillItemStack(Sign sign, String itemLine) {
        Container connectedChest = uBlock.findConnectedChest(sign, true);
        if (connectedChest != null) {
            return itemLine.equals(AUTOFILL_SHULKER_CONTENT_CODE) ? autoFillItemStackFromShulker(connectedChest)
                    : autoFillItemStackFromChest(connectedChest);
        }

        return null;
    }

    private static ItemStack autoFillItemStackFromChest(Container connectedChest) {
        for (ItemStack stack : connectedChest.getInventory().getContents()) {
            if (!MaterialUtil.isEmpty(stack)) {
                return stack;
            }
        }
        return null;
    }

    private static ItemStack autoFillItemStackFromShulker(Container connectedChest) {
        for (ItemStack stack : connectedChest.getInventory().getContents()) {
            if (!MaterialUtil.isEmpty(stack) && BlockUtil.isShulkerBox(stack.getType())) {
                ItemMeta meta = stack.getItemMeta();
                if (meta instanceof BlockStateMeta) {
                    BlockStateMeta bsm = (BlockStateMeta) meta;
                    BlockState blockState = bsm.getBlockState();
                    if (blockState instanceof ShulkerBox) {
                        ShulkerBox shulkerBox = (ShulkerBox) blockState;
                        for (ItemStack shulkerContent : shulkerBox.getSnapshotInventory().getStorageContents()) {
                            if (!MaterialUtil.isEmpty(shulkerContent)) {
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
