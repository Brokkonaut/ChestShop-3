package com.Acrobot.ChestShop.Commands;

import static com.Acrobot.ChestShop.Configuration.Messages.INCORRECT_ITEM_ID;
import static com.Acrobot.ChestShop.Configuration.Messages.ITEM_INFO;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.ItemInfoEvent;
import com.Acrobot.ChestShop.ItemNaming.ItemNamingUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

        if (!(sender instanceof HumanEntity)) {
            return false;
        }

        item = ((HumanEntity) sender).getInventory().getItemInMainHand();
        if (MaterialUtil.isEmpty(item)) {
            return false;
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

        TextComponent tc = new TextComponent("  ");
        String typeName = item.getType().name();
        if (StringUtil.capitalizeFirstLetter(typeName, '_').equals(fullName)) {
            // If Itemname is identical to Material name, no plugin "claimed" that item and changed the name
            // so we can do the player a favor and translate the item name to their Language.
            tc.addExtra(new TranslatableComponent(item.translationKey()));
            if (item.getType() == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) {
                // "item.minecraft.netherite_upgrade_smithing_template": "Smithing Template",
                // "upgrade.minecraft.netherite_upgrade": "Netherite Upgrade"
                tc.addExtra(" ");
                tc.addExtra(new TranslatableComponent("upgrade.minecraft.netherite_upgrade"));
            } else if (typeName.endsWith("_SMITHING_TEMPLATE")) {
                // "item.minecraft.bolt_armor_trim_smithing_template": "Smithing Template",
                // "trim_pattern.minecraft.bolt": "Bolt Armor Trim",
                String key = item.getType().getItemTranslationKey();
                key = key.replace("item.minecraft.", "").replace("_armor_trim_smithing_template", "");
                tc.addExtra(" ");
                tc.addExtra(new TranslatableComponent("trim_pattern.minecraft." + key));
            } else if (typeName.startsWith("MUSIC_DISC_") || typeName.endsWith("_BANNER_PATTERN")) {
                tc.addExtra(" ");
                tc.addExtra(new TranslatableComponent(item.getType().translationKey() + ".desc"));
            }
        } else {
            tc.addExtra(new TextComponent(fullName));
        }
        tc.setColor(net.md_5.bungee.api.ChatColor.WHITE);
        sender.spigot().sendMessage(tc);

        ItemInfoEvent event = new ItemInfoEvent(sender, item);
        ChestShop.callEvent(event);
    }
}
