package com.Acrobot.ChestShop.Plugins;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.Protection.BuildPermissionEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;

/**
 * @author Acrobot
 */
public class WorldGuardBuilding implements Listener {
    @EventHandler
    public void canBuild(BuildPermissionEvent event) {
        ApplicableRegionSet regions = getApplicableRegions(event.getSign().getBlock().getLocation());

        if (Properties.WORLDGUARD_USE_FLAG) {
            event.allow(regions.testState(null, WorldGuardFlags.ENABLE_SHOP));
        } else {
            event.allow(regions.size() != 0);
        }
    }

    private ApplicableRegionSet getApplicableRegions(Location location) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld())).getApplicableRegions(BukkitAdapter.asVector(location));
    }
}
