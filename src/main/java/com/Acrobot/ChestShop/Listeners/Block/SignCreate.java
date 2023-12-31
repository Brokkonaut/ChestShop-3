package com.Acrobot.ChestShop.Listeners.Block;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;

/**
 * @author Acrobot
 */
public class SignCreate implements Listener {

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

        boolean isAdminShop = ChestShopSign.isAdminShopNameString(preEvent.getOwnerName());
        ChestShopSign.setAdminshop(isAdminShop, sign);
        if (!isAdminShop)
            ChestShopSign.setOwner(postEvent.getPlayer().getUniqueId(), sign);
    }
}
