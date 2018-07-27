package com.Acrobot.ChestShop.Plugins;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;

/**
 * @author Acrobot
 */
public class WorldGuardProtection implements Listener {
    private WorldGuardPlugin worldGuard;

    public WorldGuardProtection(WorldGuardPlugin worldGuard) {
        this.worldGuard = worldGuard;
    }

    @EventHandler
    public void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY) {
            return;
        }

        Block block = event.getBlock();
        Player player = event.getPlayer();

        Location blockPos = BukkitAdapter.adapt(block.getLocation());
        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(block.getWorld()));
        ApplicableRegionSet set = manager.getApplicableRegions(blockPos.toVector());

        LocalPlayer localPlayer = worldGuard.wrapPlayer(player);

        if (!canAccess(localPlayer, block, set)) {
            event.setResult(Event.Result.DENY);
        }
    }

    private boolean canAccess(LocalPlayer player, Block block, ApplicableRegionSet set) {
        return hasBypass(player, block.getWorld()) || set.testState(player, Flags.BUILD) || set.testState(player, Flags.CHEST_ACCESS);
    }

    private boolean hasBypass(LocalPlayer wgPlayer, World world) {
        return new RegionPermissionModel(wgPlayer).mayIgnoreRegionProtection(BukkitAdapter.adapt(world));
    }
}
