package com.Acrobot.ChestShop.Signs;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.NamespacedKey;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Containers.AdminInventory;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.uBlock;

/**
 * @author Acrobot
 */
public class ChestShopSign {
    private static final byte NAME_LINE = 0;
    public static final byte QUANTITY_LINE = 1;
    public static final byte PRICE_LINE = 2;
    public static final byte ITEM_LINE = 3;

    public static final Pattern[] SHOP_SIGN_PATTERN = { Pattern.compile("^?[\\w -.]*$"), Pattern.compile("^[1-9][0-9]{0,4}$"), Pattern.compile("(?i)^[\\d.bs(free) :]+$"), Pattern.compile("^[\\w? #:-]+$") };
    public static final String AUTOFILL_CODE = "?";
    public static final String AUTOFILL_SHULKER_CONTENT_CODE = "??";

    private static NamespacedKey OWNER_NAMESPACED_KEY;
    private static NamespacedKey ADMINSHOP_NAMESPACED_KEY;
    private static NamespacedKey ACCESSORS_NAMESPACED_KEY;

    public static void createNamespacedKeys(ChestShop chestShop) {

        OWNER_NAMESPACED_KEY = new NamespacedKey(chestShop, "owner-key");
        ADMINSHOP_NAMESPACED_KEY = new NamespacedKey(chestShop, "adminshop-key");
        ACCESSORS_NAMESPACED_KEY = new NamespacedKey(chestShop, "accessors-key");
    }

    public static boolean isAdminShop(Inventory ownerInventory) {
        return ownerInventory instanceof AdminInventory;
    }

    public static boolean isAdminShopNameString(String string) {
        return string.replace(" ", "").equalsIgnoreCase(Properties.ADMIN_SHOP_NAME.replace(" ", ""));
    }

    public static boolean isAdminShop(Sign sign) {

        PersistentDataContainer persistentDataContainer = sign.getPersistentDataContainer();
        if (!persistentDataContainer.has(ADMINSHOP_NAMESPACED_KEY, PersistentDataType.BOOLEAN)) {

            boolean isAdminshop = isAdminShopNameString(sign.getLine(NAME_LINE));
            persistentDataContainer.set(ADMINSHOP_NAMESPACED_KEY, PersistentDataType.BOOLEAN, Boolean.valueOf(isAdminshop));
            return isAdminshop;
        }

        return persistentDataContainer.get(ADMINSHOP_NAMESPACED_KEY, PersistentDataType.BOOLEAN).booleanValue();
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

        if (isOwner(player, sign))
            return true;

        return isAccessor(player, sign);
    }

    public static boolean isOwner(OfflinePlayer player, Sign sign) {
        PersistentDataContainer persistentDataContainer = sign.getPersistentDataContainer();
        String playerUUIDAsString = player.getUniqueId().toString();

        if (isAdminShop(sign)) {
            return false;
        }

        if (!persistentDataContainer.has(OWNER_NAMESPACED_KEY, PersistentDataType.STRING)) {

            // Update old Signs
            String ownerName = sign.getLine(NAME_LINE);
            UUID uuidByName = NameManager.getUUIDFor(ownerName);
            persistentDataContainer.set(OWNER_NAMESPACED_KEY, PersistentDataType.STRING, uuidByName.toString());

            if (!NameManager.canUseName(player, ownerName)) {
                return false; // Player isn't Owner
            }
        }

        String owner = getOwner(sign);
        return playerUUIDAsString.equals(owner);
    }

    public static String getOwner(Sign sign) {
        return sign.getPersistentDataContainer().get(OWNER_NAMESPACED_KEY, PersistentDataType.STRING);
    }

    public static UUID getOwnerUUID(Sign sign) {

        if (isAdminShop(sign)) {
            return NameManager.getAdminShopUUID();
        }

        String owner = getOwner(sign);
        if (owner == null)
            return null;

        return UUID.fromString(owner);
    }

    public static boolean isAccessor(OfflinePlayer player, Sign sign) {

        Collection<String> accessors = getAccessors(sign);
        return accessors.contains(player.getUniqueId().toString());
    }

    public static Collection<String> getAccessors(Sign sign) {

        PersistentDataContainer persistentDataContainer = sign.getPersistentDataContainer();
        if (!persistentDataContainer.has(ACCESSORS_NAMESPACED_KEY, PersistentDataType.STRING))
            return new HashSet<>();

        String data = persistentDataContainer.get(ACCESSORS_NAMESPACED_KEY, PersistentDataType.STRING);
        String[] split = data.split(";");

        return Arrays.asList(split);
    }

    public static void addAccessor(OfflinePlayer player, Sign sign) {
        Collection<String> accessors = getAccessors(sign);
        accessors.add(player.getUniqueId().toString());
        saveAccessors(accessors, sign);
    }

    public static void removeAccessor(OfflinePlayer player, Sign sign) {
        Collection<String> accessors = getAccessors(sign);
        accessors.remove(player.getUniqueId().toString());
        saveAccessors(accessors, sign);
    }

    private static void saveAccessors(Collection<String> accessors, Sign sign) {
        String joined = String.join(";", accessors);
        sign.getPersistentDataContainer().set(ACCESSORS_NAMESPACED_KEY, PersistentDataType.STRING, joined);
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
