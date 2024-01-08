package com.Acrobot.ChestShop.Listeners.PostShopCreation;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class SignSticker implements Listener {

    @EventHandler
    public static void onShopCreation(ShopCreatedEvent event) {
        if (!Properties.STICK_SIGNS_TO_CHESTS) {
            return;
        }

        if (ChestShopSign.isAdminShop(event.getSign())) {
            return;
        }

        stickSign(event.getSign().getBlock(), event.getSignLines());
    }

    private static void stickSign(Block signBlock, String[] lines) {
        Material signMaterial = signBlock.getType();
        Material newSignMaterial = null;
        if (signMaterial == Material.ACACIA_SIGN) {
            newSignMaterial = Material.ACACIA_WALL_SIGN;
        } else if (signMaterial == Material.BIRCH_SIGN) {
            newSignMaterial = Material.BIRCH_WALL_SIGN;
        } else if (signMaterial == Material.DARK_OAK_SIGN) {
            newSignMaterial = Material.DARK_OAK_WALL_SIGN;
        } else if (signMaterial == Material.JUNGLE_SIGN) {
            newSignMaterial = Material.JUNGLE_WALL_SIGN;
        } else if (signMaterial == Material.OAK_SIGN) {
            newSignMaterial = Material.OAK_WALL_SIGN;
        } else if (signMaterial == Material.SPRUCE_SIGN) {
            newSignMaterial = Material.SPRUCE_WALL_SIGN;
        } else if (signMaterial == Material.MANGROVE_SIGN) {
            newSignMaterial = Material.MANGROVE_WALL_SIGN;
        } else if (signMaterial == Material.CRIMSON_SIGN) {
            newSignMaterial = Material.CRIMSON_WALL_SIGN;
        } else if (signMaterial == Material.WARPED_SIGN) {
            newSignMaterial = Material.WARPED_WALL_SIGN;
        }

        if (newSignMaterial == null) {
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

        WallSign wallSign = (WallSign) Bukkit.createBlockData(newSignMaterial);
        wallSign.setFacing(chestFace.getOppositeFace());
        signBlock.setBlockData(wallSign);

        Sign sign = (Sign) signBlock.getState();

        for (int i = 0; i < lines.length; ++i) {
            sign.setLine(i, lines[i]);
        }

        sign.update(true);
    }
}
