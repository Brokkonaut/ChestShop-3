package com.Acrobot.ChestShop.Listeners.PostShopCreation;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;

/**
 * @author Acrobot
 */
public class SignSticker implements Listener {

    @EventHandler
    public static void onShopCreation(ShopCreatedEvent event) {
        if (!Properties.STICK_SIGNS_TO_CHESTS) {
            return;
        }

        if (ChestShopSign.isAdminShop(event.getSign().getLine(NAME_LINE))) {
            return;
        }

        stickSign(event.getSign().getBlock(), event.getSignLines());
    }

    private static void stickSign(Block signBlock, String[] lines) {
        if (signBlock.getType() != Material.SIGN) {
            return;
        }

        BlockFace chestFace = null;

        for (BlockFace face : uBlock.CHEST_EXTENSION_FACES) {
            if (BlockUtil.isChest(signBlock.getRelative(face))) {
                chestFace = face;
                break;
            }
        }

        if (chestFace == null) {
            return;
        }

        WallSign wallSign = (WallSign) Bukkit.createBlockData(Material.WALL_SIGN);
        wallSign.setFacing(chestFace.getOppositeFace());

        signBlock.setType(Material.WALL_SIGN);
        signBlock.setBlockData(wallSign);

        Sign sign = (Sign) signBlock.getState();

        for (int i = 0; i < lines.length; ++i) {
            sign.setLine(i, lines[i]);
        }

        sign.update(true);
    }
}
