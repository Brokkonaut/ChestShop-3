package com.Acrobot.ChestShop.Commands;

import static com.Acrobot.ChestShop.Permission.ADMIN;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.util.RayTraceResult;

public class SetPrice implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (!Permission.has(sender, Permission.SET_PRICE_COMMAND)) {
            sender.sendMessage(Messages.ACCESS_DENIED);
            return true;
        }

        RayTraceResult result = player.rayTraceBlocks(8);
        Block signBlock = null;
        if (result != null) {
            signBlock = result.getHitBlock();
        }
        if (signBlock == null || !ChestShopSign.isChestShop(signBlock)) {
            sender.sendMessage(Messages.MUST_LOOK_AT_SHOP_SIGN);
            return true;
        }

        Sign sign = (Sign) signBlock.getState();
        if (!ChestShopSign.canAccess(player, sign) && !Permission.has(player, ADMIN)) {
            sender.sendMessage(Messages.ACCESS_DENIED);
            return true;
        }

        String newPriceLine = String.join(" ", args);
        String[] line = sign.getLines().clone();
        line[2] = newPriceLine;
        if (!ChestShopSign.isValidPreparedSign(line)) {
            sender.sendMessage(Messages.INVALID_PRICE_LINE);
            return true;
        }

        SignChangeEvent event = new SignChangeEvent(signBlock, player, line);
        ChestShop.getPlugin().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            sender.sendMessage(Messages.SHOP_UPDATE_FAILED);
            return true;
        }
        return true;
    }
}
