package com.Acrobot.ChestShop.Plugins;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;

public class WorldGuardFlags {
    public static final StateFlag ALLOW_SHOP;
    public static final StateFlag USE_SHOP;

    static {
        StateFlag enableShop;
        try {
            StateFlag flag = new StateFlag("allow-shop", false);
            WorldGuard.getInstance().getFlagRegistry().register(flag);
            enableShop = flag;
        } catch (FlagConflictException | IllegalStateException e) {
            enableShop = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get("allow-shop");
        }
        ALLOW_SHOP = enableShop;
        StateFlag useShop;
        try {
            StateFlag flag = new StateFlag("use-shop", true);
            WorldGuard.getInstance().getFlagRegistry().register(flag);
            useShop = flag;
        } catch (FlagConflictException | IllegalStateException e) {
            useShop = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get("use-shop");
        }
        USE_SHOP = useShop;
    }
}
