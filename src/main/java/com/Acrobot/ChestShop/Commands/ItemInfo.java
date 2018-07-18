package com.Acrobot.ChestShop.Commands;

import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.ItemInfoEvent;

/**
 * @author Acrobot
 */
public class ItemInfo implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ItemStack item;

        if (args.length == 0) {
            if (!(sender instanceof HumanEntity)) {
                return false;
            }

            item = ((HumanEntity) sender).getInventory().getItemInMainHand();
        } else {
            item = MaterialUtil.getItem(StringUtil.joinArray(args));
        }

        if (MaterialUtil.isEmpty(item)) {
            return false;
        }

        showItemInfo(sender, item);

        return true;
    }

    public static void showItemInfo(CommandSender sender, ItemStack item) {
        sender.sendMessage(Messages.prefix(iteminfo));
        String name = StringUtil.capitalizeFirstLetter(item.getType().name(), '_');
        sender.sendMessage("  " + ChatColor.WHITE + name);

        ItemInfoEvent event = new ItemInfoEvent(sender, item);
        ChestShop.callEvent(event);
    }

    public static String getMetadata(ItemStack item) {
        if (!item.hasItemMeta()) {
            return "";
        }

        return ChatColor.GOLD + "#" + MaterialUtil.Metadata.getItemCode(item);
    }
}
