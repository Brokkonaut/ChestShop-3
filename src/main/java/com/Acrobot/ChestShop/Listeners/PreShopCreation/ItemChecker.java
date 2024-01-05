package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_ITEM;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.PreShopCreationItemDisplayNameEvent;
import com.Acrobot.ChestShop.ItemNaming.ChestShopItemDisplayNameShortener;
import com.Acrobot.ChestShop.ItemNaming.ItemDisplayNameShortener;

/**
 * @author Acrobot
 */
public class ItemChecker implements Listener {

    private static final ItemDisplayNameShortener CHEST_SHOP_ITEM_DISPLAY_NAME_SHORTENER = new ChestShopItemDisplayNameShortener();

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {

        String signItemName = getSignItemName(event.getItemStack());
        if (signItemName == null) {
            event.setOutcome(INVALID_ITEM);
            return;
        }

        event.setItemLine(signItemName);
    }

    public static String getSignItemName(ItemStack itemStack) {
        return getDisplayName(itemStack, 15);
    }

    public static String getDisplayName(ItemStack itemStack, int length) {
        if (itemStack == null) {
            return null;
        }

        String itemName = MaterialUtil.getSignMaterialName(itemStack.getType());
        boolean needsItalicEffect = false;

        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (!itemMeta.equals(new ItemStack(itemStack.getType()).getItemMeta())) {
                needsItalicEffect = true;
            }
        }

        PreShopCreationItemDisplayNameEvent preShopCreationItemDisplayNameEvent = new PreShopCreationItemDisplayNameEvent(itemStack,
                itemName);
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

        String result = name;
        if (result.length() <= length)
            return result;

        if (customItemDisplayNameShortener != null) {
            result = customItemDisplayNameShortener.shorten(result, length);
            if (result.length() <= length)
                return result;
        }

        result = CHEST_SHOP_ITEM_DISPLAY_NAME_SHORTENER.shorten(result, length);
        if (result.length() <= length)
            return result;

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

        return cutdown.substring(0, length);
    }

}
