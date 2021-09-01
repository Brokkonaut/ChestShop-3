package com.Acrobot.ChestShop.Signs;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Containers.AdminInventory;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import java.util.regex.Pattern;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;

/**
 * @author Acrobot
 */
public class ChestShopSign {
    public static final byte NAME_LINE = 0;
    public static final byte QUANTITY_LINE = 1;
    public static final byte PRICE_LINE = 2;
    public static final byte ITEM_LINE = 3;

    public static final Pattern[] SHOP_SIGN_PATTERN = { Pattern.compile("^?[\\w -.]*$"), Pattern.compile("^[1-9][0-9]{0,4}$"), Pattern.compile("(?i)^[\\d.bs(free) :]+$"), Pattern.compile("^[\\w? #:-]+$") };

    public static boolean isAdminShop(Inventory ownerInventory) {
        return ownerInventory instanceof AdminInventory;
    }

    public static boolean isAdminShop(String owner) {
        return owner.replace(" ", "").equalsIgnoreCase(Properties.ADMIN_SHOP_NAME.replace(" ", ""));
    }

    public static boolean isAdminShop(Sign sign) {
        return isAdminShop(sign.getLine(NAME_LINE));
    }

    public static boolean isValid(Sign sign) {
        return isValid(sign.getLines());
    }

    public static boolean isValid(String[] line) {
        return isValidPreparedSign(line) && (line[PRICE_LINE].toUpperCase().contains("B") || line[PRICE_LINE].toUpperCase().contains("S")) && !line[NAME_LINE].isEmpty();
    }

    public static boolean isValid(Block sign) {
        return BlockUtil.isSign(sign) && isValid((Sign) sign.getState());
    }

    public static boolean canAccess(OfflinePlayer player, Sign sign) {
        if (player == null) {
            return false;
        }
        if (sign == null) {
            return true;
        }

        return NameManager.canUseName(player, sign.getLine(NAME_LINE));
    }

    public static boolean isValidPreparedSign(String[] lines) {
        for (int i = 0; i < 4; i++) {
            if (!SHOP_SIGN_PATTERN[i].matcher(lines[i]).matches()) {
                return false;
            }
        }
        return lines[PRICE_LINE].indexOf(':') == lines[PRICE_LINE].lastIndexOf(':');
    }
}
