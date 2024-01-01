package com.Acrobot.ChestShop.Signs;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Containers.AdminInventory;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.uBlock;
import java.util.regex.Pattern;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Acrobot
 */
public class ChestShopSign {
    public static final byte NAME_LINE = 0;
    public static final byte QUANTITY_LINE = 1;
    public static final byte PRICE_LINE = 2;
    public static final byte ITEM_LINE = 3;

    public static final Pattern[] SHOP_SIGN_PATTERN = { Pattern.compile("^?[\\w -.]*$"), Pattern.compile("^[1-9][0-9]{0,4}$"), Pattern.compile("(?i)^[\\d.bs(free) :]+$"), Pattern.compile("^[\\w? #:-]+$") };
    public static final String AUTOFILL_CODE = "?";
    public static final String AUTOFILL_SHULKER_CONTENT_CODE = "??";

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

    /**
     * Returns the itemCode as it should display on a sign or null, if the itemCode is invalid.
     *
     * @param itemCode
     *            the user entered itemCode
     * @param sign
     *            the sign to set the item, required for autofill
     * @return the corrected itemCode or null if it was invalid
     */
    public static String getCorrectedItemCode(String itemCode, Sign sign) {
        ItemStack item = MaterialUtil.getItem(itemCode);
        if (item == null) {
            if (Properties.ALLOW_AUTO_ITEM_FILL && (itemCode.equals(AUTOFILL_CODE) || itemCode.equals(AUTOFILL_SHULKER_CONTENT_CODE))) {
                Container connectedChest = uBlock.findConnectedChest(sign, true);
                if (connectedChest != null) {
                    if (itemCode.equals(AUTOFILL_SHULKER_CONTENT_CODE)) {
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
                                                itemCode = MaterialUtil.getSignName(shulkerContent);
                                                if (itemCode == null) {
                                                    item = null;
                                                }
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
                                itemCode = MaterialUtil.getSignName(stack);
                                if (itemCode == null) {
                                    item = null;
                                }
                                break;
                            }
                        }
                    }
                }
            }

            if (item == null) {
                return null;
            }
        }

        String metaCode = MaterialUtil.Metadata.getMetaCodeFromItemCode(itemCode);
        metaCode = metaCode == null ? "" : "#" + metaCode;
        String itemName = MaterialUtil.getSignMaterialName(item.getType(), metaCode);

        ItemStack newItem = MaterialUtil.getItem(itemCode);
        if (newItem == null || !newItem.isSimilar(item)) {
            return null;
        }

        return itemName + metaCode;
    }
}
