package com.Acrobot.ChestShop.Listeners;

import static com.Acrobot.Breeze.Utils.NumberUtil.toRoman;
import static com.Acrobot.Breeze.Utils.NumberUtil.toTime;
import static com.Acrobot.Breeze.Utils.StringUtil.capitalizeFirstLetter;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

import com.Acrobot.Breeze.Utils.EnchantmentNames;
import com.Acrobot.Breeze.Utils.PotionNames;
import com.Acrobot.ChestShop.Events.ItemInfoEvent;

/**
 * @author Acrobot
 */
public class ItemInfoListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void addDamage(ItemInfoEvent event) {
        ItemStack item = event.getItem();
        CommandSender sender = event.getSender();
        ItemMeta meta = item.getItemMeta();

        int maxdurability = item.getType().getMaxDurability();
        if (maxdurability > 0) {
            int remainingDurability = maxdurability - item.getDurability();
            sender.sendMessage("    " + ChatColor.RED + "Durability: " + remainingDurability + "/" + maxdurability);
        }

        if (meta instanceof Repairable) {
            Repairable repairable = (Repairable) meta;
            if (repairable.hasRepairCost()) {
                sender.sendMessage("    " + ChatColor.RED + "Additional Repair Cost: " + repairable.getRepairCost());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public static void addName(ItemInfoEvent event) {
        ItemStack item = event.getItem();
        CommandSender sender = event.getSender();
        ItemMeta meta = item.getItemMeta();

        if (meta.hasDisplayName()) {
            sender.sendMessage("    " + ChatColor.GRAY + "Name: " + meta.getDisplayName());
        }
    }

    @EventHandler
    public static void addEnchantment(ItemInfoEvent event) {
        ItemStack item = event.getItem();
        CommandSender sender = event.getSender();
        ItemMeta meta = item.getItemMeta();

        Map<Enchantment, Integer> enchantments = item.getEnchantments();
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            sender.sendMessage("    " + ChatColor.GRAY + EnchantmentNames.getName(enchantment.getKey()) + ' ' + toRoman(enchantment.getValue()));
        }

        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta ench = (EnchantmentStorageMeta) meta;
            if (ench.hasStoredEnchants()) {
                for (Map.Entry<Enchantment, Integer> enchantment : ench.getStoredEnchants().entrySet()) {
                    sender.sendMessage("    " + ChatColor.GRAY + EnchantmentNames.getName(enchantment.getKey()) + ' ' + toRoman(enchantment.getValue()));
                }
            }
        }
    }

    @EventHandler
    public static void addPotionInfo(ItemInfoEvent event) {
        ItemStack item = event.getItem();
        Material t = item.getType();
        if (t != Material.POTION && t != Material.SPLASH_POTION && t != Material.LINGERING_POTION && t != Material.TIPPED_ARROW) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof PotionMeta)) {
            return;
        }

        PotionMeta potion = (PotionMeta) meta;

        StringBuilder message = new StringBuilder(50);

        message.append(ChatColor.GRAY);

        PotionData base = potion.getBasePotionData();
        if (base == null) {
            return;
        }

        if (base.isExtended()) {
            message.append("Extended ");
        }
        if (t == Material.SPLASH_POTION) {
            message.append("Splash ");
        }
        if (t == Material.LINGERING_POTION) {
            message.append("Lingering ");
        }

        message.append(PotionNames.getName(base.getType())).append(' ');
        if (base.isUpgraded()) {
            message.append("II ");
        }
        CommandSender sender = event.getSender();

        sender.sendMessage("    " + message.toString());
        if (potion.hasCustomEffects()) {
            for (PotionEffect effect : potion.getCustomEffects()) {
                sender.sendMessage("    " + ChatColor.GRAY + capitalizeFirstLetter(effect.getType().getName(), '_') + ' ' + toTime(effect.getDuration() / 20));
            }
        }
    }
}
