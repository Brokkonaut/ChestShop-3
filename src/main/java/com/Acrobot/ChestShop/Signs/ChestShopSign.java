package com.Acrobot.ChestShop.Signs;

import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Listeners.PreShopCreation.ItemChecker;
import com.Acrobot.ChestShop.UUIDs.NameManager;

/**
 * @author Acrobot
 */
public class ChestShopSign {

    public static final byte PRICE_LINE = 2;
    public static final Pattern[] SHOP_SIGN_PATTERN = { Pattern.compile("^?[\\w -.]*$"), Pattern.compile("^[1-9][0-9]{0,4}$"),
            Pattern.compile("(?i)^[\\d.bs(free) :]+$"), Pattern.compile("^[\\w? #:-]+$") };

    private static NamespacedKey METADATA_NAMESPACED_KEY;

    public static void createNamespacedKeys(ChestShop chestShop) {
        METADATA_NAMESPACED_KEY = new NamespacedKey(chestShop, "metadata");
    }

    public static boolean isAdminShop(Sign sign) {
        if (!isChestShop(sign)) {
            return false;
        }
        return getChestShopMetaData(sign).isOwner(NameManager.getAdminShopUUID());
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
        return getChestShopMetaData(sign).isOwner(player.getUniqueId());
    }

    public static UUID getOwner(Sign sign) {
        if (!isChestShop(sign)) {
            return null;
        }
        ChestShopMetaData chestShopMetaData = getChestShopMetaData(sign);
        return chestShopMetaData.getOwner();
    }


    public static void addAccessor(UUID player, Sign sign) {
        ChestShopMetaData chestShopMetaData = getChestShopMetaData(sign);
        chestShopMetaData.addAccessor(player);
        saveChestShopMetaData(sign, chestShopMetaData);
    }

    public static boolean isAccessor(OfflinePlayer player, Sign sign) {
        return isAccessor(player.getUniqueId(), sign);
    }

    public static boolean isAccessor(UUID player, Sign sign) {
        ChestShopMetaData chestShopMetaData = getChestShopMetaData(sign);
        return chestShopMetaData.isAccessor(player);
    }

    public static void removeAccessor(UUID accessor, Sign sign) {
        ChestShopMetaData chestShopMetaData = getChestShopMetaData(sign);
        chestShopMetaData.removeAccessor(accessor);
        saveChestShopMetaData(sign, chestShopMetaData);
    }

    private static void updateLegacyChestShop(Sign sign) {

        UUID ownerUUID = LegacyChestShopSign.getOwnerUUID(sign);

        int quantity = LegacyChestShopSign.getQuantity(sign);
        double buyPrice = LegacyChestShopSign.getBuyPrice(sign);
        double sellPrice = LegacyChestShopSign.getSellPrice(sign);

        ItemStack itemStack = LegacyChestShopSign.getItemStack(sign);

        ChestShopMetaData chestShopMetaData = new ChestShopMetaData(ownerUUID, quantity, sellPrice, buyPrice, itemStack);

        sign.setLine(0, NameManager.getFullNameFor(chestShopMetaData.getOwner()));
        sign.setLine(3, ItemChecker.getSignItemName(itemStack));

        saveChestShopMetaData(sign, chestShopMetaData);
    }

    private static boolean isLegacyChestShop(Sign sign) {

        if (!sign.getPersistentDataContainer().has(METADATA_NAMESPACED_KEY, PersistentDataType.STRING))
            return false;

        return LegacyChestShopSign.isValid(sign);
    }

    public static boolean isChestShop(Block block) {
        BlockState state = block.getState();
        if (!(state instanceof Sign))
            return false;

        return isChestShop((Sign) state);
    }

    public static boolean isChestShop(Sign sign) {

        if (isLegacyChestShop(sign)) {
            updateLegacyChestShop(sign);
            return true;
        }

        return sign.getPersistentDataContainer().has(METADATA_NAMESPACED_KEY, PersistentDataType.STRING);
    }

    public static ChestShopMetaData getChestShopMetaData(Sign sign) {

        try {

            String string = sign.getPersistentDataContainer().get(METADATA_NAMESPACED_KEY, PersistentDataType.STRING);
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.loadFromString(string);
            return (ChestShopMetaData) yamlConfiguration.get("metadata");

        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING,
                    "Exception loading Chestshop Metadata (" + sign.getX() + " " + sign.getY() + " " + sign.getZ() + ").", e);
            return null;
        }
    }

    public static void saveChestShopMetaData(Sign sign, ChestShopMetaData chestShopMetaData) {

        try {

            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.set("metadata", chestShopMetaData);

            String string = yamlConfiguration.saveToString();
            sign.getPersistentDataContainer().set(METADATA_NAMESPACED_KEY, PersistentDataType.STRING, string);
            sign.update();

        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING,
                    "Exception saving Chestshop Metadata (" + sign.getX() + " " + sign.getY() + " " + sign.getZ() + ").", e);
        }
    }

    public static boolean isValidPreparedSign(String[] lines) {
        for (int i = 0; i < 4; i++) {
            if (!SHOP_SIGN_PATTERN[i].matcher(lines[i]).matches()) {
                return false;
            }
        }
        return lines[PRICE_LINE].indexOf(':') == lines[PRICE_LINE].lastIndexOf(':');
    }

    public static boolean isAdminshopLine(String ownerLine) {
        return ownerLine.replace(" ", "").equalsIgnoreCase(Properties.ADMIN_SHOP_NAME.replace(" ", ""));
    }
}
