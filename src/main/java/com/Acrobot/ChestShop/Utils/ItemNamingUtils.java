package com.Acrobot.ChestShop.Utils;

import com.Acrobot.Breeze.Utils.EnchantmentNames;
import com.Acrobot.Breeze.Utils.NumberUtil;
import com.Acrobot.Breeze.Utils.PotionNames;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.PreShopCreationItemDisplayNameEvent;
import com.Acrobot.ChestShop.ItemNaming.ChestShopEnchantedBookDisplayNameShortener;
import com.Acrobot.ChestShop.ItemNaming.ChestShopItemDisplayNameShortener;
import com.Acrobot.ChestShop.ItemNaming.ItemDisplayNameShortener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.bukkit.inventory.meta.PotionMeta;

public class ItemNamingUtils {
    private static final ItemDisplayNameShortener CHEST_SHOP_ITEM_DISPLAY_NAME_SHORTENER = new ChestShopItemDisplayNameShortener();
    private static final ItemDisplayNameShortener CHEST_SHOP_ENCHANTED_BOOK_DISPLAY_NAME_SHORTENER = new ChestShopEnchantedBookDisplayNameShortener();

    private static final HashMap<String, Material> materialNameToMaterial = new HashMap<>();
    static {
        try {
            for (Material m : Material.values()) {
                if (m.isItem()) {
                    String name = getSignItemName(new ItemStack(m));
                    materialNameToMaterial.putIfAbsent(name.toLowerCase(), m);
                }
            }
        } catch (ExceptionInInitializerError e) {
            // happens in a test environment because materials dont work without a registry
        }
    }

    public static Material getItemFromSignName(String name) {
        return materialNameToMaterial.get(name.toLowerCase());
    }

    public static String getSignItemName(ItemStack itemStack) {
        return getDisplayName(itemStack, 15);
    }

    public static String getDisplayName(ItemStack itemStack) {
        return getDisplayName(itemStack, Integer.MAX_VALUE);
    }

    public static String getDisplayName(ItemStack itemStack, int length) {
        if (itemStack == null) {
            return null;
        }
        ItemDisplayNameShortener extraShortener = null;
        Material type = itemStack.getType();
        String itemName = StringUtil.capitalizeFirstLetter(type.name(), '_');
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof MusicInstrumentMeta musicInstrumentMeta) {
            MusicInstrument instrumentType = musicInstrumentMeta.getInstrument();
            if (instrumentType == null) {
                instrumentType = MusicInstrument.PONDER_GOAT_HORN;
            }
            NamespacedKey instrumentKey = Registry.INSTRUMENT.getKey(instrumentType);
            String instrument = instrumentKey == null ? "(unknown instrument)" : StringUtil.capitalizeFirstLetter(instrumentKey.getKey().replace("_goat_horn", ""));
            itemName = itemName + " " + instrument;
        } else if (itemMeta instanceof PotionMeta potionMeta) {
            itemName = PotionNames.getName(potionMeta.getBasePotionType()).replace("Potion", itemName);
        } else if (itemMeta instanceof FireworkMeta fireworkMeta) {
            if (fireworkMeta.getEffectsSize() == 0) {
                itemName = "Rocket Strength " + (fireworkMeta.getPower() == 0 ? 1 : fireworkMeta.getPower());
            }
        } else if (itemMeta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            Map<Enchantment, Integer> enchants = enchantmentStorageMeta.getStoredEnchants();
            if (enchants.size() == 1) {
                Entry<Enchantment, Integer> enchantmentAndLevel = enchants.entrySet().iterator().next();
                Enchantment enchantment = enchantmentAndLevel.getKey();
                int level = enchantmentAndLevel.getValue();
                itemName = itemName + " " + StringUtil.capitalizeFirstLetter(EnchantmentNames.getName(enchantment)) + ((enchantment.getMaxLevel() > 1 || level != 1) ? (' ' + NumberUtil.toRoman(level)) : "");
                extraShortener = CHEST_SHOP_ENCHANTED_BOOK_DISPLAY_NAME_SHORTENER;
            } else {
                itemName = itemName + " " + enchants.size() + " Enchantments";
            }
        }

        boolean needsItalicEffect = false;

        if (itemStack.hasItemMeta()) {
            if (!itemMeta.equals(new ItemStack(itemStack.getType()).getItemMeta())) {
                needsItalicEffect = true;
            }
        }

        PreShopCreationItemDisplayNameEvent preShopCreationItemDisplayNameEvent = new PreShopCreationItemDisplayNameEvent(itemStack, itemName);
        ChestShop.callEvent(preShopCreationItemDisplayNameEvent);

        itemName = preShopCreationItemDisplayNameEvent.getDisplayName();
        ItemDisplayNameShortener itemDisplayNameShortener = preShopCreationItemDisplayNameEvent.getItemDisplayNameShortener();
        itemName = shortenDisplayName(itemName, length, itemDisplayNameShortener != null ? itemDisplayNameShortener : extraShortener);

        return (needsItalicEffect ? ChatColor.ITALIC : "") + itemName.substring(0, Math.min(itemName.length(), length));
    }

    public static String shortenDisplayName(String name, int length) {
        return shortenDisplayName(name, length, null);
    }

    public static String shortenDisplayName(String name, int length, ItemDisplayNameShortener customItemDisplayNameShortener) {

        String result = name; // name.toLowerCase();
        if (result.length() <= length) {
            return name;
        }

        if (customItemDisplayNameShortener != null) {
            result = customItemDisplayNameShortener.shorten(result, length);
            if (result.length() <= length) {
                return result;
            }
        }

        result = CHEST_SHOP_ITEM_DISPLAY_NAME_SHORTENER.shorten(result, length);
        if (result.length() <= length) {
            return result;
        }

        String[] words = result.split(" ");
        String[] shortenedWords = new String[words.length];

        int remainingLetters = length;
        int lettersPerWord = length / words.length;

        for (int i = 0; i < words.length; i++) {

            String word = words[i];
            int letters = Math.min(lettersPerWord, word.length());
            shortenedWords[i] = word.substring(0, letters);
            remainingLetters -= letters;
        }

        int currentWord = -1;
        while (remainingLetters > 0) {

            currentWord++;
            if (currentWord >= words.length) {
                currentWord = 0;
            }

            String word = words[currentWord];
            String shortened = shortenedWords[currentWord];
            if (word.equals(shortened)) {
                continue;
            }

            shortenedWords[currentWord] = shortened + word.charAt(shortened.length());
            remainingLetters--;
        }

        return String.join("", shortenedWords);
    }
}
