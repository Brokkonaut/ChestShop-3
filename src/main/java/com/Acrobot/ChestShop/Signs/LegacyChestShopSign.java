package com.Acrobot.ChestShop.Signs;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Containers.AdminInventory;
import com.Acrobot.ChestShop.UUIDs.NameManager;

class LegacyChestShopSign {
    public static final byte NAME_LINE = 0;
    public static final byte QUANTITY_LINE = 1;
    public static final byte ITEM_LINE = 3;

    public static boolean isAdminShop(Inventory ownerInventory) {
        return ownerInventory instanceof AdminInventory;
    }

    public static boolean isAdminShop(String owner) {
        return owner.replace(" ", "").equalsIgnoreCase(Properties.ADMIN_SHOP_NAME.replace(" ", ""));
    }

    public static ItemStack getItemStack(Sign sign) {
        return MaterialUtil.getItem(sign.getLine(ITEM_LINE));
    }

    public static double getBuyPrice(Sign sign) {
        return PriceUtil.getBuyPrice(sign.getLine(ChestShopSign.PRICE_LINE));
    }

    public static double getSellPrice(Sign sign) {
        return PriceUtil.getSellPrice(sign.getLine(ChestShopSign.PRICE_LINE));
    }

    public static int getQuantity(Sign sign) {
        String line = sign.getLine(QUANTITY_LINE);
        return Integer.parseInt(line.replaceAll("[^0-9]", ""));
    }

    public static boolean isAdminShop(Sign sign) {
        return isAdminShop(sign.getLine(NAME_LINE));
    }

    public static boolean isValid(Sign sign) {
        return isValid(sign.getLines());
    }

    public static boolean isValid(String[] line) {
        return ChestShopSign.isValidPreparedSign(line) && (line[ChestShopSign.PRICE_LINE].toUpperCase().contains("B")
                || line[ChestShopSign.PRICE_LINE].toUpperCase().contains("S"))
                && !line[NAME_LINE].isEmpty();
    }

    public static boolean isValid(Block sign) {
        return BlockUtil.isSign(sign) && isValid((Sign) sign.getState());
    }

    public static UUID getOwnerUUID(Sign sign) {

        if (isAdminShop(sign))
            return NameManager.getAdminShopUUID();

        return NameManager.getUUIDFor(sign.getLine(NAME_LINE));
    }
}
