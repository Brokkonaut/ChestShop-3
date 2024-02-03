package com.Acrobot.ChestShop.Commands;

import static com.Acrobot.ChestShop.Configuration.Messages.INCORRECT_ITEM_ID;
import static com.Acrobot.ChestShop.Configuration.Messages.ITEM_INFO;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.ItemInfoEvent;
import com.Acrobot.ChestShop.Utils.ItemNamingUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

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
            if (MaterialUtil.isEmpty(item)) {
                return false;
            }
        } else {
            item = MaterialUtil.getItem(StringUtil.joinArray(args));
        }

        showItemInfo(sender, item);

        return true;
    }

    public static void showItemInfo(CommandSender sender, ItemStack item) {
        sender.sendMessage(Messages.prefix(ITEM_INFO));
        if (MaterialUtil.isEmpty(item)) {
            sender.sendMessage("  " + ChatColor.DARK_RED + INCORRECT_ITEM_ID);
            return;
        }

        String displayName = ItemNamingUtils.getDisplayName(item);
        String fullName = ChatColor.stripColor(displayName);
        BaseComponent tc;

        if (StringUtil.capitalizeFirstLetter(item.getType().name(), '_').equals(fullName)) {
            // If Itemname is identical to Material name, no plugin "claimed" that item and changed the name
            // so we can do the player a favor and translate the item name to their Language.
            tc = new TranslatableComponent(item.getTranslationKey());
        } else {
            tc = new TextComponent(fullName);
        }
        tc.setColor(net.md_5.bungee.api.ChatColor.WHITE);
        sender.spigot().sendMessage(tc);

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
