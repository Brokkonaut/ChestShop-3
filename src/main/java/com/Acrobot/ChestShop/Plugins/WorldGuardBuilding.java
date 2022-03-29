package com.Acrobot.ChestShop.Plugins;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.INVALID_SHOP;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.Protection.BuildPermissionEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;

/**
 * @author Acrobot
 */
public class WorldGuardBuilding implements Listener {
    private WorldGuardPlugin worldGuard;

    public WorldGuardBuilding(WorldGuardPlugin worldGuard) {
        this.worldGuard = worldGuard;
    }

    @EventHandler
    public void canBuild(BuildPermissionEvent event) {
        ApplicableRegionSet regions = getApplicableRegions(event.getSign().getBlock().getLocation());

        if (Properties.WORLDGUARD_USE_FLAG) {
            event.allow(regions.testState(worldGuard.wrapPlayer(event.getPlayer()), WorldGuardFlags.ALLOW_SHOP));
        } else {
            event.allow(regions.size() != 0);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void canUse(PreTransactionEvent event) {
        if (event.isCancelled()) {
            return;
        }

        ApplicableRegionSet regions = getApplicableRegions(event.getSign().getBlock().getLocation());
        if (!regions.testState(worldGuard.wrapPlayer(event.getClient()), WorldGuardFlags.USE_SHOP)) {
            event.setCancelled(INVALID_SHOP);
            return;
        }
    }

    private ApplicableRegionSet getApplicableRegions(Location location) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld())).getApplicableRegions(BukkitAdapter.asBlockVector(location));
    }
}
