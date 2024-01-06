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

public class AddAccessor implements CommandExecutor {

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
        UUID newAccessor = NameManager.getUUIDFor(playerName);
        if (newAccessor == null) {
            sender.sendMessage(Messages.prefix(Messages.PLAYER_NOT_FOUND));
            return true;
        }

        UUID owner = ChestShopSign.getOwner(sign);
        if (owner.equals(newAccessor)) {
            sender.sendMessage(Messages.prefix(Messages.OWNER_CANT_BE_ACCESSOR));
            return true;
        }

        if (ChestShopSign.isAccessor(newAccessor, sign)) {
            sender.sendMessage(Messages.prefix(Messages.ACCESSOR_ALREADY_ADDED));
            return true;
        }

        ChestShopSign.addAccessor(newAccessor, sign);
        sender.sendMessage(Messages.prefix(Messages.NEW_ACCESSOR_ADDED));
        return true;
    }
}
