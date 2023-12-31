package com.Acrobot.ChestShop.Signs;

import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.UUIDs.NameManager;

/**
 * @author Acrobot
 */
public class ChestShopSign {
    public static final String AUTOFILL_CODE = "?";
    public static final String AUTOFILL_SHULKER_CONTENT_CODE = "??";

    private static NamespacedKey METADATA_NAMESPACED_KEY;

    public static void createNamespacedKeys(ChestShop chestShop) {
        METADATA_NAMESPACED_KEY = new NamespacedKey(chestShop, "metadata");
    }

    public static void createChestShop(Sign sign, Player owner, int quantity, double sellPrice, double buyPrice, ItemStack itemStack) {

        ChestShopMetaData chestShopMetaData = new ChestShopMetaData(owner.getUniqueId(), quantity, sellPrice, buyPrice, itemStack);
        saveChestShopMetaData(sign, chestShopMetaData);
    }

    public static void createAdminChestShop(Sign sign, int quantity, double sellPrice, double buyPrice, ItemStack itemStack) {

        ChestShopMetaData chestShopMetaData = new ChestShopMetaData(NameManager.getAdminShopUUID(), quantity, sellPrice, buyPrice,
                itemStack);
        saveChestShopMetaData(sign, chestShopMetaData);
    }

    public static boolean isAdminShop(Sign sign) {
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
    }

    public static UUID getOwner(Sign sign) {
    }

    public static boolean isAccessor(OfflinePlayer player, Sign sign) {
    }

    public static void addAccessor(UUID player, Sign sign) {
    }

    public static boolean isAccessor(UUID player, Sign sign) {
    }

    public static void removeAccessor(UUID player, Sign sign) {
    }

    private static void updateLegacyChestShop(Sign sign) {

        UUID ownerUUID = LegacyChestShopSign.getOwnerUUID(sign);

        int quantity = LegacyChestShopSign.getQuantity(sign);
        double buyPrice = LegacyChestShopSign.getBuyPrice(sign);
        double sellPrice = LegacyChestShopSign.getSellPrice(sign);

        ItemStack itemStack = LegacyChestShopSign.getItemStack(sign);

        ChestShopMetaData chestShopMetaData = new ChestShopMetaData(ownerUUID, quantity, sellPrice, buyPrice, itemStack);
        PersistentDataContainer persistentDataContainer = sign.getPersistentDataContainer();

        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("metadata", chestShopMetaData);

        persistentDataContainer.set(METADATA_NAMESPACED_KEY, PersistentDataType.STRING, yamlConfiguration.saveToString());
    }

    private static boolean isLegacyChestShop(Sign sign) {

        if (!sign.getPersistentDataContainer().has(METADATA_NAMESPACED_KEY, PersistentDataType.STRING))
            return false;

        return LegacyChestShopSign.isValid(sign);
    }

    public static boolean isChestShop(Sign sign) {

        if (isLegacyChestShop(sign)) {
            updateLegacyChestShop(sign);
            return true;
        }

        return sign.getPersistentDataContainer().has(METADATA_NAMESPACED_KEY, PersistentDataType.STRING);
    }

    private static ChestShopMetaData getChestShopMetaData(Sign sign) {

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

    private static void saveChestShopMetaData(Sign sign, ChestShopMetaData chestShopMetaData) {

        try {

            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.set("metadata", chestShopMetaData);

            String string = yamlConfiguration.saveToString();
            sign.getPersistentDataContainer().set(METADATA_NAMESPACED_KEY, PersistentDataType.STRING, string);

        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING,
                    "Exception saving Chestshop Metadata (" + sign.getX() + " " + sign.getY() + " " + sign.getZ() + ").", e);
        }
    }
}
