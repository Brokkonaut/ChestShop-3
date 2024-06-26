package com.Acrobot.ChestShop.Commands;

import static com.Acrobot.ChestShop.Permission.ADMIN;

import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import java.util.UUID;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class RemoveAccessor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        if (!(Permission.has(sender, Permission.SHOP_CREATION_BUY) || Permission.has(sender, Permission.SHOP_CREATION_SELL))) {
            sender.sendMessage(Messages.prefix(Messages.ACCESS_DENIED));
            return true;
        }

        if (args.length < 1) {
            return false;
        }

        Player player = (Player) sender;
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
        if (!ChestShopSign.isOwner(player, sign) && !Permission.has(player, ADMIN)) {
            sender.sendMessage(Messages.ACCESS_DENIED);
            return true;
        }

        String playerName = args[0];
        UUID accessorToRemove = NameManager.getUUIDForFullName(playerName);
        if (accessorToRemove == null) {
            sender.sendMessage(Messages.prefix(Messages.PLAYER_NOT_FOUND));
            return true;
        }

        if (!ChestShopSign.isAccessor(accessorToRemove, sign)) {
            sender.sendMessage(Messages.prefix(Messages.ACCESSOR_NOT_ADDED));
            return true;
        }

        ChestShopSign.removeAccessor(accessorToRemove, sign);
        sender.sendMessage(Messages.prefix(Messages.ACCESSOR_REMOVED));
        return true;
    }
}
