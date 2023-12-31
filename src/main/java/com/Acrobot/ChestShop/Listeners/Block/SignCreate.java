package com.Acrobot.ChestShop.Listeners.Block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
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

        PreShopCreationEvent preEvent = new PreShopCreationEvent(event.getPlayer(), (Sign) signBlock.getState(), line);

        ItemStack itemStack = getItemStack(event.getLine(4), (Sign) event.getBlock().getState());
        if (itemStack == null) {
            preEvent.setOutcome(PreShopCreationEvent.CreationOutcome.INVALID_ITEM);
        }

        ChestShop.callEvent(preEvent);

        if (preEvent.isCancelled()) {
            return;
        }

        for (byte i = 0; i < event.getLines().length; ++i) {
            event.setLine(i, preEvent.getSignLine(i));
        }

        ShopCreatedEvent postEvent = new ShopCreatedEvent(preEvent.getPlayer(), preEvent.getSign(), uBlock.findConnectedChest(preEvent.getSign()), preEvent.getSignLines());
        ChestShop.callEvent(postEvent);

        // clear back side
        Sign sign = (Sign) signBlock.getState();
        SignSide signSide = sign.getSide(Side.BACK);
        for (byte i = 0; i < 4; ++i) {
            signSide.setLine(i, "");
        }

        ChestShopSign.createShop(sign, event.getPlayer(), event.getLines(), itemStack);
    }

    private static ItemStack getItemStack(String itemLine, Sign sign) {
        Material material = MaterialUtil.getMaterial(itemLine);
        if (material != null)
            return new ItemStack(material);

        ItemStack item = null;
        if (Properties.ALLOW_AUTO_ITEM_FILL && (itemLine.equals(AUTOFILL_CODE) || itemLine.equals(AUTOFILL_SHULKER_CONTENT_CODE))) {
            Container connectedChest = uBlock.findConnectedChest(sign, true);
            if (connectedChest != null) {
                if (itemLine.equals(AUTOFILL_SHULKER_CONTENT_CODE)) {
                    out: for (ItemStack stack : connectedChest.getInventory().getContents()) {
                        if (!MaterialUtil.isEmpty(stack) && BlockUtil.isShulkerBox(stack.getType())) {
                            ItemMeta meta = stack.getItemMeta();
                            if (meta instanceof BlockStateMeta) {
                                BlockStateMeta bsm = (BlockStateMeta) meta;
                                BlockState blockState = bsm.getBlockState();
                                if (blockState instanceof ShulkerBox) {
                                    ShulkerBox shulkerBox = (ShulkerBox) blockState;
                                    for (ItemStack shulkerContent : shulkerBox.getSnapshotInventory().getStorageContents()) {
                                        if (!MaterialUtil.isEmpty(shulkerContent)) {
                                            item = shulkerContent;
                                            break out;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (ItemStack stack : connectedChest.getInventory().getContents()) {
                        if (!MaterialUtil.isEmpty(stack)) {
                            item = stack;
                            break;
                        }
                    }
                }
            }
        }

        return item;
    }
}
