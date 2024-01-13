package com.Acrobot.ChestShop.Utils;

import com.Acrobot.Breeze.Utils.PotionNames;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.PreShopCreationItemDisplayNameEvent;
import com.Acrobot.ChestShop.ItemNaming.ChestShopItemDisplayNameShortener;
import com.Acrobot.ChestShop.ItemNaming.ItemDisplayNameShortener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.bukkit.inventory.meta.PotionMeta;

public class ItemNamingUtils {
    private static final ItemDisplayNameShortener CHEST_SHOP_ITEM_DISPLAY_NAME_SHORTENER = new ChestShopItemDisplayNameShortener();

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

        Material type = itemStack.getType();
        String itemName = StringUtil.capitalizeFirstLetter(type.name(), '_');
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof MusicInstrumentMeta musicInstrumentMeta) {
            MusicInstrument instrumentType = musicInstrumentMeta.getInstrument();
            if (instrumentType == null) {
                instrumentType = MusicInstrument.PONDER;
            }
            String instrument = StringUtil.capitalizeFirstLetter(instrumentType.getKey().getKey().replace("_goat_horn", ""));
            itemName = itemName + " " + instrument;
        } else if (itemMeta instanceof PotionMeta potionMeta) {
            itemName = PotionNames.getName(potionMeta.getBasePotionType());
            if (type == Material.SPLASH_POTION) {
                itemName = itemName.replace("Potion", "Splash Potion");
            }
            if (type == Material.LINGERING_POTION) {
                itemName = itemName.replace("Potion", "Lingering Potion");
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
        itemName = shortenDisplayName(itemName, length, itemDisplayNameShortener);

        return (needsItalicEffect ? ChatColor.ITALIC : "") + itemName.substring(0, Math.min(itemName.length(), length));
    }

    public static String shortenDisplayName(String name, int length) {
        return shortenDisplayName(name, length, null);
    }

    public static String shortenDisplayName(String name, int length, ItemDisplayNameShortener customItemDisplayNameShortener) {

        String result = name.toLowerCase();
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

        String[] split = result.split(" ");

        int remainingLetters = length;
        StringBuilder cutdown = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String currentWord = split[i];

            int remainingWords = split.length - i;
            int wordLength = remainingLetters / remainingWords;
            int rest = remainingLetters % remainingWords;

            int newLength = wordLength + (0 < rest ? 1 : 0);
            if (currentWord.length() < newLength) {
                int currentWordLength = currentWord.length();
                newLength = currentWordLength;
            }
            remainingLetters -= newLength;

            String substring = currentWord.substring(0, newLength);
            String capitalized = StringUtil.capitalizeFirstLetter(substring);
            cutdown.append(capitalized);
        }

        return cutdown.substring(0, Math.min(cutdown.length(), length));
    }
}
