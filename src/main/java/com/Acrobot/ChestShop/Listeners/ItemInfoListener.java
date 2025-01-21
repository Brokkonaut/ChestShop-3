package com.Acrobot.ChestShop.Listeners;

import static com.Acrobot.Breeze.Utils.NumberUtil.toRoman;
import static com.Acrobot.Breeze.Utils.NumberUtil.toTime;
import static com.Acrobot.Breeze.Utils.StringUtil.capitalizeFirstLetter;

import com.Acrobot.Breeze.Utils.EnchantmentNames;
import com.Acrobot.Breeze.Utils.FireworkEffectTypeNames;
import com.Acrobot.Breeze.Utils.PotionNames;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.Events.ItemInfoEvent;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Beehive;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.DecoratedPot;
import org.bukkit.block.DecoratedPot.Side;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.bukkit.inventory.meta.OminousBottleMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
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
            if (book.hasGeneration() && book.getGeneration() != Generation.ORIGINAL) {
                sender.sendMessage("    " + ChatColor.RED + capitalizeFirstLetter(book.getGeneration().name(), '_'));
            }
        }

        Map<Enchantment, Integer> enchantments = item.getEnchantments();
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            sender.sendMessage("    " + ChatColor.GRAY + EnchantmentNames.getName(enchantment.getKey()) + ((enchantment.getKey().getMaxLevel() > 1 || enchantment.getValue() != 1) ? (' ' + toRoman(enchantment.getValue())) : ""));
        }

        if (meta instanceof EnchantmentStorageMeta ench) {
            if (ench.hasStoredEnchants()) {
                for (Map.Entry<Enchantment, Integer> enchantment : ench.getStoredEnchants().entrySet()) {
                    sender.sendMessage("    " + ChatColor.GRAY + EnchantmentNames.getName(enchantment.getKey()) + ((enchantment.getKey().getMaxLevel() > 1 || enchantment.getValue() != 1) ? (' ' + toRoman(enchantment.getValue())) : ""));
                }
            }
        }

        if (meta instanceof FireworkMeta firework) {
            sender.sendMessage("    " + ChatColor.GRAY + "Flight Duration: " + (firework.getPower() == 0 ? 1 : firework.getPower()));
            for (FireworkEffect effect : firework.getEffects()) {
                sendFireworkEffect(sender, effect);
            }
        }

        if (meta instanceof FireworkEffectMeta fireworkEffect) {
            if (fireworkEffect.hasEffect()) {
                FireworkEffect effect = fireworkEffect.getEffect();
                sendFireworkEffect(sender, effect);
            }
        }

        if (meta instanceof SkullMeta skull) {
            if (skull.hasOwner()) {
                @SuppressWarnings("deprecation")
                String owner = skull.getOwner();
                if (owner != null) {
                    sender.sendMessage("    " + ChatColor.GRAY + "Skull Owner: " + owner);
                }
            }
        }

        if (meta instanceof PotionMeta potionMeta) {
            String baseItemName = StringUtil.capitalizeFirstLetter(type.name(), '_');
            String itemName = PotionNames.getName(potionMeta.getBasePotionType()).replace("Potion", baseItemName);
            sender.sendMessage("    " + ChatColor.GRAY + itemName);

            if (potionMeta.hasCustomEffects()) {
                for (PotionEffect effect : potionMeta.getCustomEffects()) {
                    sender.sendMessage("    " + ChatColor.GRAY + capitalizeFirstLetter(effect.getType().getName(), '_') + ' ' + toTime(effect.getDuration() / 20));
                }
            }
        }

        if (meta instanceof BlockStateMeta) {
            BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
            if (blockStateMeta.hasBlockState()) {
                BlockState blockState = blockStateMeta.getBlockState();
                if (blockState != null) {
                    if (blockState instanceof Container container) {
                        Inventory inv = container.getInventory();
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
                    if (blockState instanceof Beehive beehive) {
                        // int honeyLevel = ((org.bukkit.block.data.type.Beehive) beehive.getBlockData()).getHoneyLevel();
                        // sender.sendMessage(" " + ChatColor.GRAY + "Honey Level: " + honeyLevel);
                        int bees = beehive.getEntityCount();
                        sender.sendMessage("    " + ChatColor.GRAY + "Bees: " + bees);
                    }
                    if (blockState instanceof DecoratedPot pot) {
                        for (Entry<Side, Material> e : pot.getSherds().entrySet()) {
                            Material material = e.getValue();
                            if (material != null) {
                                Component c = Component.text("    ").color(NamedTextColor.GRAY);
                                c = c.append(Component.translatable(material.getItemTranslationKey()));
                                sender.sendMessage(c);
                            }
                        }
                    }
                }
            }
        }

        if (meta instanceof TropicalFishBucketMeta) {
            TropicalFishBucketMeta tropicalFishBucketMeta = (TropicalFishBucketMeta) meta;
            if (tropicalFishBucketMeta.hasVariant()) {
                String pattern = capitalizeFirstLetter(Objects.toString(tropicalFishBucketMeta.getPattern()), '_');
                String basecolor = capitalizeFirstLetter(Objects.toString(tropicalFishBucketMeta.getBodyColor()), '_');
                String patterncolor = capitalizeFirstLetter(Objects.toString(tropicalFishBucketMeta.getPatternColor()), '_');
                sender.sendMessage("    " + ChatColor.GRAY + "Variant: " + pattern + " " + basecolor + "/" + patterncolor);
            }
        }

        if (meta instanceof AxolotlBucketMeta axolotlBucketMeta) {
            if (axolotlBucketMeta.hasVariant()) {
                String variant = capitalizeFirstLetter(Objects.toString(axolotlBucketMeta.getVariant()), '_');
                sender.sendMessage("    " + ChatColor.GRAY + "Variant: " + variant);
            }
        }

        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta;
            Color color = leatherArmorMeta.getColor();
            if (color != null) {
                sender.sendMessage("    " + ChatColor.GRAY + "Color: " + getColorHexCode(color));
            }
        }

        if (meta instanceof ArmorMeta) {
            ArmorMeta armorMeta = (ArmorMeta) meta;
            if (armorMeta.hasTrim()) {
                ArmorTrim trim = armorMeta.getTrim();
                NamespacedKey materialKey = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getKey(trim.getMaterial());
                NamespacedKey patternKey = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).getKey(trim.getPattern());

                String material = materialKey != null ? materialKey.getKey().toLowerCase() : "Unknown Material";
                String trimPattern = patternKey != null ? capitalizeFirstLetter(patternKey.getKey()) : "Unknown Pattern";
                sender.sendMessage("    " + ChatColor.GRAY + "Trim: " + material + " " + trimPattern);
            }
        }

        if (meta instanceof CrossbowMeta crossbowMeta) {
            if (crossbowMeta.hasChargedProjectiles()) {
                for (ItemStack stack : crossbowMeta.getChargedProjectiles()) {
                    if (stack != null && !stack.getType().isAir()) {
                        String arrow = capitalizeFirstLetter(Objects.toString(stack.getType()), '_');

                        ItemMeta arrowMeta = stack.getItemMeta();
                        if (arrowMeta instanceof PotionMeta potion) {
                            StringBuilder message = new StringBuilder(50).append(" (");
                            String baseItemName = StringUtil.capitalizeFirstLetter(type.name(), '_');
                            String itemName = PotionNames.getName(potion.getBasePotionType()).replace("Potion", baseItemName);
                            message.append(itemName);
                            if (potion.hasCustomEffects()) {
                                for (PotionEffect effect : potion.getCustomEffects()) {
                                    message.append(", ");
                                    message.append(ChatColor.GRAY + capitalizeFirstLetter(effect.getType().getName(), '_') + ' ' + toTime(effect.getDuration() / 20));
                                }
                            }
                            message.append(")");
                            arrow = arrow + message.toString();
                        }
                        sender.sendMessage("    " + ChatColor.GRAY + "Projectile: " + arrow);
                    }
                }
            }
        }

        if (meta instanceof MusicInstrumentMeta musicInstrumentMeta) {
            MusicInstrument instrumentType = musicInstrumentMeta.getInstrument();
            if (instrumentType == null) {
                instrumentType = MusicInstrument.PONDER_GOAT_HORN;
            }
            sender.sendMessage(Component.text("    Instrument: ", NamedTextColor.GRAY).append(Component.translatable(instrumentType)));
        }

        if (meta != null && meta.hasLore() && !(meta instanceof BookMeta)) {
            List<String> lore = meta.getLore();
            if (lore != null && lore.size() > 0) {
                if (lore.size() == 1) {
                    sender.sendMessage("    " + ChatColor.GRAY + "Lore: " + lore.get(0));
                } else {
                    sender.sendMessage("    " + ChatColor.GRAY + "Lore:");
                    for (String loreLine : lore) {
                        sender.sendMessage("      " + ChatColor.GRAY + loreLine);
                    }
                }
            }
        }

        if (meta instanceof OminousBottleMeta ominousBottleMeta) {
            int amplifier = (ominousBottleMeta.hasAmplifier() ? ominousBottleMeta.getAmplifier() : 0) + 1;
            sender.sendMessage("    " + ChatColor.GRAY + "Bad Omen" + ((amplifier > 1) ? (' ' + toRoman(amplifier)) : ""));
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

    private static String getTwoCharacterColorHexCode(int color) {
        String s = Integer.toHexString(color).toUpperCase();
        if (s.length() < 2) {
            s = "0" + s;
        }
        return s;
    }

    private static String getColorHexCode(Color color) {
        String red = getTwoCharacterColorHexCode(color.getRed());
        String green = getTwoCharacterColorHexCode(color.getGreen());
        String blue = getTwoCharacterColorHexCode(color.getBlue());
        return "#" + red + green + blue;
    }
}
