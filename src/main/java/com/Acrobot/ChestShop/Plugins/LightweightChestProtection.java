package com.Acrobot.ChestShop.Plugins;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Events.Protection.ProtectBlockEvent;
import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;

/**
 * @author Acrobot
 */
public class LightweightChestProtection implements Listener {
    private LWC lwc;

    public LightweightChestProtection() {
        this.lwc = LWC.getInstance();
    }

    @EventHandler
    public static void onShopCreation(ShopCreatedEvent event) {
        Player player = event.getPlayer();
        Sign sign = event.getSign();
        Chest connectedChest = event.getChest();

        if (Properties.PROTECT_SIGN_WITH_LWC) {
            if (!Security.protect(player, sign.getBlock())) {
                player.sendMessage(Messages.prefix(Messages.NOT_ENOUGH_PROTECTIONS));
            }
        }

        if (Properties.PROTECT_CHEST_WITH_LWC && connectedChest != null && Security.protect(player, connectedChest.getBlock())) {
            player.sendMessage(Messages.prefix(Messages.PROTECTED_SHOP));
        }
    }

    @EventHandler
    public void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY) {
            return;
        }

        Block block = event.getBlock();
        Player player = event.getPlayer();

        Protection protection = lwc.findProtection(block);

        if (protection == null) {
            return;
        }

        try {
            if (!lwc.canAccessProtectionContents(player, protection)) {
                event.setResult(Event.Result.DENY);
            }
        } catch (NoSuchMethodError e) {
            // use old method as fallback
            if (protection.getType() == Protection.Type.DONATION ? !lwc.canAdminProtection(player, protection) : !lwc.canAccessProtection(player, protection)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @EventHandler
    public void onBlockProtect(ProtectBlockEvent event) {
        if (event.isProtected()) {
            return;
        }

        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (player == null) {
            return;
        }

        Protection existingProtection = lwc.findProtection(block);

        if (existingProtection != null) {
            event.setProtected(true);
            return;
        }

        LWCProtectionRegisterEvent protectionEvent = new LWCProtectionRegisterEvent(player, block);
        lwc.getModuleLoader().dispatchEvent(protectionEvent);

        if (protectionEvent.isCancelled()) {
            return;
        }

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        String worldName = block.getWorld().getName();
        Protection protection = lwc.getPhysicalDatabase().registerProtection(block.getType(), Protection.Type.PRIVATE, worldName, player.getUniqueId().toString(), "", x, y, z);

        if (protection != null) {
            event.setProtected(true);
        }
    }

    @EventHandler
    public void onShopRemove(ShopDestroyedEvent event) {
        Protection signProtection = lwc.findProtection(event.getSign().getBlock());

        if (signProtection != null) {
            signProtection.remove();
        }

        if (event.getChest() == null || !Properties.REMOVE_LWC_PROTECTION_AUTOMATICALLY) {
            return;
        }

        Protection chestProtection = lwc.findProtection(event.getChest().getBlock());

        if (chestProtection != null) {
            chestProtection.remove();
        }
    }
}
