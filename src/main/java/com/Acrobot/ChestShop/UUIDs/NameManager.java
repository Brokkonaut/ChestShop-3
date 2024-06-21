package com.Acrobot.ChestShop.UUIDs;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Lets you save/cache username and UUID relations
 *
 * @author Andrzej Pomirski (Acrobot)
 */
public class NameManager {
    private final static UUID adminShopUUID = UUID.nameUUIDFromBytes(("ChestShop-Adminshop").getBytes());
    private final static UUID serverAccountUUID = UUID.nameUUIDFromBytes(("ChestShop-ServerAccount").getBytes());

    public static String getNameFor(Player player) {
        return player.getName();
    }

    public static UUID getUUIDForFullName(String name) {
        if (ChestShopSign.isAdminshopLine(name)) {
            return adminShopUUID;
        }
        if (Properties.SERVER_ECONOMY_ACCOUNT != null && Properties.SERVER_ECONOMY_ACCOUNT.length() > 0
                && Properties.SERVER_ECONOMY_ACCOUNT.equals(name)) {
            return serverAccountUUID;
        }

        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayerIfCached(name);
        if (offlinePlayer != null) {
            return offlinePlayer.getUniqueId();
        }
        return null;
    }

    public static String getFullNameFor(UUID playerId) {
        if (isAdminShop(playerId)) {
            return Properties.ADMIN_SHOP_NAME;
        }
        if (isServerAccount(playerId)) {
            return Properties.SERVER_ECONOMY_ACCOUNT;
        }
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(playerId);
        return offlinePlayer.getName() != null ? offlinePlayer.getName() : playerId.toString();
    }

    public static boolean isAdminShop(UUID uuid) {
        return adminShopUUID.equals(uuid);
    }

    public static UUID getAdminShopUUID() {
        return adminShopUUID;
    }

    public static boolean isServerAccount(UUID uuid) {
        return serverAccountUUID.equals(uuid);
    }

    public static UUID getServerAccountUUID() {
        return serverAccountUUID;
    }
}
