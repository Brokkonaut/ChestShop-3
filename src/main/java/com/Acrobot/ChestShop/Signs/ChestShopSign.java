package com.Acrobot.ChestShop.Signs;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.ItemNaming.ItemNamingUtils;
import com.Acrobot.ChestShop.UUIDs.NameManager;
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

/**
 * @author Acrobot
 */
public class ChestShopSign {
    public static final byte NAME_LINE = 0;
    public static final byte QUANTITY_LINE = 1;
    public static final byte PRICE_LINE = 2;
    public static final byte ITEM_LINE = 3;

    public static final Pattern[] SHOP_SIGN_PATTERN = { Pattern.compile("^?[\\w -.]*$"), Pattern.compile("^[1-9][0-9]{0,4}$"),
            Pattern.compile("(?i)^[\\d.bs(free) :]+$") };

    private static NamespacedKey METADATA_NAMESPACED_KEY;

    public static void createNamespacedKeys(ChestShop chestShop) {
        METADATA_NAMESPACED_KEY = new NamespacedKey(chestShop, "metadata");
    }

    public static boolean isAdminShop(Sign sign) {
        if (!isChestShop(sign)) {
            return false;
        }
        return getChestShopMetaData(sign).isAdminshop();
    }

    public static boolean canAccess(OfflinePlayer player, Sign sign) {
        if (player == null) {
            return false;
        }
        if (sign == null) {
            return true;
        }

        if (isOwner(player, sign)) {
            return true;
        }

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

    public static ItemStack getItemStack(Sign sign) {
        if (!isChestShop(sign)) {
            return null;
        }
        ChestShopMetaData chestShopMetaData = getChestShopMetaData(sign);
        return chestShopMetaData.getItemStack();
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

    public static boolean isChestShop(Block block) {
        BlockState state = block.getState();
        if (!(state instanceof Sign sign)) {
            return false;
        }

        return isChestShop(sign);
    }

    public static boolean isChestShop(Sign sign) {
        return isChestShop(sign, false);
    }

    public static boolean isChestShop(Sign sign, boolean updateSign) {
        try {
            boolean isChestshop = sign.getPersistentDataContainer().has(METADATA_NAMESPACED_KEY, PersistentDataType.STRING);
            if (updateSign && isChestshop) {
                isChestshop = updateSignDisplay(sign);
            }

            return isChestshop;
        } catch (Exception e) {
            ChestShop.getBukkitLogger().log(Level.SEVERE, "Could not load shop info at " + sign.getLocation(), e);
            return false;
        }
    }

    private static boolean updateSignDisplay(Sign sign) {

        ChestShopMetaData chestShopMetaData = getChestShopMetaData(sign);
        if (chestShopMetaData == null || chestShopMetaData.getItemStack() == null) {
            return false;
        }
        UUID owner = chestShopMetaData.getOwner();
        String fullOwnerName = NameManager.getFullNameFor(owner);

        sign.setLine(NAME_LINE, fullOwnerName);
        sign.setLine(ITEM_LINE, ItemNamingUtils.getSignItemName(chestShopMetaData.getItemStack()));
        sign.update();
        return true;
    }

    public static ChestShopMetaData getChestShopMetaData(Sign sign) {
        try {
            String string = sign.getPersistentDataContainer().get(METADATA_NAMESPACED_KEY, PersistentDataType.STRING);
            if (string == null) {
                return null;
            }

            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.loadFromString(string);

            ChestShopMetaData metaData = (ChestShopMetaData) yamlConfiguration.get("metadata");
            if (metaData == null) {
                throw new NullPointerException("No metadata in:\n" + string);
            }
            if (metaData.getItemStack() == null) {
                ChestShop.getBukkitLogger().log(Level.WARNING, "No ItemStack found in Shop at: \n" + sign.getLocation());
                sign.getPersistentDataContainer().remove(METADATA_NAMESPACED_KEY);
                sign.update();
                return null;
            }
            if (metaData.shouldUpdate()) {
                saveChestShopMetaData(sign, metaData);
            }
            return metaData;
        } catch (Exception e) {
            ChestShop.getBukkitLogger().log(Level.SEVERE,
                    "Exception loading Chestshop Metadata (" + sign.getX() + " " + sign.getY() + " " + sign.getZ() + ").", e);
            return null;
        }
    }

    public static void saveChestShopMetaData(Sign sign, ChestShopMetaData chestShopMetaData) {
        saveChestShopMetaData(sign, chestShopMetaData, false);
    }

    public static void saveChestShopMetaData(Sign sign, ChestShopMetaData chestShopMetaData, boolean delayUpdate) {
        Runnable change = () -> {
            try {
                if (chestShopMetaData == null) {
                    sign.getPersistentDataContainer().remove(METADATA_NAMESPACED_KEY);
                } else {
                    YamlConfiguration yamlConfiguration = new YamlConfiguration();
                    yamlConfiguration.set("metadata", chestShopMetaData);

                    String string = yamlConfiguration.saveToString();
                    sign.getPersistentDataContainer().set(METADATA_NAMESPACED_KEY, PersistentDataType.STRING, string);
                }
                sign.update();

            } catch (Exception e) {
                ChestShop.getBukkitLogger().log(Level.WARNING,
                        "Exception saving Chestshop Metadata (" + sign.getX() + " " + sign.getY() + " " + sign.getZ() + ").", e);
            }
        };
        if (delayUpdate) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(ChestShop.getPlugin(), change, 1L);
        } else {
            change.run();
        }
    }

    public static boolean isValidPreparedSign(String[] lines) {
        for (int i = 0; i < 3; i++) {
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
