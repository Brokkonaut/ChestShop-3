package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_ITEM;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Utils.uBlock;

/**
 * @author Acrobot
 */
public class ItemChecker implements Listener {
    private static final String AUTOFILL_CODE = "?";

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        String itemCode = event.getSignLine(ITEM_LINE);
        ItemStack item = MaterialUtil.getItem(itemCode);

        if (item == null) {
            boolean foundItem = false;

            if (Properties.ALLOW_AUTO_ITEM_FILL && itemCode.equals(AUTOFILL_CODE) && uBlock.findConnectedChest(event.getSign()) != null) {
                for (ItemStack stack : uBlock.findConnectedChest(event.getSign(), true).getInventory().getContents()) {
                    if (!MaterialUtil.isEmpty(stack)) {
                        item = stack;
                        itemCode = MaterialUtil.getSignName(stack);

                        event.setSignLine(ITEM_LINE, itemCode);
                        foundItem = true;

                        break;
                    }
                }
            }

            if (!foundItem) {
                event.setOutcome(INVALID_ITEM);
                return;
            }
        }

        String metadata = getMetadata(itemCode);
        String itemName = MaterialUtil.getSignMaterialName(item.getType(), metadata);
        if (isSameItem(itemName + metadata, item)) {
            event.setSignLine(ITEM_LINE, itemName + metadata);
            return;
        }
        event.setOutcome(INVALID_ITEM);
    }

    private static boolean isSameItem(String newCode, ItemStack item) {
        ItemStack newItem = MaterialUtil.getItem(newCode);

        return newItem != null && newItem.isSimilar(item);
    }

    private static String getMetadata(String itemCode) {
        String metaCode = MaterialUtil.Metadata.getMetaCodeFromItemCode(itemCode);
        if (metaCode != null) {
            return "#" + metaCode;
        }
        return "";
    }
}
