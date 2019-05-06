package com.Acrobot.ChestShop.Plugins;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;

public class WorldGuardFlags {
    public static final StateFlag ENABLE_SHOP;

    static {
        StateFlag enableShop;
        try {
            StateFlag flag = new StateFlag("allow-shop", false);
            WorldGuard.getInstance().getFlagRegistry().register(flag);
            enableShop = flag;
        } catch (FlagConflictException | IllegalStateException e) {
            enableShop = (StateFlag) Flags.get("allow-shop");
        }
        ENABLE_SHOP = enableShop;
    }
}
