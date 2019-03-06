package com.Acrobot.ChestShop.Listeners;

import static com.Acrobot.Breeze.Utils.NumberUtil.toRoman;
import static com.Acrobot.Breeze.Utils.NumberUtil.toTime;
import static com.Acrobot.Breeze.Utils.StringUtil.capitalizeFirstLetter;

import com.Acrobot.Breeze.Utils.EnchantmentNames;
import com.Acrobot.Breeze.Utils.FireworkEffectTypeNames;
import com.Acrobot.Breeze.Utils.PotionNames;
import com.Acrobot.ChestShop.Events.ItemInfoEvent;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

/**
 * @author Acrobot
 */
public class ItemInfoListener implements Listener {

    @EventHandler
    public static void addInfo(ItemInfoEvent event) {
        CommandSender sender = event.getSender();
        ItemStack item = event.getItem();
        Material type = item.getType();
        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName() && !(meta instanceof BookMeta)) {
            sender.sendMessage("    " + ChatColor.GRAY + "Name: " + meta.getDisplayName());
        }

        int maxdurability = type.getMaxDurability();
        if (maxdurability > 0 && meta instanceof Damageable) {
            int remainingDurability = maxdurability - ((Damageable) meta).getDamage();
            sender.sendMessage("    " + ChatColor.RED + "Durability: " + remainingDurability + "/" + maxdurability);
        }

        if (meta instanceof Repairable) {
            Repairable repairable = (Repairable) meta;
            if (repairable.hasRepairCost()) {
                sender.sendMessage("    " + ChatColor.RED + "Additional Repair Cost: " + repairable.getRepairCost());
            }
        }

        if (meta instanceof BookMeta) {
            BookMeta book = (BookMeta) meta;
            if (meta.hasDisplayName()) {
                sender.sendMessage("    " + ChatColor.GRAY + "Title: " + meta.getDisplayName());
            } else if (book.hasTitle()) {
                sender.sendMessage("    " + ChatColor.GRAY + "Title: " + book.getTitle());
            }
            if (book.hasAuthor()) {
                sender.sendMessage("    " + ChatColor.GRAY + "Author: " + book.getAuthor());
            }
            if (book.getGeneration() != Generation.ORIGINAL) {
                sender.sendMessage("    " + ChatColor.RED + capitalizeFirstLetter(book.getGeneration().name(), '_'));
            }
        }

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

        if (meta instanceof FireworkMeta) {
            FireworkMeta firework = (FireworkMeta) meta;
            sender.sendMessage("    " + ChatColor.GRAY + "Flight Duration: " + firework.getPower());
            for (FireworkEffect effect : firework.getEffects()) {
                sendFireworkEffect(sender, effect);
            }
        }

        if (meta instanceof FireworkEffectMeta) {
            FireworkEffectMeta fireworkEffect = (FireworkEffectMeta) meta;
            if (fireworkEffect.hasEffect()) {
                FireworkEffect effect = fireworkEffect.getEffect();
                sendFireworkEffect(sender, effect);
            }
        }

        if (meta instanceof SkullMeta) {
            SkullMeta skull = (SkullMeta) meta;
            if (skull.hasOwner()) {
                @SuppressWarnings("deprecation")
                String owner = skull.getOwner();
                if (owner != null) {
                    sender.sendMessage("    " + ChatColor.GRAY + "Skull Owner: " + owner);
                }
            }
        }

        if (meta instanceof PotionMeta) {
            PotionMeta potion = (PotionMeta) meta;
            StringBuilder message = new StringBuilder(50).append("    ").append(ChatColor.GRAY);
            PotionData base = potion.getBasePotionData();
            if (base != null) {
                if (base.isExtended()) {
                    message.append("Extended ");
                }
                if (type == Material.SPLASH_POTION) {
                    message.append("Splash ");
                }
                if (type == Material.LINGERING_POTION) {
                    message.append("Lingering ");
                }
                message.append(PotionNames.getName(base.getType())).append(' ');
                if (base.isUpgraded()) {
                    message.append("II ");
                }
                sender.sendMessage(message.toString());
            }
            if (potion.hasCustomEffects()) {
                for (PotionEffect effect : potion.getCustomEffects()) {
                    sender.sendMessage("    " + ChatColor.GRAY + capitalizeFirstLetter(effect.getType().getName(), '_') + ' ' + toTime(effect.getDuration() / 20));
                }
            }
        }

        if (meta instanceof BlockStateMeta) {
            BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
            if (blockStateMeta.hasBlockState()) {
                BlockState blockState = blockStateMeta.getBlockState();
                if (blockState != null) {
                    if (blockState instanceof Container) {
                        Inventory inv = ((Container) blockState).getInventory();
                        int stacks = 0;
                        for (ItemStack stack : inv.getContents()) {
                            if (stack != null && stack.getType() != Material.AIR) {
                                stacks++;
                            }
                        }
                        if (stacks > 0) {
                            sender.sendMessage("    " + ChatColor.GRAY + "Content: " + stacks + " Stacks");
                        }
                    }
                }
            }
        }
    }

    private static void sendFireworkEffect(CommandSender sender, FireworkEffect effect) {
        sender.sendMessage("    " + ChatColor.GRAY + FireworkEffectTypeNames.getName(effect.getType()));
        List<Color> colors = effect.getColors();
        if (colors != null && !colors.isEmpty()) {
            sender.sendMessage("      " + ChatColor.GRAY + colorListToString(colors));
        }
        colors = effect.getFadeColors();
        if (colors != null && !colors.isEmpty()) {
            sender.sendMessage("      " + ChatColor.GRAY + "Fade to " + colorListToString(colors));
        }
        if (effect.hasTrail()) {
            sender.sendMessage("      " + ChatColor.GRAY + "Trail");
        }
        if (effect.hasFlicker()) {
            sender.sendMessage("      " + ChatColor.GRAY + "Twinkle");
        }

    }

    private static String colorListToString(List<Color> colors) {
        StringBuilder sb = new StringBuilder();
        for (Color c : colors) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            DyeColor dyeColor = DyeColor.getByFireworkColor(c);
            sb.append(dyeColor != null ? capitalizeFirstLetter(dyeColor.name(), '_') : "Custom");
        }
        return sb.toString();
    }
}
